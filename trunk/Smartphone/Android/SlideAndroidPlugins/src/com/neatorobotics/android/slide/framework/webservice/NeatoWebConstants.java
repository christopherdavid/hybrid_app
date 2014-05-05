package com.neatorobotics.android.slide.framework.webservice;

import com.neatorobotics.android.slide.framework.R;

import android.content.Context;

public class NeatoWebConstants {

    public static final String QUERY_KEY_METHOD = "method";
    public static final String QUERY_KEY_APIKEY = "api_key";
    // Responses after a http requests
    public static final int RESPONSE_NETWORK_ERROR = 100;
    public static final int RESPONSE_SERVER_ERROR_REASON_UNKNOWN = -1;
    public static final int RESPONSE_SERVER_ERROR_JSON_PARSING = -2;
    public static final int RESPONSE_SERVER_ERROR = 101;
    public static final int RESPONSE_SUCCESS = 0;

    public static String getBaseJsonUrl(Context context) {
        return context.getString(R.string.base_server_url);
    }

    public static String getServerName(Context context) {
        return context.getString(R.string.server_name);
    }

    public static String getServerUrl(Context context) {
        return context.getString(R.string.server_url);
    }

    public static String getApiKey(Context context) {
        return context.getString(R.string.api_key);
    }

    public static String getXmppServerDomain(Context context) {
        return context.getString(R.string.xmpp_server_domain);
    }

    public static String getXmppWebServer(Context context) {
        return context.getString(R.string.xmpp_webserver_url);
    }
    
    public static String getCrittercismAppId(Context context) {
        return context.getString(R.string.xmpp_webserver_url);
    }
}
