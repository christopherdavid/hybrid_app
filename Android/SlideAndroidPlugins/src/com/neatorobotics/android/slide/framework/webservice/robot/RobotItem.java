package com.neatorobotics.android.slide.framework.webservice.robot;

import java.util.ArrayList;

import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

public class RobotItem {
    public String id;
    public String name;
    public String serial_number;
    public String chat_id;

    public ArrayList<UserItem> users = new ArrayList<UserItem>();

    public int getAssociateUserCount() {
        return users.size();
    }

    public String toString() {
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
