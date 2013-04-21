package com.neatorobotics.android.slide.framework.robot.settings;

import android.content.Context;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;


public class SettingsManager {
	private static final String TAG = SettingsManager.class.getSimpleName();
	private Context mContext;

	private static SettingsManager sSettingsManager;
	private static final Object INSTANCE_LOCK = new Object();

	private SettingsManager(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static SettingsManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sSettingsManager == null) {
				sSettingsManager = new SettingsManager(context);
			}
		}
		
		return sSettingsManager;
	}

	public void updateSpotDefinition(final String robotId, final int spotAreaLength, 
			final int spotAreaHeight, final CleaningSettingsListener listener) 
	{
		LogHelper.logD(TAG, "updateSpotDefinition called");
		LogHelper.logD(TAG, "Robot Id = " + robotId + ", Length = " + spotAreaLength +
				", Height = " + spotAreaHeight);
		Runnable task = new Runnable() {
			public void run() {
				CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(mContext, robotId);
				if (cleaningSettings == null) {
					cleaningSettings =  new CleaningSettings();
				}
				// update values
				cleaningSettings.setSpotAreaLength(spotAreaLength);
				cleaningSettings.setSpotAreaHeight(spotAreaHeight);

				boolean updated = RobotHelper.updateCleaningSettings(mContext, robotId, cleaningSettings);	
				if (updated) {
					listener.onSuccess(cleaningSettings);
				}
				else {
					listener.onError();
				}
				
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	public void getCleaningSettings(final String robotId, final CleaningSettingsListener listener) 
	{
		LogHelper.logD(TAG, "getRobotSettings called");
		LogHelper.logD(TAG, "Robot Id = " + robotId);
		Runnable task = new Runnable() {
			public void run() {
				CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(mContext, robotId);	
				if (cleaningSettings != null) {
					listener.onSuccess(cleaningSettings);
				}
				else {
					listener.onError();
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
}
