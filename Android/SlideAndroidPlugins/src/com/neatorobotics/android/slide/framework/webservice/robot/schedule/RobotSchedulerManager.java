package com.neatorobotics.android.slide.framework.webservice.robot.schedule;



import java.lang.ref.WeakReference;
import android.content.Context;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedRobotSchedule;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;


public class RobotSchedulerManager {
	private static final String TAG = RobotSchedulerManager.class.getSimpleName();
	Context mContext;
	private static RobotSchedulerManager sRobotSchedulerManager;
	private static final Object INSTANCE_LOCK = new Object();

	private RobotSchedulerManager(Context context)
	{
		mContext = context.getApplicationContext();
	}
	
	public static RobotSchedulerManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotSchedulerManager == null) {
				sRobotSchedulerManager = new RobotSchedulerManager(context);
			}
		}

		return sRobotSchedulerManager;
	}


	//TODO: i have kept these two functions separate as i feel there will be many more things involved in advanced scheduling later 
	//though the web services
	// as of now as same.
	public void sendRobotSchedule(AdvancedScheduleGroup robotScheduleGroup, final String serial_number, ScheduleWebserviceListener scheduleDetailsListener) {
		final String xmlSchedule = robotScheduleGroup.getXml();
		final String blobData = robotScheduleGroup.getBlobData();
		final String scheduleType = NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED;
		final WeakReference<ScheduleWebserviceListener> scheduleDetailsListenerWeakRef = new WeakReference<ScheduleWebserviceListener>(scheduleDetailsListener);

		Runnable task = new Runnable() {

			public void run() {
				ScheduleWebserviceListener scheduleListener  = scheduleDetailsListenerWeakRef.get();
				String currentScheduleId = null;
				String currentxml_version = null;
				GetNeatoRobotSchedulesResult schedulesResult = NeatoRobotScheduleWebservicesHelper.getNeatoRobotSchedulesRequest(mContext, serial_number);
				if (schedulesResult != null && schedulesResult.success()) {
					int scheduleListSize = schedulesResult.mResult.size();

					//TODO: This is under assumption that the schedules are sent in descending order.
					for (int scheduleIterator = (scheduleListSize-1); scheduleIterator >= 0 ; scheduleIterator--) {
						if (NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED.equals(schedulesResult.mResult.get(scheduleIterator).mSchedule_Type)) {
							currentScheduleId = schedulesResult.mResult.get(scheduleIterator).mId;
							currentxml_version = schedulesResult.mResult.get(scheduleIterator).mXml_Data_Version;
							LogHelper.log(TAG, "Current schedule id is: "+currentScheduleId);
							break;
						}
					}
				} else {
					LogHelper.log(TAG, "No scheduling data exists.");
				}

				if (currentScheduleId == null) {
					AddNeatoRobotScheduleDataResult result = NeatoRobotScheduleWebservicesHelper.addNeatoRobotScheduleDataRequest(mContext, serial_number, scheduleType, xmlSchedule, blobData);
					if (result != null && result.success()) {
						LogHelper.log(TAG, "Sucessfully posted scheduling data with robot schedule id:"+result.mResult.mRobot_Schedule_Id);
						if (scheduleListener != null) {
							scheduleListener.onSuccess();
						}
					} else {
						LogHelper.log(TAG, "Error: "+result.mMessage);
						if (scheduleListener != null) {
							scheduleListener.onNetworkError();
						}
					}
				} else {
					// Robot schedule exists. Hence update schedule
					LogHelper.log(TAG, "Currrent scheduel ID: :" + currentScheduleId);
					UpdateNeatoRobotScheduleResult result = NeatoRobotScheduleWebservicesHelper.updateNeatoRobotScheduleDataRequest(mContext, currentScheduleId, NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED, xmlSchedule, currentxml_version);
					if (result != null && result.success()) {
						LogHelper.log(TAG, "Sucessfully updated scheduling data with robot schedule id: " + currentScheduleId);
						if (scheduleListener != null) {
							scheduleListener.onSuccess();
						}
					} else {
						//TODO: Take into account server error also.
						LogHelper.log(TAG, "Error in updating schedule");
						if (scheduleListener != null) {
							scheduleListener.onNetworkError();
						}
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}


	public void sendRobotSchedule(AdvancedRobotSchedule robotSchedule, final String serial_number, ScheduleWebserviceListener scheduleDetailsListener) {
		AdvancedScheduleGroup robotScheduleGroup = new AdvancedScheduleGroup();
		robotScheduleGroup.addSchedule(robotSchedule);
		sendRobotSchedule(robotScheduleGroup, serial_number, scheduleDetailsListener);
	}
}
