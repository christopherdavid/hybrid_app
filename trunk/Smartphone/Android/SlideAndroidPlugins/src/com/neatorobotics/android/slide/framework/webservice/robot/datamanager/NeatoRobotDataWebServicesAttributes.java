package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import java.util.ArrayList;

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

        public static class ProfileAttributeKeys {
            public static final String ROBOT_CURRENT_STATE = "robotCurrentState";
            public static final String ROBOT_CLEANING_COMMAND = "cleaningCommand";
            public static final String ROBOT_CURRENT_STATE_DETAILS = "robotCurrentStateDetails";
            public static final String ROBOT_NAME = "name";
            public static final String ROBOT_SERIAL_NUMBER = "serial_number";
            public static final String ROBOT_ENABLE_BASIC_SCHEDULE = "enable_basic_schedule";
            public static final String ROBOT_SCHEDULE_UPDATED = "schedule_updated";
            public static final String ROBOT_ONLINE_STATUS = "robotOnlineStatus";
            public static final String ROBOT_NETWORK_INFO = "NetInfo";

            public static ArrayList<String> sInternalUsedKeys = new ArrayList<String>();
            
            static {
                sInternalUsedKeys.add(ProfileAttributeKeys.ROBOT_CLEANING_COMMAND);
                sInternalUsedKeys.add(ProfileAttributeKeys.ROBOT_ONLINE_STATUS);
                sInternalUsedKeys.add(ProfileAttributeKeys.ROBOT_NAME);
                sInternalUsedKeys.add(ProfileAttributeKeys.ROBOT_SERIAL_NUMBER);
                sInternalUsedKeys.add(ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED);
            }

        }

        public static class ProfileAttributeValueKeys {
            public static final String DEVICE_ID = "device_id";
            public static final String ROBOT_IP_ADDRESS = "robotIpAddress";
            public static final String ROBOT_STATE_DETAILS = "robotStateDetails";
            public static final String ROBOT_CLEANING_CATEGORY = "robotCleaningCategory";
            public static final String ROBOT_STATE_PARAMS = "robotStateParams";
            public static final String ROBOT_NOTIFICATION_MESSAGE_ID = "messageID ";
        }

    }

    public static class GetRobotProfileDetails2 {
        public static final String METHOD_NAME = "robot.get_profile_details2";

        public static class Attribute {
            public static final String SERIAL_NUMBER = "serial_number";
            public static final String KEY = "key";
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
