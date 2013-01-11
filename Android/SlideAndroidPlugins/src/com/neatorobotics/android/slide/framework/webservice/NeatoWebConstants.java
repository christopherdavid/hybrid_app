package com.neatorobotics.android.slide.framework.webservice;

public class NeatoWebConstants {

	public static final int PROD_SERVER_ID = 1001;
	private static final String PROD_BASE_JSON_URL = "http://neato.rajatogo.com/api/rest/json";
	private static final String PROD_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String PROD_XMPP_SERVER_DOMAIN = "rajatogo.com";

	public static final int STAGING_SERVER_ID = 1002;
	private static final String STAGING_BASE_JSON_URL = "http://neatostaging.rajatogo.com/api/rest/json";
	private static final String STAGING_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String STAGING_XMPP_SERVER_DOMAIN = "rajatogo.com";

	public static final int DEV_SERVER_ID = 1003;
	private static final String DEV_BASE_JSON_URL = "http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json";
	private static final String DEV_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
	private static final String DEV_XMPP_SERVER_DOMAIN = "rajatogo.com";

	public static final String QUERY_KEY_METHOD = "method";
	public static final String QUERY_KEY_APIKEY = "api_key";
	//  Responses after a http requests
	public static final int RESPONSE_NETWORK_ERROR = 100;	
	public static final int RESPONSE_SERVER_ERROR_REASON_UNKNOWN = -1;
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

		}
		return null;
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
		}
		return null;
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
		}
		return null;
	}

}
