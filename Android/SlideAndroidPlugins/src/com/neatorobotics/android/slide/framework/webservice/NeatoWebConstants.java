package com.neatorobotics.android.slide.framework.webservice;



public class NeatoWebConstants {

	// Uncomment to switch to Neato server
	private static final int SERVER_TYPE = WebServerTypeFactory.SERVER_TYPE_NEATO;
	// Uncomment to switch to Vorwerk server
// 	private static final int SERVER_TYPE = WebServerTypeFactory.SERVER_TYPE_VORWERK;
	
	
	public static final int PROD_SERVER_ID = 1001;
	public static final int STAGING_SERVER_ID = 1002;
	public static final int DEV_SERVER_ID = 1003;
	public static final int DEMO_SERVER_ID = 1004;
	public static final int DEMO2_SERVER_ID = 1005;

	public static final String QUERY_KEY_METHOD = "method";
	public static final String QUERY_KEY_APIKEY = "api_key";
	//  Responses after a http requests
	public static final int RESPONSE_NETWORK_ERROR = 100;	
	public static final int RESPONSE_SERVER_ERROR_REASON_UNKNOWN = -1;
	public static final int RESPONSE_SERVER_ERROR_JSON_PARSING = -2;
	public static final int RESPONSE_SERVER_ERROR = 101;
	public static final int RESPONSE_SUCCESS = 0;

	private static WebServerType webServerType;
	
	
	static {
		webServerType = WebServerTypeFactory.getWebServerConfig(SERVER_TYPE);
	}
	
	public static void initializeServerType(int serverType) {
		webServerType = WebServerTypeFactory.getWebServerConfig(serverType);
	}

	public  static String getBaseJsonUrl() {
		return webServerType.getBaseJsonUrl();
	}
	
	public static void setServerEnvironment(int environment)
	{
		webServerType.setServerEnvironment(environment);
	}

	public static String getServerName() {
		return webServerType.getServerName();
	}
	
	public static String getServerUrl() {
		return webServerType.getServerUrl();
	}
	
	public static String getApiKey() {
		return webServerType.getApiKey();
	}
	
	public static String getXmppServerDomain() {
		return webServerType.getXmppServerDomain();
	}
}
