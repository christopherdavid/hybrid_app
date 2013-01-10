package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;

public class RobotAtlasGridWebServicesAttributes {
	
	public static class PostGridImage {
		public static final String METHOD_NAME = "robot.post_grid_image"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ATLAS_ID = "id_atlas";
			public static final String GRID_ID = "id_grid";
			public static final String BLOB_DATA = "encoded_blob_data";		
		}
	}

	public static class UpdateGridImage {
		public static final String METHOD_NAME = "robot.update_grid_image"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ATLAS_ID = "id_atlas";
			public static final String GRID_ID = "id_grid";
			public static final String BLOB_DATA = "encoded_blob_data";		
		}
	}
	
	public static class GetAtlasGridMetadata {
		public static final String METHOD_NAME = "robot.get_atlas_grid_metadata"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ATLAS_ID = "id_atlas";
		}
	}
}
