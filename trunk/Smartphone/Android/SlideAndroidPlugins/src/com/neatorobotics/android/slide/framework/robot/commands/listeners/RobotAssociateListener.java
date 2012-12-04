package com.neatorobotics.android.slide.framework.robot.commands.listeners;

public interface RobotAssociateListener {
	
	public void associationSuccess();
	public void associationError(String errorMessage);

}
