package com.neatorobotics.android.slide.framework.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class TcpIpTransport implements Transport {

    private static final String TAG = TcpIpTransport.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT = 15 * 1000;
    
    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private TcpIpTransport(Socket socket) {
        mSocket = socket;
        try {        	
            mInputStream = socket.getInputStream();            
        } catch (IOException e) {
            LogHelper.log(TAG, "Exception to getInputStream", e);
        }
        try {
            mOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            LogHelper.log(TAG, "Exception to getOutputStream", e);
        }
    }

    public static Transport createTransport(InetAddress remoteAddress, int port) {
        try {
            LogHelper.logD(TAG, "Create TCP Connection. Remote Address: " + remoteAddress.getHostAddress() + " Port: "
                    + port);            
            Socket socket = new Socket();            
            socket.connect(new InetSocketAddress(remoteAddress, port), CONNECTION_TIMEOUT);
            return new TcpIpTransport(socket);
        } catch (IOException e) {
            LogHelper.log(TAG, "Exception in createTransport", e);
        }
        return null;

    }

    public void send(byte[] data) throws IOException {
        if (mOutputStream != null) {
            mOutputStream.write(data);
        }
    }

    public void read(byte[] data) throws IOException {
        if (mInputStream != null) {
            mInputStream.read(data);
        }
    }

    public DatagramPacket readDatagram() throws IOException {
        return null;
    }

    public void close() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            LogHelper.log(TAG, "Exception in close", e);
        }
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public int getVersion() {
        return AppConstants.TCP_PACKET_VERSION;
    }

    public boolean isConnected() {
        if (mSocket != null && mSocket.isConnected()) {
            return true;
        }
        return false;

    }

}
