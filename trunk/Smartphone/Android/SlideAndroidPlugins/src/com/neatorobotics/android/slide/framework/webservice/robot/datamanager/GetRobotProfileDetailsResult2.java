package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;

public class GetRobotProfileDetailsResult2 extends NeatoWebserviceResult {

	public GetRobotProfileDetailsResult2(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	@JsonProperty(value="result")
	public Result mResult;

	public GetRobotProfileDetailsResult2() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && ((mResult != null)));
	}
	public static class Result {
		
		@JsonProperty(value="success")
		public boolean mSuccess;
		
		@JsonProperty(value="profile_details")
		public HashMap<String, ProfileKeyDetails> mProfileDetails;	
		
	}
	
	public static class ProfileKeyDetails {
		public String value;
		public long timestamp;
	}
	
	public String getCleaningCommand() {
		return getProfileParameterValue(ProfileAttributeKeys.ROBOT_CLEANING_COMMAND);
	}
	
	public String getRobotCurrentState() {
		return getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE);
	}
	
	@SuppressWarnings("unused")
	private long getProfileParameterTimeStamp(String key) {
		if (mResult != null && mResult.mProfileDetails != null) {
			HashMap<String, ProfileKeyDetails> profileDetails = mResult.mProfileDetails; 
			if (profileDetails.containsKey(key)) {
				return profileDetails.get(key).timestamp;
			}
		}
		return 0;
	}
	
	private String getProfileParameterValue(String key) {
		if (mResult != null && mResult.mProfileDetails != null) {
			HashMap<String, ProfileKeyDetails> profileDetails = mResult.mProfileDetails; 
			if (profileDetails.containsKey(key)) {
				return profileDetails.get(key).value;
			}
		}
		return null;
	}
}
