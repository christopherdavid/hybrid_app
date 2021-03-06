package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

public class NeatoRobotScheduleWebServicesAttributes {

    public static final String SCHEDULE_TYPE_BASIC = "Basic";

    public static class PostNeatoRobotScheduleData {
        public static final String METHOD_NAME = "robotschedule.post_data";

        public static class Attribute {
            public static final String SERIAL_NUMBER = "serial_number";
            public static final String SCHEDULE_TYPE = "schedule_type";
            public static final String XML_DATA = "xml_data";
            public static final String BLOB_DATA = "blob_data";
        }
    }

    public static class UpdateNeatoRobotScheduleData {
        public static final String METHOD_NAME = "robotschedule.update_data";

        public static class Attribute {
            public static final String ROBOT_SCHEDULE_ID = "robot_schedule_id";
            public static final String SCHEDULE_TYPE = "schedule_type";
            public static final String XML_DATA = "xml_data";
            public static final String BLOB_DATA = "blob_data";
            public static final String XML_DATA_VERSION = "xml_data_version";
            public static final String BLOB_DATA_VERSION = "blob_data_version";
        }
    }

    public static class GetScheduleBasedOnType {
        public static final String METHOD_NAME = "robotschedule.get_schedule_based_on_type";

        public static class Attribute {
            public static final String ROBOT_SERIAL_NUMBER = "robot_serial_number";
            public static final String SCHEDULE_TYPE = "schedule_type";
        }
    }

}
