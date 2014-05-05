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

        // IMPORTANT: Whenever any profile attribute key is added to the below
        // class,
        // add the profile key enum in ProfileAttributeKeysEnum as well.
        // This enum is used to parse through all profile keys
        // when we retrieve profile data.
        public static class ProfileAttributeKeys {
            public static final String ROBOT_CURRENT_STATE = "robotCurrentState";
            public static final String ROBOT_CLEANING_COMMAND = "cleaningCommand";
            public static final String ROBOT_CURRENT_STATE_DETAILS = "robotCurrentStateDetails";
            public static final String ROBOT_NOTIFICATION = "robotNotificationMsg";
            public static final String ROBOT_ERROR = "robotErrorMsg";
            public static final String ROBOT_NAME = "name";
            public static final String ROBOT_ENABLE_BASIC_SCHEDULE = "enable_basic_schedule";
            public static final String ROBOT_TURN_VACUUM_ONOFF = "vacuum_onoff";
            public static final String ROBOT_TURN_WIFI_ONOFF = "wifi_onoff";
            public static final String ROBOT_SCHEDULE_UPDATED = "schedule_updated";
            public static final String INTEND_TO_DRIVE = "intend_to_drive";
            public static final String AVAILABLE_TO_DRIVE = "available_to_drive";
            public static final String ROBOT_ONLINE_STATUS = "robotOnlineStatus";
        }

        public static class ProfileAttributeValueKeys {
            public static final String DEVICE_ID = "device_id";
            public static final String ROBOT_WIFI_ON_TIME_IN_MS = "wifi_on_time_ms";
            public static final String DRIVE_AVAILABLE_STATUS = "driveAvailableStatus";
            public static final String ERROR_DRIVE_REASON_CODE = "errorDriveReasonCode";
            public static final String ROBOT_IP_ADDRESS = "robotIpAddress";

            public static final String ROBOT_STATE_DETAILS = "robotStateDetails";
            public static final String ROBOT_CLEANING_CATEGORY = "robotCleaningCategory";
            public static final String ROBOT_STATE_PARAMS = "robotStateParams";
            public static final String ROBOT_NOTIFICATION_MESSAGE_ID = "messageID ";

        }

		public static enum ProfileAttributeKeysEnum {
			 ROBOT_CURRENT_STATE,
			 ROBOT_CURRENT_STATE_DETAILS,
			 ROBOT_CLEANING_COMMAND,
			 ROBOT_NAME,
			 ROBOT_ENABLE_BASIC_SCHEDULE,
			 ROBOT_ENABLE_ADVANCED_SCHEDULE,
			 ROBOT_TURN_VACUUM_ONOFF,
			 ROBOT_TURN_WIFI_ONOFF,
			 ROBOT_SCHEDULE_UPDATED,
			 INTEND_TO_DRIVE,
			 AVAILABLE_TO_DRIVE, 
			 ROBOT_NOTIFICATION, 
			 ROBOT_ERROR, 
			 ROBOT_ONLINE_STATUS
		}
		
		public static String getProfileKey(ProfileAttributeKeysEnum key) {
			switch(key) {
				case ROBOT_CURRENT_STATE:
					return ProfileAttributeKeys.ROBOT_CURRENT_STATE;
				case ROBOT_CURRENT_STATE_DETAILS:
					return ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS;
				case ROBOT_CLEANING_COMMAND:
					return ProfileAttributeKeys.ROBOT_CLEANING_COMMAND;
				case ROBOT_NAME:
					return ProfileAttributeKeys.ROBOT_NAME;
				case ROBOT_ENABLE_BASIC_SCHEDULE:
					return ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE;
				case ROBOT_TURN_VACUUM_ONOFF:
					return ProfileAttributeKeys.ROBOT_TURN_VACUUM_ONOFF;
				case ROBOT_SCHEDULE_UPDATED:
					return ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED;
				case ROBOT_TURN_WIFI_ONOFF:
					return ProfileAttributeKeys.ROBOT_TURN_WIFI_ONOFF;
				case AVAILABLE_TO_DRIVE:
					return ProfileAttributeKeys.AVAILABLE_TO_DRIVE;
				case INTEND_TO_DRIVE:
					return ProfileAttributeKeys.INTEND_TO_DRIVE;
				case ROBOT_NOTIFICATION:
					return ProfileAttributeKeys.ROBOT_NOTIFICATION;
				case ROBOT_ERROR:
					return ProfileAttributeKeys.ROBOT_ERROR;
				case ROBOT_ONLINE_STATUS:
					return ProfileAttributeKeys.ROBOT_ONLINE_STATUS;
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
            public static final String NOTIFICATION_FLAG = "notification_flag";
        }
    }

}
