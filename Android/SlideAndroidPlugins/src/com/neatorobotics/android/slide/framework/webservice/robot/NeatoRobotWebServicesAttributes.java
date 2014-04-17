package com.neatorobotics.android.slide.framework.webservice.robot;

public class NeatoRobotWebServicesAttributes {

    public static final String ACCOUNT_TYPE_NATIVE = "Native";
    public static final String ACCOUNT_TYPE_FACEBOOK = "Facebook";
    public static final String ACCOUNT_TYPE_GOOGLE = "Google";

    public static class AssociateNeatoRobotToUser {
        public static final String METHOD_NAME = "robot.set_user";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String SERIAL_NUMBER = "serial_number";
        }

    }

    public static class DisassociateNeatoRobotToUser {
        public static final String METHOD_NAME = "robot.disassociate_user";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String SERIAL_NUMBER = "serial_number";
        }

    }

    public static class GetRobotDetails {
        public static final String METHOD_NAME = "robot.get_details";

        public static class Attribute {
            public static final String SERIAL_NUMBER = "serial_number";
        }

    }

    public static class SetRobotProfileDetails {
        public static final String METHOD_NAME = "robot.set_profile_details";

        public static class Attribute {
            public static final String API_KEY = "api_key";
            public static final String SERIAL_NUMBER = "serial_number";
            public static final String ROBOT_NAME = "name";
            public static final String PROFILE = "profile";
        }
    }

    public static class ClearRobotData {
        public static final String METHOD_NAME = "robot.clear_robot_association";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String SERIAL_NUMBER = "serial_number";
            public static final String IS_DELETE = "is_delete";
        }
    }

    public static class DissociateAllNeatoRobotsFromUser {
        public static final String METHOD_NAME = "user.disassociate_robot";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String SERIAL_NUMBER = "serial_number";
        }
    }

    public static class GetRobotOnlineStatus {
        public static final String METHOD_NAME = "robot.is_online";

        public static class Attribute {
            public static final String SERIAL_NUMBER = "serial_number";
        }
    }

    public static class GetRobotVirtualOnlineStatus {
        public static final String METHOD_NAME = "robot.is_robot_online_virtual";

        public static class Attribute {
            public static final String API_KEY = "api_key";
            public static final String SERIAL_NUMBER = "serial_number";
        }
    }

    public static class InitiateLinkToRobot {
        public static final String METHOD_NAME = "robot.link_to_robot";

        public static class Attribute {
            public static final String EMAIL = "email";
            public static final String LINKING_CODE = "linking_code";
        }
    }

}
