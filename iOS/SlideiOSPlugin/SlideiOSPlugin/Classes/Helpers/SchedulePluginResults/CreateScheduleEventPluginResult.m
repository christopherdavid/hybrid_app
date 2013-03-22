#import "CreateScheduleEventPluginResult.h"
#import "PluginConstants.h"
#import "AppHelper.h"

@implementation CreateScheduleEventPluginResult
@synthesize scheduleId = _scheduleId;
@synthesize scheduleEventId = _scheduleEventId;

- (NSMutableDictionary *)toDictionary {
  NSMutableDictionary *jsonObject = [[NSMutableDictionary alloc] init];
  [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.scheduleId] forKey:KEY_SCHEDULE_ID];
  [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.scheduleEventId] forKey:KEY_SCHEDULE_EVENT_ID];
  return jsonObject;
}

@end
