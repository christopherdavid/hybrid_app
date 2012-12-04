package com.neatorobotics.android.slide.framework.webservice.robot.map;

public class NeatoRobotMapWebServicesAttributes {
	public static class GetNeatoRobotMaps {
		public static final String METHOD_NAME = "robot.get_maps"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
		}

	}

	public static class GetNeatoRobotMapData {
		public static final String METHOD_NAME = "robot.get_map_data"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_MAP_ID = "robot_map_id";
		}

	}
	public static class PostNeatoRobotMapData {
		public static final String METHOD_NAME = "robot.post_map_data"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
			public static final String XML_DATA = "xml_data";
			public static final String BLOB_DATA = "encoded_blob_data";
		}
	}

	public static class UpdateNeatoRobotMapData {
		public static final String METHOD_NAME = "robot.update_map_data"; 
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_MAP_ID = "map_id";
			public static final String XML_DATA = "xml_data";
			public static final String BLOB_DATA = "encoded_blob_data";
			public static final String XML_DATA_VERSION = "xml_data_version";
			public static final String BLOB_DATA_VERSION = "blob_data_version";
		}
	}
	
	public static class DeleteNeatoRobotMapData {
		public static final String METHOD_NAME = "robot.delete_map"; 
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ROBOT_MAP_ID = "robot_map_id";
		}
	}

}
