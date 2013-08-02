package com.neatorobotics.android.slide.framework.webservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NeatoHttpRequestHeader {

	private String osName;

	private String osVersion;

	private String locale;

	private String timezone;

	private String appVersion;
	
	private String appPackage;

	private String deviceName;
	
	@JsonProperty("locale")
	public String getLocale() {
		return locale;
	}

	@JsonProperty("locale")
	public void setLocale(String locale) {
		this.locale = locale;
	}

	@JsonProperty("application_version")
	public void setApplicationVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@JsonProperty("application_version")
	public String getApplicationVersion() {
		return appVersion;
	}

	@JsonProperty("os_name")
	public void setOsName(String osName) {
		this.osName = osName;
	}

	@JsonProperty("os_name")
	public String getOsName() {
		return osName;
	}

	@JsonProperty("os_version")
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	@JsonProperty("os_version")
	public String getOsVersion() {
		return osVersion;
	}

	@JsonProperty("current_time_zone")
	public String getTimezone() {
		return timezone;
	}

	@JsonProperty("current_time_zone")
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@JsonProperty("app_package")
	public String getAppPackage() {
		return appPackage;
	}

	@JsonProperty("app_package")
	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	@JsonProperty("device_name")
	public String getDeviceName() {
		return deviceName;
	}

	@JsonProperty("device_name")
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}
