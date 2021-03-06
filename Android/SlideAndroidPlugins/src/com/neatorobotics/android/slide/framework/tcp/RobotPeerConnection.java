package com.neatorobotics.android.slide.framework.tcp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.Handler;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;

public class RobotPeerConnection {

    private static final String TAG = RobotPeerConnection.class.getSimpleName();

    private static final int TCP_ROBOT_SERVER_PORT = 49001;
    private static final int TCP_RECONNECT_RETRY_COUNT = 2;
    private static final int TCP_RETRY_TIME_GAP = 300; // 300 milli seconds.

    private static final int TCP_BREAK_CONNECTION_WAIT_TIME = 200; // 100 milli
                                                                   // seconds.

    @SuppressWarnings("unused")
	private Context mContext;
    private Handler mHandler;

    private RobotPeerDataListener mRobotPeerDataListener;
    private RobotConnectionInfo mRobotConnectionInfo;
    private Object mRobotConnectionInfoLock = new Object();
    private WriteDataThread mWriteDataThread;
    private ReadDataThread mReadDataThread;

    public RobotPeerConnection(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setPeerDataListener(RobotPeerDataListener robotPeerDataListener) {
        mRobotPeerDataListener = robotPeerDataListener;
    }

    private void notifyRobotConnected(final String robotId) {
        if (mRobotPeerDataListener != null) {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    public void run() {
                        LogHelper.log(TAG, "Robot is connected peer-to-peer");
                        mRobotPeerDataListener.onConnect(robotId);
                    }
                });
            } else {
                mRobotPeerDataListener.onConnect(robotId);
            }
        }
    }

    private void notifyRobotDisconnected(final String robotId) {
        if (mRobotPeerDataListener != null) {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    public void run() {
                        mRobotPeerDataListener.onDisconnect(robotId);
                    }
                });
            } else {
                mRobotPeerDataListener.onDisconnect(robotId);
            }
        }
    }

    private void notifyPacketReceived(final String robotId, final RobotCommandPacket robotCommandPacket) {
        if (mRobotPeerDataListener == null) {
            return;
        }

        if (mHandler != null) {
            mHandler.post(new Runnable() {
                public void run() {
                    mRobotPeerDataListener.onDataReceived(robotId, robotCommandPacket);
                }
            });
        } else {
            mRobotPeerDataListener.onDataReceived(robotId, robotCommandPacket);
        }

    }

    public void connectToRobot(final String robotId, final String robotIpAddress) {
        LogHelper.logD(TAG, "connectToRobot called");
        final int peerPort = TCP_ROBOT_SERVER_PORT;
        Runnable task = new Runnable() {
            public void run() {
                // As we are supporting only one TCP connection, break the
                // exisiting conneciton if any
                closeExistingConnectionInternal();
                connectToRobotInternal(robotId, robotIpAddress, peerPort);
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    private void connectToRobotInternal(String robotId, String ipAddress, int port) {
        LogHelper.log(TAG, "connectToRobot internal called");
        InetAddress peerAddress = TcpUtils.getInetAddressFromIp(ipAddress);
        LogHelper.logD(TAG, "Robot TCP IP Address = " + peerAddress);
        Transport transport = null;
        for (int i = 0; i < TCP_RECONNECT_RETRY_COUNT; i++) {
            transport = TransportFactory.createTransport(peerAddress, port);
            if (transport != null) {
                LogHelper.logD(TAG, "transport = " + transport);
                RobotConnectionInfo robotConnectionInfo = createRobotConnectionInfo(robotId, ipAddress, transport);
                setConnectionRobotInfo(robotConnectionInfo);
                startReadDataThreadForRobot(robotConnectionInfo);
                startWriteDataThreadForRobot(robotConnectionInfo);
                break;
            } else {
                closeExistingConnectionInternal();
                TaskUtils.sleep(TCP_RETRY_TIME_GAP);
            }
        }

        if (transport == null) {
            LogHelper.log(TAG, "Could not connect to peer. Try again.");
            if (mRobotPeerDataListener != null) {
                mRobotPeerDataListener.errorInConnecting(robotId);
            }
        }
    }

    private void startReadDataThreadForRobot(RobotConnectionInfo robotConnectionInfo) {
        if ((robotConnectionInfo != null) && (robotConnectionInfo.getTransport() != null)) {
            InputStream transportInputStream = robotConnectionInfo.getTransport().getInputStream();
            if (transportInputStream != null) {
                mReadDataThread = new ReadDataThread(robotConnectionInfo);
                mReadDataThread.start();
            } else {
                LogHelper.log(TAG, "startReadDataThreadForRobot - Could not get input stream for the socket. Try again.");
            }
        }
    }

    private void startWriteDataThreadForRobot(RobotConnectionInfo robotConnectionInfo) {
        if ((robotConnectionInfo != null) && (robotConnectionInfo.getTransport() != null)) {            
        	mWriteDataThread = new WriteDataThread(robotConnectionInfo.getTransport());           
        	mWriteDataThread.start();
        } else {
            LogHelper.log(TAG, "startWriteDataThreadForRobot - Could not get RobotConnectionInfo for the robot. Try again.");
        }
    }

    public void closeExistingPeerConnection() {
        closeExistingConnectionInternal();
    }
	
    private class ReadDataThread extends Thread {

        private InputStream mIs;
        private RobotConnectionInfo connectionInfo;
        private Transport mTransport;
        private String mRobotId;

        // No need to send input stream. It can be very well retrieved from the
        // transport. This is just to make sure input streaM
        // exists for ther transport before making an attempt to connect.
        public ReadDataThread(RobotConnectionInfo robotConnectionInfo) {
            connectionInfo = robotConnectionInfo;
        }

        @Override
        public void run() {
            try {            	
                mTransport = connectionInfo.getTransport();
                mIs = mTransport.getInputStream();
                mRobotId = connectionInfo.getRobotId();
                notifyRobotConnected(mRobotId);
                DataInputStream din = new DataInputStream(mIs);
                while (true) {
                    RobotCommandPacket commandPacket = null;
                    try {
                        if (mTransport != null && mTransport.isConnected()) {
                            int signature = readInt(din);
                            LogHelper.log(TAG, "received signature:" + signature + " expected signature : "
                                    + RobotCommandPacketConstants.COMMAND_PACKET_SIGNATURE);

                            if (signature != RobotCommandPacketConstants.COMMAND_PACKET_SIGNATURE) {
                                LogHelper.log(TAG, "****ERROR***** - Signature mismatch");
                                mTransport.close();
                                break;
                            }

                            int version = readInt(din);
                            LogHelper.log(TAG, "received version:" + version + " expected version : "
                                    + RobotCommandPacketConstants.COMMAND_PACKET_VERSION);

                            if (version != RobotCommandPacketConstants.COMMAND_PACKET_VERSION) {
                                LogHelper.log(TAG, "****ERROR***** - version mismatch");
                                mTransport.close();
                                break;
                            }
                            commandPacket = RobotPeerConnectionUtils.readPacket(din);
                        } else {
                            LogHelper.log(TAG, "Peer is not connected");
                            break;
                        }
                    }
                    catch (EOFException e) {
                        LogHelper.log(TAG, "EOFException in ReadDataThread", e);
                        break;
                    } catch (IOException e) {
                        LogHelper.log(TAG, "IOException in ReadDataThread", e);
                        break;
                    }
                    if (commandPacket != null) {
                        notifyPacketReceived(mRobotId, commandPacket);
                    } else {
                        LogHelper.log(TAG, "Null packet received");
                    }
                }
            }
            finally {
                LogHelper.logD(TAG, "Connected Thread end");
                notifyRobotDisconnected(mRobotId);
                deleteConnectionInfo();                
            }
        }
        
        public void close() {      
        	closeSilently(mIs);
        }
    }
	
    private static void closeSilently(InputStream is) {
    	if (is != null) {
    		try {
    			is.close();
    		}
    		catch (IOException e) {
    			// ignore
    		}
    	}
    }
    
	// Amit
    private class WriteDataThread extends Thread {    	
    	private final LinkedBlockingQueue<RobotCommandPacket> mMessageQueue;
    	private final Transport mTransport;    	
    	
    	public WriteDataThread(Transport transport) {
    		mTransport = transport;
    		mMessageQueue = new LinkedBlockingQueue<RobotCommandPacket>();
    	}
    	
    	public void sendPacket(RobotCommandPacket packet) {
    		if(packet != null) {    			
    			mMessageQueue.add(packet);
    		}
    	}
    	
		@Override
		public void run() {
			while (true) {
				try {
					RobotCommandPacket packet = mMessageQueue.take();					
					RobotPeerConnectionUtils.sendRobotPacket(mTransport, packet);
				}
				catch (InterruptedException e) {
					 LogHelper.log(TAG, "InterruptedException in WriteDataThread");
					 break;
				}				
			}
		}
		
		public void close() {			
			interrupt();
	    }
    }
    
    public void closePeerRobotConnection(final String robotId) {
        Runnable task = new Runnable() {
            public void run() {
                if (isPeerRobotConnected(robotId)) {
                    closeExistingConnectionInternal();
                } else {
                    LogHelper.logD(TAG, "Cannot close connection as, Robot is not peer-connected: " + robotId);
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    private void closeExistingConnectionInternal() {
        RobotConnectionInfo robotConnection = getConnectedRobotInfo();
        if (robotConnection != null) {
            Transport transport = robotConnection.getTransport();
            if (transport != null) {
                if (transport.isConnected()) {
                    LogHelper.log(TAG, "closePeerConnection called");
                    String robot = robotConnection.getRobotId();
                    sendConnectionBreakPacket(robot);
                    
                    // Close write data thread
                    if(mWriteDataThread != null) {
                    	mWriteDataThread.close();
                    }
                    mWriteDataThread = null;
                    
                    // Close read data thread
                    if(mReadDataThread != null) {
                    	mReadDataThread.close();
                    }
                    mReadDataThread = null;
                    
                    TaskUtils.sleep(TCP_BREAK_CONNECTION_WAIT_TIME);
                }
                transport.close();
                transport = null;
            }
            deleteConnectionInfo();
            notifyRobotDisconnected("");
        }
    }

    public boolean isConnectedAndPingRobot(String robotId) {
        LogHelper.logD(TAG, "isConnectedAndPingRobot transport: " + robotId);
        boolean isConnected = false;
        RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
        if (robotConnectionInfo == null) {
            LogHelper.log(TAG, "RobotConnectionInfo for robotId does not exist");
            return false;
        }

        Transport transport = robotConnectionInfo.getTransport();
        if (transport != null) {
            isConnected = transport.isConnected();
            if ((isConnected)) {
                LogHelper.logD(TAG, "Send ping packet: " + robotId);
                sendConnectionPingPacket(robotId);
            }
        }

        return isConnected;
    }

    public boolean isConnectedAndSendPingPacket() {
        RobotConnectionInfo robotConnectionInfo = getConnectedRobotInfo();
        if (robotConnectionInfo == null) {
            LogHelper.log(TAG, "RobotConnectionInfo for robotId does not exist");
            return false;
        }
        boolean isConnected = false;
        Transport transport = robotConnectionInfo.getTransport();
        isConnected = false;
        if (transport != null) {
            isConnected = transport.isConnected();
            if (isConnected) {
                String robotId = robotConnectionInfo.mRobotId;
                LogHelper.logD(TAG, "Send ping packet: " + robotId);
                sendConnectionPingPacket(robotId);
            }
        }
        return isConnected;
    }

    public boolean isPeerRobotConnected(String robotId) {
        LogHelper.logD(TAG, "isPeerRobotConnected transport: " + robotId);
        boolean isConnected = false;
        RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
        if (robotConnectionInfo == null) {
            LogHelper.log(TAG, "RobotConnectionInfo for robotId does not exist");
            return false;
        }

        Transport transport = robotConnectionInfo.getTransport();
        if (transport != null) {
            isConnected = transport.isConnected();
        }
        return isConnected;
    }

    public boolean isAnyPeerConnectionExists(boolean sendPingPacket) {
        RobotConnectionInfo robotConnectionInfo = getConnectedRobotInfo();
        boolean isConnected = false;
        if (robotConnectionInfo != null) {
            Transport transport = robotConnectionInfo.getTransport();
            isConnected = false;
            if (transport != null) {
                isConnected = transport.isConnected();
                if ((isConnected) && (sendPingPacket)) {
                    String robotId = robotConnectionInfo.mRobotId;
                    LogHelper.logD(TAG, "Send ping packet: " + robotId);
                    sendConnectionPingPacket(robotId);
                }
            } else {
                LogHelper.log(TAG, "trasport for robotId is null");
            }
        }
        return isConnected;
    }

    public void sendRobotCommand(String robotId, RobotCommandPacket robotPacket) {
        RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
        if ((robotConnectionInfo != null) && (robotConnectionInfo.getTransport() != null)) {            
            LogHelper.log(TAG, "Connection exist. Sending command.");            
            if(mWriteDataThread == null) {
            	mWriteDataThread = new WriteDataThread(robotConnectionInfo.getTransport());
            }            
            
            mWriteDataThread.sendPacket(robotPacket);
        } else {
            LogHelper.log(TAG, "Connection does not exist.");
        }
    }

    public void sendConnectionPingPacket(String robotId) {
        //RobotCommandPacket packet = RobotPeerConnectionUtils.getConnectionPingPacket(mContext);
        //sendRobotCommand(robotId, packet);
    }

    public void sendConnectionBreakPacket(String robotId) {    	
        //RobotCommandPacket packet = RobotPeerConnectionUtils.getConnectionBreakPacket(mContext);
        //sendRobotCommand(robotId, packet);
    }

    private int readInt(DataInputStream din) throws IOException {
        int data = din.readInt();
        return data;
    }

    private RobotConnectionInfo getConnectedRobotInfo() {
        synchronized (mRobotConnectionInfoLock) {
            LogHelper.log(TAG, "getConnectedRobotInfo called. mRobotConnectionInfo = " + mRobotConnectionInfo);
            return mRobotConnectionInfo;
        }

    }

    private void setConnectionRobotInfo(RobotConnectionInfo robotConnectionInfo) {
        LogHelper.log(TAG, "setConnectionRobotInfo called. robotConnectionInfo = " + robotConnectionInfo);
        synchronized (mRobotConnectionInfoLock) {
            mRobotConnectionInfo = robotConnectionInfo;
        }
    }

    private RobotConnectionInfo createRobotConnectionInfo(String robotId, String ipAddress, Transport transport) {
        RobotConnectionInfo robotConnectionInfo = new RobotConnectionInfo(robotId, ipAddress, transport);
        return robotConnectionInfo;
    }

    private RobotConnectionInfo getRobotConnectionInfo(String robotId) {
        LogHelper.log(TAG, "getRobotConnectionInfo called");
        LogHelper.log(TAG, "mRobotConnectionInfo = " + mRobotConnectionInfo);
        if (mRobotConnectionInfo == null) {
            LogHelper.log(TAG, "RobotConnectionInfo does not exist for robotId:" + robotId);
            return null;
        }
        return mRobotConnectionInfo.getRobotId().equalsIgnoreCase(robotId) ? mRobotConnectionInfo : null;
    }

    private void deleteConnectionInfo() {
        LogHelper.log(TAG, "Deleting Peer robot connection info");
        if (mRobotConnectionInfo != null) {
            mRobotConnectionInfo.setIpAddress(null);
            mRobotConnectionInfo.setRobotId(null);
            mRobotConnectionInfo.setTransport(null);
        }
        setConnectionRobotInfo(null);
    }

    private class RobotConnectionInfo {
        private String mIpAddress;
        private String mRobotId;
        private Transport mTransport;

        public RobotConnectionInfo(String robotId, String ipAddress, Transport transport) {
            mRobotId = robotId;
            mIpAddress = ipAddress;
            mTransport = transport;
        }

        public String getRobotId() {
            return mRobotId;
        }

        @SuppressWarnings("unused")
        public String getIpAddress() {
            return mIpAddress;
        }

        public Transport getTransport() {
            return mTransport;
        }

        public void setRobotId(String robotId) {
            mRobotId = robotId;
        }

        public void setIpAddress(String ipAddress) {
            mIpAddress = ipAddress;
        }

        public void setTransport(Transport transport) {
            mTransport = transport;
        }
    }
}
