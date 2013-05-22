package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import java.util.HashMap;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetRobotProfileDetailsResult2 extends NeatoWebserviceResult {

	public GetRobotProfileDetailsResult2(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	public Result result;

	public GetRobotProfileDetailsResult2() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && ((result != null)));
	}
	public static class Result {
		public boolean success;
		public HashMap<String, ProfileKeyDetails> profile_details;	
		
	}
	
	public static class ProfileKeyDetails {
		public String value;
		public long timestamp;
	}
	
	
	public long getProfileParameterTimeStamp(String key) {
		if (result != null && result.profile_details != null) {
			HashMap<String, ProfileKeyDetails> profileDetails = result.profile_details; 
			if (profileDetails.containsKey(key)) {
				return profileDetails.get(key).timestamp;
			}
		}
		return 0;
	}
	
	public boolean contains(String profileKey) {
		if (result != null && result.profile_details != null) {
			HashMap<String, ProfileKeyDetails> profileDetails = result.profile_details; 
			if (profileDetails.containsKey(profileKey)) {
				return true;
			}
		}
		return false;
	}
	
	public String getProfileParameterValue(String key) {
		if (result != null && result.profile_details != null) {
			HashMap<String, ProfileKeyDetails> profileDetails = result.profile_details; 
			if (profileDetails.containsKey(key)) {
				return profileDetails.get(key).value;
			}
		}
		return null;
	}
}
