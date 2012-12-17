package com.neatorobotics.android.slide.framework.pluginhelper;


//JSON data keys which will be used by javasacript to send the data in json array object.
public class JsonMapKeys {
	
	// Used by user plugin
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_AUTH_TOKEN = "auth_token";
	// Used by robot plugin
	public static final String KEY_COMMAND_ID = "commandId";
	public static final String KEY_COMMAND_PARAMETERS = "commandParams";
	public static final String KEY_ROBOT_ID = "robotId";
	public static final String KEY_USE_XMPP = "useXMPP";
	public static final String KEY_ROBOT_NAME = "robot_name";
	public static final String KEY_ROBOT_IP_ADDRESS = "robot_ipaddress";
	
	
	//Used for error
	public static final String KEY_ERROR_CODE = "errorCode";
	public static final String KEY_ERROR_MESSAGE = "errorMessage";
	
	//Used by scheduler
	public static final String KEY_DAY = "day";
	public static final String KEY_START_TIME_HRS = "startTimeHrs";
	public static final String KEY_END_TIME_HRS = "endTimeHrs";
	public static final String KEY_START_TIME_MINS = "startTimeMins";
	public static final String KEY_END_TIME_MINS = "endTimeMins";
	public static final String KEY_EVENT_TYPE = "eventType";
	public static final String KEY_AREA = "area";
	
	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";
	
	
	public static final String KEY_SCHEDULE_TYPE = "scheduletype";
	public static final String KEY_SCHEDULES = "schedules";
	public static final String KEY_SCHEDULE_ID = "scheduleId";
	
	//Used by robot map
	
	public static final String KEY_ROBOT_MAP_ID = "mapId";
	public static final String KEY_MAP_OVERLAY_INFO = "mapOverlayInfo";
	public static final String KEY_MAP_IMAGE = "mapImage";

	
	public static final String KEY_ROOMMAP = "RoomMap";
	public static final String KEY_NO_GO_MAP = "NoGoMap";
	public static final String KEY_BASE_STATION_MAP = "BaseStationMap";
	public static final String KEY_DIRT_MAP = "DirtMap";
	public static final String KEY_EXPLORED_MAP = "ExploredMap";
	public static final String KEY_VISIBLE_MAP = "VisibleMap";
	
	public static final String KEY_POINT_X = "x";
	public static final String KEY_POINT_Y = "y";
	
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_ICON = "icon";
	public static final String KEY_COLOR = "color";
	public static final String KEY_SET_COLOR = "setColor";
	public static final String KEY_BOUNDING_BOX = "boundingBox";
	public static final String KEY_COORDINATES = "coords";
	public static final String ICON_DEFAULT_VALUE = "ICON.KITCHEN";
	public static final String COLOR_DEFAULT_VALUE = "#FF8080";
	public static final String SET_COLOR_DEFAULT_VALUE = "#FF0000";
	public static final String KEY_ROOMS = "rooms";
	public static final String KEY_GEOGRAPHIES = "geographies";

}
