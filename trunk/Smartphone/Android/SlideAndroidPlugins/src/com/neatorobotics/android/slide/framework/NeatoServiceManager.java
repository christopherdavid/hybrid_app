package com.neatorobotics.android.slide.framework;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiver;
import com.neatorobotics.android.slide.framework.service.INeatoRobotService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppService;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.AppUtils;

public class NeatoServiceManager {

    private static final String TAG = NeatoServiceManager.class.getSimpleName();

    private INeatoRobotService mNeatoRobotService;
    private boolean mServiceBound = false;
    private ResultReceiver mResultReciever;
    private Handler mHandler = new Handler();
    private Context mContext;
    private static NeatoServiceManager sNeatoServiceManager;
    private static final Object INSTANCE_LOCK = new Object();

    public static NeatoServiceManager getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (sNeatoServiceManager == null) {
                sNeatoServiceManager = new NeatoServiceManager(context);
            }
        }
        return sNeatoServiceManager;
    }

    private NeatoServiceManager(Context context) {
        mContext = context;
    }

    private ServiceConnection mNeatoRobotServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            LogHelper.logD(TAG, "onServiceDisconnected called");
            LogHelper.logD(TAG, "component Name = " + name);
            mServiceBound = false;
            mNeatoRobotService = null;
            ApplicationConfig.getInstance(mContext).setRobotService(null);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            LogHelper.log(TAG, "onServiceConnected called");
            mServiceBound = true;
            mNeatoRobotService = INeatoRobotService.Stub.asInterface(service);
            ApplicationConfig.getInstance(mContext).setRobotService(mNeatoRobotService);
            Intent resultReceiverIntent = new Intent(NeatoSmartAppService.NEATO_RESULT_RECEIVER_ACTION);
            resultReceiverIntent.putExtra(NeatoSmartAppService.EXTRA_RESULT_RECEIVER, mResultReciever);
            mContext.sendBroadcast(resultReceiverIntent);
            // Login to XMPP:
            RobotCommandServiceManager.initiateXmppConnection(mContext);
        }
    };

    public void bindNeatoService() {
        LogHelper.logD(TAG, "Initialise Neato Service from service manager");
        AppUtils.logApplicationVersion(mContext);
        
        mResultReciever = new NeatoRobotResultReceiver(mHandler);
        ApplicationConfig.getInstance(mContext).setRobotResultReceiver((NeatoRobotResultReceiver) mResultReciever);
        
        Intent serviceIntent = new Intent(mContext, NeatoSmartAppService.class);
        mContext.startService(serviceIntent);
        Intent bindServiceIntent = new Intent(mContext, NeatoSmartAppService.class);
        bindServiceIntent.putExtra(NeatoSmartAppService.EXTRA_RESULT_RECEIVER, mResultReciever);
        
        mContext.bindService(bindServiceIntent, mNeatoRobotServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindNeatoService() {
        if (mServiceBound) {
            RobotCommandServiceManager.cleanUpServiceConnections(mContext);
            mContext.unbindService(mNeatoRobotServiceConnection);
            mServiceBound = false;
        }
    }

}
