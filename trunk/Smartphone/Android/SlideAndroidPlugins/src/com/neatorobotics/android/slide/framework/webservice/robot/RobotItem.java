package com.neatorobotics.android.slide.framework.webservice.robot;

import java.util.ArrayList;



public class RobotItem {
	private String id;
	private String name;	
	private String serialNumber;
	private String chatId;
	private String chatPwd;
	private ArrayList<String> users = new ArrayList<String>();
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
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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
	
	public void addAssociatedUser(String user)
	{
		users.add(user);
	}
	
	public String getAssociateUser(int index)
	{
		int size = users.size();
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("getAssociatedUser Index is out of bound");
		}
		return users.get(index);
	}
	
	public int getAssociateUserCount()
	{
		return users.size();
	}
	
}
