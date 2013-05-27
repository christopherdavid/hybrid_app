
// Constants for schedule API's

#define KEY_AREA            @"area"
#define KEY_EVENT_TYPE      @"eventType"
#define KEY_START_TIME      @"startTime"
#define KEY_END_TIME        @"endTime"
#define KEY_DAY             @"day"


typedef NS_ENUM(NSInteger, Day) {
  SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
};

typedef NS_ENUM(NSInteger, SchedularEvent) {
  QUIET, CLEAN, NONE
};

// Server Schedule JSON Constants
#define SCHEDULE_ID @"schedule_id"
#define SCHEDULE_TYPE @"schedule_type"
#define KEY_SCHEDULE_VERSION @"schedule_version"
#define KEY_SCHEDULE_DATA @"schedule_data"
#define KEY_SCHEDULE_GROUP @"scheduleGroup"
#define KEY_EVENTS @"events"
#define KEY_SCHEDULE_EVENT_ID @"scheduleEventId"
#define KEY_CLEANING_MODE @"cleaningMode"
#define KEY_SCHEDULE_UUID @"scheduleUUID"

#define KEY_ID @"id"
#define KEY_XML_DATA_VERSION @"xml_data_version"

// Cleaning modes.
#define CLEANING_MODE_ECO 1
#define CLEANING_MODE_NORMAL 2

