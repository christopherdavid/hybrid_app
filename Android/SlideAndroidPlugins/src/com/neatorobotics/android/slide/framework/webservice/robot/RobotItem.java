package com.neatorobotics.android.slide.framework.webservice.robot;

import java.util.ArrayList;

public class RobotItem {
	public String id;
	public String name;	
	public String serial_number;
	public String chat_id;
	public String chatPwd;
	public ArrayList<String> users = new ArrayList<String>();
	
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
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("******RobotItem******\n");
		sb.append("id = ");
		sb.append(id);
		sb.append("\n");
		
		sb.append("name = ");
		sb.append(name);
		sb.append("\n");
		
		sb.append("serialNumber = ");
		sb.append(serial_number);
		sb.append("\n");
		
		sb.append("chatId = ");
		sb.append(chat_id);
		sb.append("\n");
		return sb.toString();
	}
	
}
