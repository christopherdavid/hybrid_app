package com.neatorobotics.android.slide.framework.database;

import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleJsonHelper;
import com.neatorobotics.android.slide.framework.robot.schedule2.Schedules;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleInfo2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes;

public class ScheduleHelper {
	
	private static final String TAG = ScheduleHelper.class.getSimpleName();
	
	
	public static ScheduleInfo2 getScheduleInfoById(Context context, String id) {
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);		
		return scheduleInfo;
	}
	
	
	public static Schedules getScheduleGroupById(Context context, String id) {
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		if ((scheduleInfo != null) && 
			(scheduleInfo.getScheduleType().equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC))) {
			return ScheduleJsonHelper.readJsonToBasicSchedule(scheduleInfo.getScheduleData());
		} 
		else {
			// TODO: Add support for Advance schedule 
		}
		
		return null;
	}

	public static String getScheduleIdFromDB(Context context, String robotId, int type) {
		String scheduleId = null;
		if (type == SchedulerConstants2.SCHEDULE_TYPE_BASIC) {
			scheduleId = DBHelper.getInstance(context).getBasicScheduleIdForRobot(robotId);
		} else if (type == SchedulerConstants2.SCHEDULE_TYPE_ADVANCED) {
			scheduleId = DBHelper.getInstance(context).getAdvancedScheduleIdForRobot(robotId);
		}
		return scheduleId;
	}
	
	public static void saveScheduleId(Context context, String robotId, String id, int type) {
		if (type == SchedulerConstants2.SCHEDULE_TYPE_BASIC) {
			DBHelper.getInstance(context).saveBasicScheduleId(robotId, id);
		} else if (type == SchedulerConstants2.SCHEDULE_TYPE_ADVANCED) {
			DBHelper.getInstance(context).saveAdvancedScheduleId(robotId, id);
		}
	}
	
	public static void saveScheduleInfo(Context context, String id, String serverId, String scheduleVersion, String scheduleType, String data) {
		DBHelper.getInstance(context).saveScheduleInfo(id, serverId, scheduleVersion, scheduleType, data);
	}
	
	public static boolean updateScheduleVersion(Context context, String id, String scheduleVersion) {
		LogHelper.logD(TAG, "updateScheduleVersion");
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		if (scheduleInfo != null) {			
			scheduleInfo.setDataVersion(scheduleVersion);
			return DBHelper.getInstance(context).updateScheduleInfo(scheduleInfo);
		}
		return false;
	}

	public static boolean saveScheduleData(Context context, String id, String scheduleData) {
		LogHelper.logD(TAG, "saveScheduleData:" +scheduleData);
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		if (scheduleInfo != null) {			
			scheduleInfo.setScheduleData(scheduleData);
			return DBHelper.getInstance(context).updateScheduleInfo(scheduleInfo);
		}
		return false;
	}

	public static String getRobotIdForSchedule(Context context, String id) {
		LogHelper.logD(TAG, "getRobotIdForSchedule:"+id);
		String robotId = null;
		// TODO: get schedule type first and call the appropriate function
		robotId = DBHelper.getInstance(context).getRobotIdForBasicSchedule(id);
		if (TextUtils.isEmpty(robotId)) {
			robotId = DBHelper.getInstance(context).getRobotIdForAdvancedSchedule(id);
		}
		return robotId;
	}
	
	public static String getScheduleType(Context context, String id) {
		LogHelper.logD(TAG, "getScheduleType:" + id);
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		return scheduleInfo.getScheduleType();
	}
	
	public static String getScheduleVersion(Context context, String id) {
		LogHelper.logD(TAG, "getScheduleVersion:" + id);
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		return scheduleInfo.getDataVersion();
	}
	
	public static String getScheduleData(Context context, String id) {
		LogHelper.logD(TAG, "getScheduleData:" + id);
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		return scheduleInfo.getScheduleData();

	}
	
	public static boolean setServerScheduleIdAndVersion(Context context, String id, String serverId, String version) {
		LogHelper.logD(TAG, "getScheduleServerId:"+id);
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		if (scheduleInfo != null) {
			scheduleInfo.setServerId(serverId);
			scheduleInfo.setDataVersion(version);
			DBHelper.getInstance(context).updateScheduleInfo(scheduleInfo);
		}
		return false;
	}
	
	public static String getScheduleServerId(Context context, String id) {
		LogHelper.logD(TAG, "getScheduleServerId:"+id);
		ScheduleInfo2 scheduleInfo = DBHelper.getInstance(context).getScheduleInfoById(id);
		return scheduleInfo.getServerId();
	}
}   
