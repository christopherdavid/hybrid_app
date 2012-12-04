package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

public class NeatoRobotScheduleWebServicesAttributes {

	public static final String SCHEDULE_TYPE_ADVANCED = "Advanced";
	public static final String SCHEDULE_TYPE_BASIC = "Basic";

	public static class GetNeatoRobotSchedules {
		public static final String METHOD_NAME = "robotschedule.get_schedules"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
		}

	}

	public static class GetNeatoRobotScheduleData {
		public static final String METHOD_NAME = "robotschedule.get_data"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_SCHEDULE_ID = "robot_schedule_id";
		}
	}

	public static class PostNeatoRobotScheduleData {
		public static final String METHOD_NAME = "robotschedule.post_data"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String SCHEDULE_TYPE = "schedule_type";
			public static final String XML_DATA = "xml_data";
			public static final String BLOB_DATA = "blob_data";
		}
	}

	public static class UpdateNeatoRobotScheduleData {
		public static final String METHOD_NAME = "robotschedule.update_data"; 
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_SCHEDULE_ID = "robot_schedule_id";
			public static final String SCHEDULE_TYPE = "schedule_type";
			public static final String XML_DATA = "xml_data";
			public static final String BLOB_DATA = "blob_data";
			public static final String XML_DATA_VERSION = "xml_data_version";
			public static final String BLOB_DATA_VERSION = "blob_data_version";
		}
	}
	
	public static class DeleteNeatoRobotScheduleData {
		public static final String METHOD_NAME = "robotschedule.delete_data"; 
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_SCHEDULE_ID = "robot_schedule_id";
		}
	}

}
