package com.neatorobotics.android.slide.framework.database;

import android.content.Context;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleInfo;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleJsonHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.Schedules;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes;

public class ScheduleHelper {

    private static final String TAG = ScheduleHelper.class.getSimpleName();

    public static ScheduleInfo getScheduleInfoById(Context context, String id) {
        ScheduleInfo scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
        return scheduleInfo;
    }

    public static Schedules getScheduleGroupById(Context context, String id) {
        ScheduleInfo scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
        if ((scheduleInfo != null)
                && (scheduleInfo.getScheduleType().equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC))) {
            return ScheduleJsonHelper.readJsonToBasicSchedule(scheduleInfo.getScheduleData());
        } else {
            // TODO: Add support for Advance schedule
        }

        return null;
    }

    public static void saveScheduleId(Context context, String robotId, String id, int type) {
        DBHelper.getInstance(context).saveBasicScheduleId(robotId, id);
    }

    public static void saveScheduleInfo(Context context, String id, String serverId, String scheduleVersion,
            String scheduleType, String data) {
        DBHelper.getInstance(context).saveScheduleInfo(id, serverId, scheduleVersion, scheduleType, data);
    }

    public static boolean updateScheduleVersion(Context context, String id, String scheduleVersion) {
        LogHelper.logD(TAG, "updateScheduleVersion");
        ScheduleInfo scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
        if (scheduleInfo != null) {
            scheduleInfo.setDataVersion(scheduleVersion);
            return DBHelper.getInstance(context).updateScheduleInfo(scheduleInfo);
        }
        return false;
    }

    public static boolean saveScheduleData(Context context, String id, String scheduleData) {
        LogHelper.logD(TAG, "saveScheduleData:" + scheduleData);
        ScheduleInfo scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
        if (scheduleInfo != null) {
            scheduleInfo.setScheduleData(scheduleData);
            return DBHelper.getInstance(context).updateScheduleInfo(scheduleInfo);
        }
        return false;
    }

    public static String getRobotIdForSchedule(Context context, String id) {
        LogHelper.logD(TAG, "getRobotIdForSchedule: " + id);
        String robotId = null;
        // TODO: get schedule type first and call the appropriate function
        robotId = DBHelper.getInstance(context).getRobotIdForBasicSchedule(id);
        return robotId;
    }

    public static String getScheduleType(Context context, String id) {
        LogHelper.logD(TAG, "getScheduleType:" + id);
        ScheduleInfo scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
        return scheduleInfo.getScheduleType();
    }

}
