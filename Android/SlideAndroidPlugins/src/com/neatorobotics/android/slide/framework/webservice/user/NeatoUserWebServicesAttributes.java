package com.neatorobotics.android.slide.framework.webservice.user;

public class NeatoUserWebServicesAttributes {
	
	public static final String ACCOUNT_TYPE_NATIVE = "Native";
	public static final String ACCOUNT_TYPE_FACEBOOK = "Facebook";
	public static final String ACCOUNT_TYPE_GOOGLE = "Google";
		
	public static class CreateNeatoUser {
		public static final String METHOD_NAME = "user.create";

		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ACCOUNT_TYPE = "account_type";
			public static final String NAME = "name";
			public static final String EMAIL = "email";
			public static final String PASSWORD = "password";
			public static final String EXTERNAL_SOCIAL_ID = "external_social_id";
		}
	}

	public static class LoginNeatoUser {
		public static final String METHOD_NAME = "auth.get_user_auth_token"; 
		
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String ACCOUNT_TYPE = "account_type";
			public static final String EMAIL = "email";
			public static final String PASSWORD = "password";
			public static final String EXTERNAL_SOCIAL_ID = "external_social_id";
		}
		
	}
	
	public static class GetNeatoUserDetails {
		public static final String METHOD_NAME = "user.get_user_account_details"; 
		
		public static class Attribute {
			public static final String API_KEY = "api_key";
			public static final String EMAIL = "email";
			public static final String AUTHHENTICATION_TOKEN = "auth_token";
		}
		
	}


}
