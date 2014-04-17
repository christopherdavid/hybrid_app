package com.neatorobotics.android.slide.framework.webservice.user.settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.MobileWebServiceClient;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetRobotPushNotificationOptions;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.SetRobotPushNotificationOptions;

public class NeatoRobotWebservicesSettingsHelper {

    public static RobotNotificationSettingsResult getRobotNotificationSettingsRequest(Context context, String email)
            throws UserUnauthorizedException, NeatoServerException, IOException {

        Map<String, String> params = new HashMap<String, String>();
        params.put(GetRobotPushNotificationOptions.Attribute.EMAIL, email);

        String response = MobileWebServiceClient.executeHttpPost(context, GetRobotPushNotificationOptions.METHOD_NAME,
                params);
        return AppUtils.checkResponseResult(response, RobotNotificationSettingsResult.class);
    }

    public static SetRobotNotificationSettingsResult setRobotNotificationSettingsRequest(Context context, String email,
            String notificationJson) throws UserUnauthorizedException, NeatoServerException, IOException {

        Map<String, String> params = new HashMap<String, String>();
        params.put(SetRobotPushNotificationOptions.Attribute.EMAIL, email);
        params.put(SetRobotPushNotificationOptions.Attribute.JSON_OBJECT, notificationJson);

        String response = MobileWebServiceClient.executeHttpPost(context, SetRobotPushNotificationOptions.METHOD_NAME,
                params);
        return AppUtils.checkResponseResult(response, SetRobotNotificationSettingsResult.class);
    }
}
