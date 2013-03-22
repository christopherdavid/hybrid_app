
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
