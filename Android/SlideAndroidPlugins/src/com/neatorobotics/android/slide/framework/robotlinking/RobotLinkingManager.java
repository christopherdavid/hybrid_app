package com.neatorobotics.android.slide.framework.robotlinking;

import java.io.IOException;
import android.content.Context;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.user.RobotLinkResult;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotLinkingManager {
	
	private static final String TAG = RobotLinkingManager.class.getSimpleName();
	
	public static void linkRobot(final Context context, final String linkCode, final String emailId, final WebServiceBaseRequestListener listener) {		
		Runnable task = new Runnable() {
			public void run() {
				try {
					RobotLinkResult initiateLink = NeatoUserWebservicesHelper.initiateLinkToRobot(context, emailId, linkCode);
					String robotId = initiateLink.result.serial_number;
					RobotManager.getInstance(context).getRobotDetailAndSave(robotId);
					listener.onReceived(initiateLink);
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}	
			}

		};
		TaskUtils.scheduleTask(task, 0);
	}
	
}
