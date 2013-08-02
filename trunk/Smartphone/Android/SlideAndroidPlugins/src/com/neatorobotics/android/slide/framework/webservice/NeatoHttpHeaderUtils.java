package com.neatorobotics.android.slide.framework.webservice;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;

public class NeatoHttpHeaderUtils {
	
	private static final String TAG = NeatoHttpHeaderUtils.class.getSimpleName();
	private static ObjectMapper resultMapper = NeatoWebserviceUtils.getObjectMapper();
	
	public static String getNeatoHttpHeaderString(Context context) {
		
		String neatoHttpHeaderString = null;
		
		NeatoHttpRequestHeader header = new NeatoHttpRequestHeader();
		// add application details
		header.setAppPackage(AppUtils.getAppPackage(context));
		header.setApplicationVersion(AppUtils.getVersionWithBuildNumber(context));
		String timezone = AppUtils.getTimezoneHoursOffset();
		header.setTimezone(timezone);
		String locale = AppUtils.getCurrentLocale(context);
		header.setLocale(locale);
		
		// add device details 
		header.setOsName(DeviceUtils.getDeviceOperatingSystem(context));
		header.setOsVersion(DeviceUtils.getDeviceOperatingSystemVersion(context));
		header.setDeviceName(DeviceUtils.getDeviceName(context));
		
		try {
			neatoHttpHeaderString = resultMapper.writeValueAsString(header);
        } catch (RuntimeException e) {
            LogHelper.log(TAG, "Runtime exception in getNeatoHttpHeaderString", e);
        } catch (Exception e) {
        	LogHelper.log(TAG, "Exception in getNeatoHttpHeaderString", e);
        }
        return neatoHttpHeaderString;
	}
}
