package com.neatorobotics.android.slide.framework.webservice;

public abstract class WebServerType {

	protected final String TAG = getClass().getSimpleName();

	private int mCurrentServerCode = NeatoWebConstants.STAGING_SERVER_ID;

	public abstract String getBaseJsonUrl();

	public void setServerEnvironment(int environment)
	{
		mCurrentServerCode = environment;
	}

	public abstract String getServerName();

	public abstract String getServerUrl();

	public abstract String getApiKey();

	public abstract String getXmppServerDomain();

	protected int getCurrentServerCode() {
		return mCurrentServerCode;
	}

}
