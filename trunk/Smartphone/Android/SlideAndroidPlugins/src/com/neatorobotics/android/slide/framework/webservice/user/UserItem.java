package com.neatorobotics.android.slide.framework.webservice.user;

import java.util.ArrayList;

import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class UserItem {

	private String id;
	private String name;	
	private String email;
	private String chatId;
	private String chatPwd;
	private ArrayList<String> social_networks = new ArrayList<String>();	
	// TODO : Add list of robot items details associated. Right now only adding the robot names.
	
	private ArrayList<RobotItem> robots = new ArrayList<RobotItem>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public String getChatPwd() {
		return chatPwd;
	}
	public void setChatPwd(String chatPwd) {
		this.chatPwd = chatPwd;
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
		sb.append("\nchatId = " + chatId);
		return sb.toString();
	}
	

}
