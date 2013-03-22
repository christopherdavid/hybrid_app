#import "GetScheduleEventDataPluginResult.h"
#import "PluginConstants.h"
#import "BasicScheduleEvent.h"
#import "ScheduleXMLHelper.h"
#import "AppHelper.h"

@implementation GetScheduleEventDataPluginResult
@synthesize scheduleId = _scheduleId;
@synthesize scheduleEventId = _scheduleEventId;
@synthesize basicScheduleEvent = _basicScheduleEvent;

- (NSMutableDictionary *)toDictionary {
  NSMutableDictionary *jsonObject = [[NSMutableDictionary alloc] init];
  [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.scheduleId] forKey:KEY_SCHEDULE_ID];
  [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.scheduleEventId] forKey:KEY_SCHEDULE_EVENT_ID];
  NSMutableDictionary *schedule = [[NSMutableDictionary alloc] init];
  BasicScheduleEvent *scheduleEvent = [ScheduleXMLHelper basicScheduleEventFromString:self.basicScheduleEvent.xmlData];
  [schedule setObject:[AppHelper getEmptyStringIfNil:[NSString stringWithFormat:@"%d", scheduleEvent.day]] forKey:KEY_DAY];
  [schedule setObject:[AppHelper getEmptyStringIfNil:scheduleEvent.startTime.toString] forKey:KEY_START_TIME];
  [jsonObject setObject:schedule forKey:KEY_SCHEDULE_EVENT_DATA];
  return jsonObject;
}

@end
