package com.neatorobotics.android.slide.android.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedRobotSchedule;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleTimeObject;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.SchedularEvent;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleWebserviceListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleWebserviceListenerWrapper;

public class ScheduleRobotActivity extends Activity{

	private static final String TAG = ScheduleRobotActivity.class.getSimpleName();
	private String mRobotSerialId;
	private Handler mHandler = new Handler();
	private boolean mIsRobotAssocited;
	private ProgressBar mProgressView;
	private ScheduleWebserviceListener mScheduleDetailsAndroidListener;
	private ScheduleWebserviceListener mScheduleClearAndroidListener;
	private ScheduleWebserviceListenerWrapper mScheduleDetailsListenerWrapper;
	private ScheduleWebserviceListenerWrapper mScheduleClearListenerWrapper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_robot);
		mProgressView = (ProgressBar)findViewById(R.id.progress_schedule);
		mScheduleDetailsAndroidListener = new ScheduleDetailsAndroidListener();
		mScheduleDetailsListenerWrapper = new ScheduleWebserviceListenerWrapper(mHandler, mScheduleDetailsAndroidListener);
		mScheduleClearAndroidListener = new ScheduleDetailsAndroidListener();
		mScheduleClearListenerWrapper = new ScheduleWebserviceListenerWrapper(mHandler, mScheduleClearAndroidListener);

		findViewById(R.id.btn_set_schedule_weekdays).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_set_schedule_babynap).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_set_schedule_weekends).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_set_schedule_working_couple).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_set_schedule_clear).setOnClickListener(mOnClickListener);

	}

	@Override
	protected void onResume() {		
		super.onResume();
		setAssociatedRobotSerialId();
	}

	private void setAssociatedRobotSerialId() {
		RobotItem robotItem = NeatoPrefs.getRobotItem(getApplicationContext());
		if (robotItem != null) {
			mRobotSerialId = robotItem.getSerialNumber();
			if (mRobotSerialId != null) {
				mIsRobotAssocited = true;
			}
		}
	}

	private boolean isRobotAssociated() {
		return mIsRobotAssocited;
	}

	private void showProgressView() {
		mProgressView.setVisibility(ProgressBar.VISIBLE);
	}

	private void hideProgressView() {
		mProgressView.setVisibility(ProgressBar.GONE);
	}



	private OnClickListener mOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {

			if (isRobotAssociated()) {
				switch (v.getId()) {
				case R.id.btn_set_schedule_weekdays:
					setWeekDaysSchedule();
					break;

				case R.id.btn_set_schedule_babynap:
					setWeekdaysBabyNapSchedule();
					break;

				case R.id.btn_set_schedule_weekends:
					setWeekendsSchedule();
					break;

				case R.id.btn_set_schedule_working_couple:
					setWorkingCoupleSchedule();
					break;

				case R.id.btn_set_schedule_clear:
					Toast.makeText(getApplicationContext(), "Need to be implement", Toast.LENGTH_SHORT).show();
					break;
				}
			}
			else {
				Toast.makeText(getApplicationContext(), "Please Associate a Robot before scheduling.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void setWeekDaysSchedule() {				
		ScheduleTimeObject startTime = new ScheduleTimeObject(10, 30);
		ScheduleTimeObject endTime = new ScheduleTimeObject(12, 30);		
		ArrayList<Day> days = new ArrayList<Day>();		
		days.add(Day.MONDAY);
		days.add(Day.WEDNESDAY);
		days.add(Day.FRIDAY);
		AdvancedRobotSchedule schedule = new AdvancedRobotSchedule(days, startTime, endTime, "Kitchen", SchedularEvent.CLEAN);

		sendSchedulToRobotScheduler(schedule);
	}

	private void setWeekdaysBabyNapSchedule() {
		ScheduleTimeObject startTime = new ScheduleTimeObject(13, 00);
		ScheduleTimeObject endTime = new ScheduleTimeObject(16, 00);		

		ArrayList<Day> days = new ArrayList<Day>();
		days.add(Day.MONDAY);
		days.add(Day.TUESDAY);
		days.add(Day.WEDNESDAY);		
		AdvancedRobotSchedule schedule = new AdvancedRobotSchedule(days, startTime, endTime, "", SchedularEvent.QUIET);

		ScheduleTimeObject startTime2 = new ScheduleTimeObject(14, 00);
		ScheduleTimeObject endTime2 = new ScheduleTimeObject(17, 00);		
		ArrayList<Day> days2 = new ArrayList<Day>();		
		days2.add(Day.THURSDAY);
		days2.add(Day.FRIDAY);
		AdvancedRobotSchedule schedule2 = new AdvancedRobotSchedule(days2, startTime2, endTime2, "", SchedularEvent.QUIET);

		ScheduleTimeObject startTime3 = new ScheduleTimeObject(9, 00);
		ScheduleTimeObject endTime3 = new ScheduleTimeObject(11, 00);		
		ArrayList<Day> days3 = new ArrayList<Day>();		
		days3.add(Day.MONDAY);
		days3.add(Day.TUESDAY);
		days3.add(Day.WEDNESDAY);
		days3.add(Day.THURSDAY);		
		days3.add(Day.FRIDAY);
		AdvancedRobotSchedule schedule3 = new AdvancedRobotSchedule(days3, startTime3, endTime3, "Bedroom", SchedularEvent.CLEAN);


		AdvancedScheduleGroup scheduleGroup = new AdvancedScheduleGroup();
		scheduleGroup.addSchedule(schedule);
		scheduleGroup.addSchedule(schedule2);
		scheduleGroup.addSchedule(schedule3);
		sendSchedulToRobotScheduler(scheduleGroup);
	}

	private void setWeekendsSchedule() {
		ScheduleTimeObject startTime = new ScheduleTimeObject(12, 30);
		ScheduleTimeObject endTime = new ScheduleTimeObject(14, 30);		
		ArrayList<Day> days = new ArrayList<Day>();		
		days.add(Day.SATURDAY);
		days.add(Day.SUNDAY);
		AdvancedRobotSchedule schedule = new AdvancedRobotSchedule(days, startTime, endTime, "Kitchen", SchedularEvent.CLEAN);

		ScheduleTimeObject startTime2 = new ScheduleTimeObject(15, 30);
		ScheduleTimeObject endTime2 = new ScheduleTimeObject(18, 30);		
		ArrayList<Day> days2 = new ArrayList<Day>();		
		days2.add(Day.SATURDAY);
		days2.add(Day.SUNDAY);
		AdvancedRobotSchedule schedule2 = new AdvancedRobotSchedule(days2, startTime2, endTime2, "Garage", SchedularEvent.CLEAN);

		AdvancedScheduleGroup scheduleGroup = new AdvancedScheduleGroup();
		scheduleGroup.addSchedule(schedule);
		scheduleGroup.addSchedule(schedule2);

		sendSchedulToRobotScheduler(scheduleGroup);
	}

	private void setWorkingCoupleSchedule() {
		ScheduleTimeObject startTime = new ScheduleTimeObject(10, 30);
		ScheduleTimeObject endTime = new ScheduleTimeObject(12, 30);
		ArrayList<Day> days = new ArrayList<Day>();		
		days.add(Day.MONDAY);
		days.add(Day.TUESDAY);
		days.add(Day.WEDNESDAY);
		days.add(Day.THURSDAY);
		AdvancedRobotSchedule schedule = new AdvancedRobotSchedule(days, startTime, endTime, "Kitchen", SchedularEvent.CLEAN);

		ScheduleTimeObject startTime2 = new ScheduleTimeObject(18, 30);
		ScheduleTimeObject endTime2 = new ScheduleTimeObject(22, 30);		
		ArrayList<Day> days2 = new ArrayList<Day>();		
		days2.add(Day.MONDAY);
		days2.add(Day.TUESDAY);
		days2.add(Day.WEDNESDAY);
		days2.add(Day.THURSDAY);
		AdvancedRobotSchedule schedule2 = new AdvancedRobotSchedule(days2, startTime2, endTime2, "Garage", SchedularEvent.CLEAN);

		ScheduleTimeObject startTime3 = new ScheduleTimeObject(8, 30);
		ScheduleTimeObject endTime3 = new ScheduleTimeObject(12, 30);		
		ArrayList<Day> days3 = new ArrayList<Day>();		
		days3.add(Day.SUNDAY);
		days3.add(Day.SATURDAY);
		AdvancedRobotSchedule schedule3 = new AdvancedRobotSchedule(days3, startTime3, endTime3, "", SchedularEvent.QUIET);

		AdvancedScheduleGroup scheduleGroup = new AdvancedScheduleGroup();
		scheduleGroup.addSchedule(schedule);
		scheduleGroup.addSchedule(schedule2);
		scheduleGroup.addSchedule(schedule3);

		sendSchedulToRobotScheduler(scheduleGroup);
	}

	private void sendSchedulToRobotScheduler(AdvancedRobotSchedule schedule) {
		RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(getApplicationContext());
		showProgressView();
		LogHelper.log(TAG, "Updating robot schedule to the server");
		schedulerManager.sendRobotSchedule(schedule, mRobotSerialId, mScheduleDetailsListenerWrapper);		
	}

	private void sendSchedulToRobotScheduler(AdvancedScheduleGroup scheduleGroup) {
		RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(getApplicationContext());
		showProgressView();
		LogHelper.log(TAG, "Updating robot schedule to the server");
		schedulerManager.sendRobotSchedule(scheduleGroup, mRobotSerialId, mScheduleDetailsListenerWrapper);		
	}

	/*private void clearRobotSchedule(String robot_schedule_id) {
		mScheduleClearAndroidListener = new ScheduleClearAndroidListener();
		mScheduleDetailsListenerWrapper = new ScheduleWebserviceListenerWrapper(mHandler, mScheduleClearAndroidListener);
		LogHelper.log(TAG, "Updating robot schedule to the server");
		RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(getApplicationContext());
		schedulerManager.clearRobotSchedule(robot_schedule_id , mScheduleDetailsListenerWrapper);		

	}
	 */
	private class ScheduleDetailsAndroidListener implements ScheduleWebserviceListener {

		@Override
		public void onSuccess() {
			LogHelper.log(TAG, "Schedule details posted");			
			hideProgressView();
			Toast.makeText(ScheduleRobotActivity.this, "Updated robot schedule successfully", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNetworkError() {
			LogHelper.log(TAG, "Schedule details post error");
			hideProgressView();
			Toast.makeText(ScheduleRobotActivity.this, "Could not update robot schedule.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onServerError() {
			LogHelper.log(TAG, "Schedule details post error");
			hideProgressView();
			Toast.makeText(ScheduleRobotActivity.this, "Could not update robot schedule.", Toast.LENGTH_SHORT).show();
		}

	}

	private class ScheduleClearAndroidListener implements ScheduleWebserviceListener {

		@Override
		public void onSuccess() {
			LogHelper.log(TAG, "Schedule cleared");			
			hideProgressView();
			Toast.makeText(ScheduleRobotActivity.this, "Cleared robot schedule successfully", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNetworkError() {
			LogHelper.log(TAG, "Schedule details post error");
			hideProgressView();
			Toast.makeText(ScheduleRobotActivity.this, "Could not clear robot schedule.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onServerError() {
			LogHelper.log(TAG, "Schedule details post error");
			hideProgressView();
			Toast.makeText(ScheduleRobotActivity.this, "Could not clear robot schedule.", Toast.LENGTH_SHORT).show();
		}

	}
}
