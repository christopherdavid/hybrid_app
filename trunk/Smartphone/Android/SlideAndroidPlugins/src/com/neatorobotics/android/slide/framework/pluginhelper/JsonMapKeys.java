package com.neatorobotics.android.slide.framework.pluginhelper;


//JSON data keys which will be used by javasacript to send the data in json array object.
public class JsonMapKeys {
	
	public static final String KEY_MESSAGE = "message";
	
	// Used by user plugin
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_AUTH_TOKEN = "authToken";
	public static final String KEY_CURRENT_PASSWORD = "currentPassword";
	public static final String KEY_NEW_PASSWORD = "newPassword";
	public static final String KEY_IS_VALIDATED_USER = "isValidated";
	public static final String KEY_ALTERNATE_EMAIL = "alternate_email";
	public static final String KEY_VALIDATION_STATUS = "validation_status";	
	
	// Used by robot plugin
	public static final String KEY_COMMAND_ID = "commandId";
	public static final String KEY_COMMAND_PARAMETERS = "commandParams";
	public static final String KEY_ROBOT_ID = "robotId";
	public static final String KEY_USE_XMPP = "useXMPP";
	public static final String KEY_ROBOT_NAME = "robotName";
	public static final String KEY_ROBOT_IP_ADDRESS = "robotIpaddress";
	public static final String KEY_ROBOT_ONLINE_STATUS = "online";
	
	// Cleaning API params
	public static final String KEY_SPOT_CLEANING_CATEGORY = "cleaningCategory";
	public static final String KEY_SPOT_CLEANING_AREA_LENGTH = "spotCleaningAreaLength";
	public static final String KEY_SPOT_CLEANING_AREA_HEIGHT = "spotCleaningAreaHeight";

	// Used for notification in robot plugin
	public static final String KEY_RESPONSE_STATUS = "responseStatus";
	public static final String KEY_RESPONSE_PARAMS = "responseParams";
	public static final String KEY_ROBOT_COMMANDS = "robotCommands";
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
	
	public static final String KEY_SCHEDULE_GROUP = "scheduleGroup";
	public static final String KEY_SCHEDULE_UUID = "scheduleUUID";
	public static final String KEY_EVENTS = "events";
	
	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";
	
	public static final String KEY_IS_SCHEDULE_ENABLED = "isScheduleEnabled";
	public static final String KEY_ENABLE_SCHEDULE = "enableSchedule";
	
	public static final String KEY_SCHEDULE_TYPE = "scheduleType";
	public static final String KEY_SCHEDULES = "schedules";
	public static final String KEY_SCHEDULE_ID = "scheduleId";
	public static final String KEY_SCHEDULE_EVENTS_LIST = "scheduleEventLists";
	public static final String KEY_SCHEDULE_EVENT_ID = "scheduleEventId";
	public static final String KEY_SCHEDULE_EVENT_DATA = "scheduleEventData";
	public static final String KEY_CLEANING_MODE = "cleaningMode";

	//Used by robot map
	
	public static final String KEY_ROBOT_MAP_ID = "mapId";
	public static final String KEY_MAP_OVERLAY_INFO = "mapOverlayInfo";
	public static final String KEY_MAP_IMAGE = "mapImage";

	
	// Used by ATLAS 
	public static final String KEY_ATLAS_ID = "atlasId";
	public static final String KEY_ATLAS_VERSION = "atlasVersion";
	public static final String KEY_ALTAS_METADATA = "atlasMetadata";
	public static final String KEY_ATLAS_GRID_ID = "gridId";
	public static final String KEY_ATLAS_GRID_DATA = "gridData";
	
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
	
	// Robot status notification events constants
	private static final int EVENT_ID_BASE  		= 20000; 
	public static final int EVENT_ID_REGISTER 		= EVENT_ID_BASE;
	public static final int EVENT_ID_UNREGISTER 	= EVENT_ID_BASE + 1;
	public static final int EVENT_ID_STATUS		 	= EVENT_ID_BASE + 2;
	
	public static final String KEY_EVENT_NOTIFICATION_ID 		= "eventId";
	public static final String KEY_EVENT_NOTIFICATION_PARAMS 	= "params";
	public static final String KEY_NOTIFICATION_ID  = "notificationId";	
	
	public static final String KEY_REGISTER_RESULT		= "register";
	public static final String KEY_UNREGISTER_RESULT 	= "unregister";
	
	// Used by navigation APIs
	public static final String KEY_NAVIGATION_CONTROL_ID = "navigationControlId";
	
	public static final String KEY_FLAG_ON = "on";
	public static final String KEY_WIFI_TURN_ON_DURATION_INSEC = "wiFiTurnOnDurationInSec";
	
	// Used by notifications APIs
	public static final String KEY_GLOBAL_NOTIFICATIONS = "global";
	public static final String KEY_NOTIFICATIONS = "notifications";
	public static final String KEY_NOTIFICATION_KEY = "key";
	public static final String KEY_NOTIFICATION_VALUE = "value";
	
	// Used by push notification APIs
	public static final String KEY_REGISTER_PUSH_NOTIFICATION = "registerPushNotification";
	public static final String KEY_PUSH_NOTIFICATION_TYPES = "pushNotificationTypes";
	
	public static final String KEY_FLAG_ON_OFF = "flagOnOff";
	// Timed Mode 
	public static final String KEY_EXPECTED_TIME_TO_EXECUTE = "expectedTimeToExecute";
	public static final String KEY_ROBOT_DATA_ID = "robotDataKeyId"; 
	public static final String KEY_ROBOT_DATA = "robotData";
	
	public static final String KEY_ROBOT_CURRENT_STATE = "robotCurrentState";
	public static final String KEY_SCHEDULE_STATE = "scheduleState";
	//Virtual
	public static final String KEY_ROBOT_STATE = "robotState";
	//Update
	public static final String KEY_ROBOT_STATE_UPDATE = "robotStateUpdate";
	public static final String KEY_ROBOT_TIME = "robotTime";
}
