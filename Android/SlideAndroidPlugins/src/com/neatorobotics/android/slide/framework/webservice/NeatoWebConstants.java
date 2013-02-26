package com.neatorobotics.android.slide.framework.webservice;


public class NeatoWebConstants {

	public static final int PROD_SERVER_ID = 1001;
	private static final String PROD_BASE_JSON_URL = "http://neato.rajatogo.com/api/rest/json";
	private static final String PROD_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String PROD_XMPP_SERVER_DOMAIN = "rajatogo.com";
	private static final String PROD_SERVER_URL = "http://neato.rajatogo.com";

	public static final int STAGING_SERVER_ID = 1002;
	private static final String STAGING_BASE_JSON_URL = "http://neatostaging.rajatogo.com/api/rest/json";
	private static final String STAGING_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String STAGING_XMPP_SERVER_DOMAIN = "rajatogo.com";
	private static final String STAGING_SERVER_URL = "http://neatostaging.rajatogo.com";
	
	
	public static final int DEV_SERVER_ID = 1003;
	private static final String DEV_BASE_JSON_URL = "http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json";
	private static final String DEV_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String DEV_XMPP_SERVER_DOMAIN = "rajatogo.com";
	private static final String DEV_SERVER_URL = "http://neatodev.rajatogo.com/Server_Yii/Neato";
	
	
	public static final int DEMO_SERVER_ID = 1004;
	private static final String DEMO_BASE_JSON_URL = "http://neatodemo.rajatogo.com/api/rest/json";
	private static final String DEMO_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String DEMO_XMPP_SERVER_DOMAIN = "rajatogo.com";
	private static final String DEMO_SERVER_URL = "http://neatodemo.rajatogo.com";
	

	public static final String QUERY_KEY_METHOD = "method";
	public static final String QUERY_KEY_APIKEY = "api_key";
	//  Responses after a http requests
	public static final int RESPONSE_NETWORK_ERROR = 100;	
	public static final int RESPONSE_SERVER_ERROR_REASON_UNKNOWN = -1;
	public static final int RESPONSE_SERVER_ERROR_JSON_PARSING = -2;
	public static final int RESPONSE_SERVER_ERROR = 101;
	public static final int RESPONSE_SUCCESS = 0;

	private static int s_CurrentServerCode = STAGING_SERVER_ID;
	
	public static int getServerId() {
		return s_CurrentServerCode;
	}

	public static String getBaseJsonUrl() {
		switch (s_CurrentServerCode) {
		case PROD_SERVER_ID:
			return PROD_BASE_JSON_URL;
		case STAGING_SERVER_ID:
			return STAGING_BASE_JSON_URL;
		case DEV_SERVER_ID:
			return DEV_BASE_JSON_URL;
		case DEMO_SERVER_ID:
			return DEMO_BASE_JSON_URL;

		}
		return PROD_BASE_JSON_URL;
	}

	public static void setServerEnvironment(int environment)
	{
		s_CurrentServerCode = environment;
	}
	
	public static String getApiKey() {
		switch (s_CurrentServerCode) {
		case PROD_SERVER_ID:
			return PROD_API_KEY;
		case STAGING_SERVER_ID:
			return STAGING_API_KEY;
		case DEV_SERVER_ID:
			return DEV_API_KEY;
		case DEMO_SERVER_ID:
			return DEMO_API_KEY;
		}
		return PROD_API_KEY;
	}
	
	public static String getServerName() {
		String serverName = "Production";
		
		switch (s_CurrentServerCode) {
			case NeatoWebConstants.DEV_SERVER_ID:
				serverName = "Development";
				break;
				
			case NeatoWebConstants.STAGING_SERVER_ID:
				serverName = "Staging";
				break;
			case NeatoWebConstants.PROD_SERVER_ID:
				serverName = "Production";
				break;
			case DEMO_SERVER_ID:
				serverName = "Demo";
				break;
		}
		
		return serverName;
	}
	
	public static String getServerUrl() {
		String serverUrl = PROD_SERVER_URL;
		
		switch (s_CurrentServerCode) {
			case NeatoWebConstants.DEV_SERVER_ID:
				serverUrl = DEV_SERVER_URL;
				break;
			case NeatoWebConstants.STAGING_SERVER_ID:
				serverUrl = STAGING_SERVER_URL;
				break;
			case NeatoWebConstants.PROD_SERVER_ID:
				serverUrl = PROD_SERVER_URL;
				break;
			case DEMO_SERVER_ID:
				serverUrl = DEMO_SERVER_URL;
				break;
		}
		
		return serverUrl;
	}
	
	public static String getXmppServerDomain()
	{
		switch (s_CurrentServerCode) {
		case PROD_SERVER_ID:
			return PROD_XMPP_SERVER_DOMAIN;
		case STAGING_SERVER_ID:
			return STAGING_XMPP_SERVER_DOMAIN;
		case DEV_SERVER_ID:
			return DEV_XMPP_SERVER_DOMAIN;
		case DEMO_SERVER_ID:
			return DEMO_XMPP_SERVER_DOMAIN;
		}
		return PROD_XMPP_SERVER_DOMAIN;
	}

}
