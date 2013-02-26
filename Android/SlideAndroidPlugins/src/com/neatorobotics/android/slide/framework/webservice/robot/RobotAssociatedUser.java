package com.neatorobotics.android.slide.framework.webservice.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotAssociatedUser {
	@JsonProperty(value="id")
	public String mId;
	
	@JsonProperty(value="name")
	public String mName;	

	@JsonProperty(value="email")
	public String mSerialNumber;
	
	@JsonProperty(value="mEmail")
	public String mChat_id;
}
