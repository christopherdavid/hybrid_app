package com.neatorobotics.android.slide.framework.webservice.robot;

import java.io.IOException;
import java.util.HashMap;
import android.content.Context;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileDataUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotManager {
    private static final String TAG = RobotManager.class.getSimpleName();
    private Context mContext;

    private static RobotManager sRobotManager;
    private static final Object INSTANCE_LOCK = new Object();

    private RobotManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static RobotManager getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (sRobotManager == null) {
                sRobotManager = new RobotManager(context);
            }
        }

        return sRobotManager;
    }

    public void getRobotDetail(final String serialId, final WebServiceBaseRequestListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    RobotDetailResult result = NeatoRobotWebservicesHelper.getRobotDetail(mContext, serialId);

                    // Update the robot data if anything changed
                    RobotHelper.saveRobotDetails(mContext, result.result);

                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public RobotItem getRobotDetail(final String serialId) {
        RobotItem robotItem = null;
        try {
            RobotDetailResult result = NeatoRobotWebservicesHelper.getRobotDetail(mContext, serialId);
            robotItem = result.result;
        } catch (UserUnauthorizedException ex) {
            LogHelper.log(TAG, "UserUnauthorizedException in getRobotDetail - " + ex.getErrorMessage());
        } catch (NeatoServerException ex) {
            LogHelper.log(TAG, "NeatoServerException in getRobotDetail - " + ex.getErrorMessage());
        } catch (IOException ex) {
            LogHelper.log(TAG, "IOException in getRobotDetail - " + ex);
        }

        return robotItem;
    }

    public RobotItem getRobotDetailAndSave(final String serialId) {
        RobotItem robotItem = null;
        try {
            RobotDetailResult result = NeatoRobotWebservicesHelper.getRobotDetail(mContext, serialId);
            robotItem = result.result;
            RobotHelper.saveRobotDetails(mContext, robotItem);
            RobotHelper.setRobotToManage(mContext, robotItem);
            LogHelper.log(TAG, "Saving robot information");
        } catch (UserUnauthorizedException ex) {
            LogHelper.log(TAG, "UserUnauthorizedException in getRobotDetailAndSave - " + ex.getErrorMessage());
        } catch (NeatoServerException ex) {
            LogHelper.log(TAG, "NeatoServerException in getRobotDetailAndSave - " + ex.getErrorMessage());
        } catch (IOException ex) {
            LogHelper.log(TAG, "IOException in getRobotDetailAndSave - " + ex);
        }

        return robotItem;
    }

    public void setRobotName(final String robotId, final String robotName, final WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "setRobotName called");
        LogHelper.logD(TAG, "Robot Id = " + robotId + " New Name = " + robotName);

        Runnable task = new Runnable() {
            public void run() {
                try {
                    HashMap<String, String> profileParams = new HashMap<String, String>();
                    profileParams.put(ProfileAttributeKeys.ROBOT_NAME, robotName);
                    SetRobotProfileDetailsResult3 result = NeatoRobotDataWebservicesHelper
                            .setRobotProfileDetailsRequest3(mContext, robotId, profileParams);

                    // Robot Name updated on the server, we now update the name
                    // in the database
                    RobotHelper.updateRobotName(mContext, robotId, robotName);
                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public void getRobotOnlineStatus(final String robotId, final WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "getRobotOnlineStatus called for RobotID = " + robotId);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    RobotOnlineStatusResult result = NeatoRobotWebservicesHelper
                            .getRobotOnlineStatus(mContext, robotId);
                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };

        TaskUtils.scheduleTask(task, 0);
    }

    public void getRobotVirtualOnlineStatus(final String robotId, final WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "getRobotVirtualOnlineStatus called for RobotID = " + robotId);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    RobotVirtualOnlineStatusResult result = NeatoRobotWebservicesHelper.getRobotVirtualOnlineStatus(
                            mContext, robotId);
                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };

        TaskUtils.scheduleTask(task, 0);
    }

    public void getRobotCleaningState(final Context context, final String robotId,
            final WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "getRobotCleaningState called for RobotID = " + robotId);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    GetRobotProfileDetailsResult2 result = NeatoRobotDataWebservicesHelper
                            .getRobotProfileDetailsRequest2(mContext, robotId, "");

                    RobotProfileDataUtils.updateDataTimestampIfChanged(context, result, robotId,
                            ProfileAttributeKeys.ROBOT_CURRENT_STATE);
                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };

        TaskUtils.scheduleTask(task, 0);
    }

    public void getRobotCleaningStateDetails(final Context context, final String robotId,
            final WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "getRobotCleaningStateDetails called for RobotID = " + robotId);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    GetRobotProfileDetailsResult2 result = NeatoRobotDataWebservicesHelper
                            .getRobotProfileDetailsRequest2(mContext, robotId, "");
                    RobotProfileDataUtils.updateDataTimestampIfChanged(context, result, robotId,
                            ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS);
                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };

        TaskUtils.scheduleTask(task, 0);
    }

    public void clearRobotData(final String email, final String robotId, final WebServiceBaseRequestListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    RobotClearDataResult result = NeatoRobotWebservicesHelper.clearRobotDataRequest(mContext, email,
                            robotId);
                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }
}
