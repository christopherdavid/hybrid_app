package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

public class NeatoRobotDataWebServicesAttributes {
	
	public static class SetRobotProfileDetails3 {
		public static final String METHOD_NAME = "robot.set_profile_details3"; 
		public static final int DATA_CHANGED_NOTIFICATION_FLAG_ON = 1;
		public static final int DATA_CHANGED_NOTIFICATION_FLAG_OFF = 0;
		public static class Attribute {
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String SOURCE_SMARTAPP_ID = "source_smartapp_id";
			public static final String PROFILE = "profile";
			public static final String CAUSING_AGENT_ID = "cause_agent_id";
		}
		// IMPORTANT: Whenever any profile attribute key is added to the below class,
		// add the profile key enum in ProfileAttributeKeysEnum as well.
		// This enum is used to parse through all profile keys
		// when we retrieve profile data.
		public static class ProfileAttributeKeys {
			public static final String ROBOT_CURRENT_STATE 		= "robotCurrentState";
			public static final String ROBOT_CLEANING_COMMAND 	= "cleaningCommand";
			public static final String ROBOT_NAME 				= "name";
			public static final String ROBOT_ENABLE_BASIC_SCHEDULE 	= "enable_basic_schedule";
			public static final String ROBOT_ENABLE_ADVANCED_SCHEDULE 	= "enable_advanced_schedule";
			public static final String ROBOT_TURN_VACUUM_ONOFF  = "vacuum_onoff";
			public static final String ROBOT_TURN_WIFI_ONOFF  	= "wifi_onoff";
			public static final String ROBOT_SCHEDULE_UPDATED 	= "schedule_updated";
		}
		
		public static enum ProfileAttributeKeysEnum {
			 ROBOT_CURRENT_STATE,
			 ROBOT_CLEANING_COMMAND,
			 ROBOT_NAME,
			 ROBOT_ENABLE_BASIC_SCHEDULE,
			 ROBOT_ENABLE_ADVANCED_SCHEDULE,
			 ROBOT_TURN_VACUUM_ONOFF,
			 ROBOT_TURN_WIFI_ONOFF,
			 ROBOT_SCHEDULE_UPDATED,
		}
		
		public static String getProfileKey(ProfileAttributeKeysEnum key) {
			switch(key) {
				case ROBOT_CURRENT_STATE:
					return ProfileAttributeKeys.ROBOT_CURRENT_STATE;
				case ROBOT_CLEANING_COMMAND:
					return ProfileAttributeKeys.ROBOT_CLEANING_COMMAND;
				case ROBOT_NAME:
					return ProfileAttributeKeys.ROBOT_NAME;
				case ROBOT_ENABLE_BASIC_SCHEDULE:
					return ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE;
				case ROBOT_ENABLE_ADVANCED_SCHEDULE:
					return ProfileAttributeKeys.ROBOT_ENABLE_ADVANCED_SCHEDULE;
				case ROBOT_TURN_VACUUM_ONOFF:
					return ProfileAttributeKeys.ROBOT_TURN_VACUUM_ONOFF;
				case ROBOT_SCHEDULE_UPDATED:
					return ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED;
				case ROBOT_TURN_WIFI_ONOFF:
					return ProfileAttributeKeys.ROBOT_TURN_WIFI_ONOFF;
				default:
					return null;
			}
		}
	}
	
	public static class GetRobotProfileDetails2 {
		public static final String METHOD_NAME = "robot.get_profile_details2";
		public static class Attribute {
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String KEY = "key";
		}
	}
	
	public static class GetRobotPresenceStatus {
		public static final String METHOD_NAME = "robot.get_robot_presence_status"; 

		public static class Attribute {
			public static final String SERIAL_NUMBER = "serial_number";
		}
	}

	public static class IsRobotOnlineVirtual {
		public static final String METHOD_NAME = "robot.is_robot_online_virtual"; 

		public static class Attribute {
			public static final String SERIAL_NUMBER = "serial_number";
		}
	}
	
	public static class DeleteRobotProfileKey {
		public static final String METHOD_NAME = "robot.delete_robot_profile_key2";
		public static final String DATA_CHANGED_NOTIFICATION_FLAG_ON = "1";
		public static final String DATA_CHANGED_NOTIFICATION_FLAG_OFF = "0";
		public static class Attribute {
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String PROFILE_KEY = "key";
			public static final String CAUSING_AGENT_ID = "cause_agent_id";
			public static final String SOURCE_SMARTAPP_ID = "source_smartapp_id";
			public static final String NOTIFICATION_FLAG	= "notification_flag";
		}
	}

}
