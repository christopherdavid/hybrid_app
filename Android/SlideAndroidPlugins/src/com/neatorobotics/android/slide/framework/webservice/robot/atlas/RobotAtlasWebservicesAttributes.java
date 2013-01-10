package com.neatorobotics.android.slide.framework.webservice.robot.atlas;

public class RobotAtlasWebservicesAttributes {

	public static class GetRobotAtlasData {
		public static final String METHOD_NAME = "robot.get_atlas_data"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_ID = "serial_number";
		}

	}
	public static class AddUpdateRobotAtlasData {
		public static final String METHOD_NAME = "robot.update_atlas"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_ID = "serial_number";
			public static final String ATLAS_ID = "atlas_id";
			public static final String XML_DATA = "xml_data";
			public static final String XML_DATA_VERSION = "xml_data_version";
			public static final String DELETE_GRIDS = "delete_grids";
		}
	}
}
