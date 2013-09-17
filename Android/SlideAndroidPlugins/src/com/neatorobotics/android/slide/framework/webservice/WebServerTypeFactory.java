package com.neatorobotics.android.slide.framework.webservice;

public class WebServerTypeFactory {
	static final int SERVER_TYPE_NEATO  = 1;
	static final int SERVER_TYPE_VORWERK  = 2;

	public static final WebServerType getWebServerConfig(int serverType) {
		if (serverType == SERVER_TYPE_NEATO) {
			return new NeatoWebServerType();
		}
		else if (serverType == SERVER_TYPE_VORWERK) {
			return new VorwerkWebServerType();
		}
		else {
			throw new RuntimeException("Unknown Servertype");
		}
	}
}
