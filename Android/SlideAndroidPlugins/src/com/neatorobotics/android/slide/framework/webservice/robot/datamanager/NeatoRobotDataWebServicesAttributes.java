package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

public class NeatoRobotDataWebServicesAttributes {

	public static class SetRobotProfileDetails2 {
		public static final String METHOD_NAME = "robot.set_profile_details2"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String SOURCE_SMARTAPP_ID = "source_smartapp_id";
			public static final String PROFILE = "profile";
		}
		
		public static class ProfileAttributeKeys {
			public static final String ROBOT_CURRENT_STATE 		= "robotCurrentState";
			public static final String ROBOT_CLEANING_COMMAND 	= "cleaningCommand";
			public static final String ROBOT_NAME 				= "name";
			public static final String ROBOT_ENABLE_SCHEDULE 	= "enable_basic_schedule";
			public static final String ROBOT_TURN_VACUUM_ONOFF  = "vacuum_onoff";
			public static final String ROBOT_TURN_WIFI_ONOFF  	= "wifi_onoff";
		}
	}
	
	public static class GetRobotProfileDetails2 {
		public static final String METHOD_NAME = "robot.get_profile_details2";
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String KEY = "key";
		}
	}
	
	public static class GetRobotPresenceStatus {
		public static final String METHOD_NAME = "robot.get_robot_presence_status"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
		}
	}

	public static class IsRobotOnlineVirtual {
		public static final String METHOD_NAME = "robot.is_robot_online_virtual"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
		}
	}
	
	public static class DeleteRobotProfileKey {
		public static final String METHOD_NAME = "robot.delete_robot_profile_key";
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String PROFILE_KEY = "key";
		}
	}

}
