package com.neatorobotics.android.slide.framework.webservice.user;

import java.util.ArrayList;

import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class UserItem {

	public String id;
	public String name;	
	public String email;
	public String chat_id;
	public String chat_pwd;	
	public ArrayList<String> social_networks; // = new ArrayList<String>();	
	
	public ArrayList<RobotItem> robots = new ArrayList<RobotItem>();
	
	public String alternate_email;
	public int validation_status;
	public UserParam extra_param;
	
	public class UserParam {
		public String countryCode;
		public String optIn;
	}
	
	public void addAssociatedRobot(RobotItem robot)	{
		robots.add(robot);
	}
	
	public RobotItem getAssociateRobot(int index)
	{
		int size = robots.size();
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("getAssociatedRobot Index is out of bound");
		}
		return robots.get(index);
	}
	
	public int getAssociateRobotCount()
	{
		return robots.size();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name = " + name);
		sb.append("\nEmail = " + email);
		sb.append("\nid = " + id);
		sb.append("\nchatId = " + chat_id);
		return sb.toString();
	}
	

}
