package com.neatorobotics.android.slide.framework.webservice.robot;

public class NeatoRobotWebServicesAttributes {


	public static final String ACCOUNT_TYPE_NATIVE = "Native";
	public static final String ACCOUNT_TYPE_FACEBOOK = "Facebook";
	public static final String ACCOUNT_TYPE_GOOGLE = "Google";

	public static class CreateNeatoRobot {
		public static final String METHOD_NAME = "robot.create";

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String NAME = "name";
			public static final String SERIAL_NUMBER = "serial_number";
		}
	}

	public static class AssociateNeatoRobotToUser {
		public static final String METHOD_NAME = "robot.set_user"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String EMAIL = "email";
			public static final String SERIAL_NUMBER = "serial_number";
		}

	}
	
	public static class DisassociateNeatoRobotToUser {
		public static final String METHOD_NAME = "robot.disassociate_user"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String EMAIL = "email";
			public static final String SERIAL_NUMBER = "serial_number";
		}

	}

	public static class GetRobotDetails {
		public static final String METHOD_NAME = "robot.get_details"; 

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String SERIAL_NUMBER = "serial_number";
		}

	}



}