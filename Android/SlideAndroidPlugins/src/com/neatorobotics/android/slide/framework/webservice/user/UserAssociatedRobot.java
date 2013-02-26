package com.neatorobotics.android.slide.framework.webservice.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAssociatedRobot {
	@JsonProperty(value="id")
	public String mId;
	
	@JsonProperty(value="name")
	public String mName;	

	@JsonProperty(value="serial_number")
	public String mSerialNumber;
	
	@JsonProperty(value="chat_id")
	public String mChat_id;
}
