package com.neatorobotics.android.slide.framework.database;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule2.Schedule2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleGroup2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleInfo2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleXmlHelper2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;

public class ScheduleHelper {
	
	private static final String TAG = ScheduleHelper.class.getSimpleName();
	public static ScheduleGroup2 getScheduleFromDB(Context context, String robotId, int type) {
		ScheduleGroup2 schedule = null;
		String id = getScheduleIdFromDB(context, robotId, type);
		if (!TextUtils.isEmpty(id)) {
			schedule = getScheduleFromId(context, id);
		}
		return schedule;
	}
	
	public static ScheduleGroup2 getScheduleFromId(Context context, String id) {
		String data = getScheduleData(context, id);
		LogHelper.logD(TAG, "Schedule in DB: " + data);
		String scheduleType = getScheduleType(context, id);
		ScheduleGroup2 schedule = ScheduleXmlHelper2.readXmlString(scheduleType, data);
		return schedule;
	}

	
	public static boolean addScheduleEvent(Context context, String id, Schedule2 event) {
		ScheduleGroup2 schedule = getScheduleFromId(context, id);
		boolean success = false;
		if (schedule != null) {
			success = schedule.addSchedule(event);
			String scheduleData = schedule.getXml();
			saveScheduleData(context, id, scheduleData);
			LogHelper.logD(TAG, schedule.toJsonArray().toString());
		}
		return success;
	}
	
	public static boolean deleteScheduleEvent(Context context, String id, String eventId) {
		ScheduleGroup2 schedule = getScheduleFromId(context, id);
		boolean success = false;
		if (schedule != null) {
			success = schedule.removeScheduleEvent(eventId);
			String scheduleData = schedule.getXml();
			saveScheduleData(context, id, scheduleData);
			LogHelper.logD(TAG, schedule.toJsonArray().toString());
		}
		return success;
	}
	
	public static boolean updateScheduleEvent(Context context, String id, Schedule2 event) {
		ScheduleGroup2 schedule = getScheduleFromId(context, id);
		boolean success = false;
		if (schedule != null) {
			success = schedule.updateScheduleEvent(event);
			String scheduleData = schedule.getXml();
			saveScheduleData(context, id, scheduleData);
			LogHelper.log(TAG, schedule.toJsonArray().toString());
		}
		return success;
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
