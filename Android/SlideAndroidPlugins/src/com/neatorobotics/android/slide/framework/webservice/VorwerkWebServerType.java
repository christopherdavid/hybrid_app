package com.neatorobotics.android.slide.framework.webservice;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class VorwerkWebServerType extends WebServerType {

    private static final String SERVER_DEMO2 = "Demo2 (Vorwerk)";
    private static final String SERVER_DEMO = "Demo (Vorwerk)";
    private static final String SERVER_STAGING = "Staging (Vorwerk)";
    private static final String SERVER_DEVELOPMENT = "Development (Vorwerk)";
    private static final String SERVER_PRODUCTION = "Production (Vorwerk)";
    private static final String SERVER_WP = "WP (Vorwerk)";
    private static final String SERVER_NEATO_SECURE = "Secure Server (Vorwerk)";
    private static final String SERVER_VORWERK_BETA = "Beta (Vorwerk)";

    final String PROD_BASE_JSON_URL = "http://neato.rajatogo.com/api/rest/json";
    final String PROD_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String PROD_XMPP_SERVER_DOMAIN = "rajatogo.com";
    final String PROD_SERVER_URL = "http://neato.rajatogo.com";
    final String PROD_XMPP_WEBSERVER = "rajatogo";

    final String STAGING_BASE_JSON_URL = "http://neatostaging.rajatogo.com/api/rest/json";
    final String STAGING_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String STAGING_XMPP_SERVER_DOMAIN = "rajatogo.com";
    final String STAGING_SERVER_URL = "http://neatostaging.rajatogo.com";
    final String STAGING_XMPP_WEBSERVER = "rajatogo";

    final String DEV_BASE_JSON_URL = "http://neatodev.rajatogo.com/api/rest/json";
    final String DEV_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String DEV_XMPP_SERVER_DOMAIN = "rajatogo.com";
    final String DEV_SERVER_URL = "http://neatodev.rajatogo.com";
    final String DEV_XMPP_WEBSERVER = "rajatogo";

    final String DEMO_BASE_JSON_URL = "http://neatodemo.rajatogo.com/api/rest/json";
    final String DEMO_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String DEMO_XMPP_SERVER_DOMAIN = "rajatogo.com";
    final String DEMO_SERVER_URL = "http://neatodemo.rajatogo.com";
    final String DEMO_XMPP_WEBSERVER = "rajatogo";

    final String DEMO2_BASE_JSON_URL = "http://neatodemo2.rajatogo.com/api/rest/json";
    final String DEMO2_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String DEMO2_XMPP_SERVER_DOMAIN = "neatodemo2.rajatogo.com";
    final String DEMO2_SERVER_URL = "http://neatodemo2.rajatogo.com";
    final String DEMO2_XMPP_WEBSERVER = "rajatogo";

    final String WP_BASE_JSON_URL = "http://neatowp.rajatogo.com/api/rest/json";
    final String WP_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String WP_XMPP_SERVER_DOMAIN = "neatowp.rajatogo.com";
    final String WP_SERVER_URL = "http://neatowp.rajatogo.com";
    final String WP_XMPP_WEBSERVER = "rajatogo";

    final String VW_SECURE_BASE_JSON_URL = "https://neatosecure.rajatogo.com/api/rest/json";
    final String VW_SECURE_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String VW_SECURE_XMPP_SERVER_DOMAIN = "rajatogo.com";
    final String VW_SECURE_SERVER_URL = "https://neatosecure.rajatogo.com";
    final String VW_SECURE_XMPP_WEBSERVER = "rajatogo";

    final String VW_BETA_BASE_JSON_URL = "http://server-01.fut.emea.vr200.ksecosys.net/api/rest/json";
    final String VW_BETA_API_KEY = "1e26686d806d82144a71ea9a99d1b3169adaad917";
    final String VW_BETA_XMPP_SERVER_DOMAIN = "server-01.fut.emea.vr200.ksecosys.net";
    final String VW_BETA_SERVER_URL = "http://server-01.fut.emea.vr200.ksecosys.net";
    final String VW_BETA_XMPP_WEBSERVER = "server-01.fut.emea.vr200.ksecosys.net";

    @Override
    public String getBaseJsonUrl() {
        LogHelper.logD(TAG, "getBaseJsonUrl called");
        switch (getCurrentServerCode()) {
            case NeatoWebConstants.PROD_SERVER_ID:
                return PROD_BASE_JSON_URL;
            case NeatoWebConstants.STAGING_SERVER_ID:
                return STAGING_BASE_JSON_URL;
            case NeatoWebConstants.DEV_SERVER_ID:
                return DEV_BASE_JSON_URL;
            case NeatoWebConstants.DEMO_SERVER_ID:
                return DEMO_BASE_JSON_URL;
            case NeatoWebConstants.DEMO2_SERVER_ID:
                return DEMO2_BASE_JSON_URL;
            case NeatoWebConstants.WP_SERVER_ID:
                return WP_BASE_JSON_URL;
            case NeatoWebConstants.NEATO_SECURE_SERVER_ID:
                return VW_SECURE_BASE_JSON_URL;
            case NeatoWebConstants.VW_BETA_SERVER_ID:
                return VW_BETA_BASE_JSON_URL;

        }
        return PROD_BASE_JSON_URL;
    }

    @Override
    public String getServerName() {
        LogHelper.logD(TAG, "getServerName called");
        String serverName = SERVER_PRODUCTION;

        switch (getCurrentServerCode()) {
            case NeatoWebConstants.DEV_SERVER_ID:
                serverName = SERVER_DEVELOPMENT;
                break;
            case NeatoWebConstants.STAGING_SERVER_ID:
                serverName = SERVER_STAGING;
                break;
            case NeatoWebConstants.PROD_SERVER_ID:
                serverName = SERVER_PRODUCTION;
                break;
            case NeatoWebConstants.DEMO_SERVER_ID:
                serverName = SERVER_DEMO;
                break;
            case NeatoWebConstants.DEMO2_SERVER_ID:
                serverName = SERVER_DEMO2;
                break;
            case NeatoWebConstants.WP_SERVER_ID:
                serverName = SERVER_WP;
                break;
            case NeatoWebConstants.NEATO_SECURE_SERVER_ID:
                serverName = SERVER_NEATO_SECURE;
                break;
            case NeatoWebConstants.VW_BETA_SERVER_ID:
                serverName = SERVER_VORWERK_BETA;
                break;
        }

        return serverName;
    }

    @Override
    public String getServerUrl() {
        LogHelper.logD(TAG, "getServerUrl called");
        String serverUrl = PROD_SERVER_URL;

        switch (getCurrentServerCode()) {
            case NeatoWebConstants.DEV_SERVER_ID:
                serverUrl = DEV_SERVER_URL;
                break;
            case NeatoWebConstants.STAGING_SERVER_ID:
                serverUrl = STAGING_SERVER_URL;
                break;
            case NeatoWebConstants.PROD_SERVER_ID:
                serverUrl = PROD_SERVER_URL;
                break;
            case NeatoWebConstants.DEMO_SERVER_ID:
                serverUrl = DEMO_SERVER_URL;
                break;
            case NeatoWebConstants.DEMO2_SERVER_ID:
                serverUrl = DEMO2_SERVER_URL;
                break;
            case NeatoWebConstants.WP_SERVER_ID:
                serverUrl = WP_SERVER_URL;
                break;
            case NeatoWebConstants.NEATO_SECURE_SERVER_ID:
                serverUrl = VW_SECURE_SERVER_URL;
                break;
            case NeatoWebConstants.VW_BETA_SERVER_ID:
                serverUrl = VW_BETA_SERVER_URL;
                break;
        }

        return serverUrl;
    }

    @Override
    public String getApiKey() {
        LogHelper.logD(TAG, "getApiKey called");
        switch (getCurrentServerCode()) {
            case NeatoWebConstants.PROD_SERVER_ID:
                return PROD_API_KEY;
            case NeatoWebConstants.STAGING_SERVER_ID:
                return STAGING_API_KEY;
            case NeatoWebConstants.DEV_SERVER_ID:
                return DEV_API_KEY;
            case NeatoWebConstants.DEMO_SERVER_ID:
                return DEMO_API_KEY;
            case NeatoWebConstants.DEMO2_SERVER_ID:
                return DEMO2_API_KEY;
            case NeatoWebConstants.WP_SERVER_ID:
                return WP_API_KEY;
            case NeatoWebConstants.NEATO_SECURE_SERVER_ID:
                return VW_SECURE_API_KEY;
            case NeatoWebConstants.VW_BETA_SERVER_ID:
                return VW_BETA_API_KEY;
        }
        return PROD_API_KEY;
    }

    @Override
    public String getXmppServerDomain() {
        LogHelper.logD(TAG, "getXmppServerDomain called");
        switch (getCurrentServerCode()) {
            case NeatoWebConstants.PROD_SERVER_ID:
                return PROD_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.STAGING_SERVER_ID:
                return STAGING_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.DEV_SERVER_ID:
                return DEV_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.DEMO_SERVER_ID:
                return DEMO_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.DEMO2_SERVER_ID:
                return DEMO2_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.WP_SERVER_ID:
                return WP_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.NEATO_SECURE_SERVER_ID:
                return VW_SECURE_XMPP_SERVER_DOMAIN;
            case NeatoWebConstants.VW_BETA_SERVER_ID:
                return VW_BETA_XMPP_SERVER_DOMAIN;
        }
        return PROD_XMPP_SERVER_DOMAIN;
    }

    @Override
    public String getXmppWebServer() {
        LogHelper.logD(TAG, "getXmppWebserver called");
        switch (getCurrentServerCode()) {
            case NeatoWebConstants.PROD_SERVER_ID:
                return PROD_XMPP_WEBSERVER;
            case NeatoWebConstants.STAGING_SERVER_ID:
                return STAGING_XMPP_WEBSERVER;
            case NeatoWebConstants.DEV_SERVER_ID:
                return DEV_XMPP_WEBSERVER;
            case NeatoWebConstants.DEMO_SERVER_ID:
                return DEMO_XMPP_WEBSERVER;
            case NeatoWebConstants.DEMO2_SERVER_ID:
                return DEMO2_XMPP_WEBSERVER;
            case NeatoWebConstants.WP_SERVER_ID:
                return WP_XMPP_WEBSERVER;
            case NeatoWebConstants.NEATO_SECURE_SERVER_ID:
                return VW_SECURE_XMPP_WEBSERVER;
            case NeatoWebConstants.VW_BETA_SERVER_ID:
                return VW_BETA_XMPP_WEBSERVER;
        }
        return PROD_XMPP_SERVER_DOMAIN;
    }

}
