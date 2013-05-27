#import "CreateSchedulePluginResult.h"
#import "PluginConstants.h"
#import "AppHelper.h"

@implementation CreateSchedulePluginResult
@synthesize scheduleId = _scheduleId, scheduleType = _scheduleType, robotId = _robotId;

- (NSMutableDictionary *)toDictionary {
    NSMutableDictionary *jsonObject = [[NSMutableDictionary alloc] init];
    [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.scheduleId] forKey:KEY_SCHEDULE_ID];
    [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.robotId] forKey:KEY_ROBOT_ID];
    [jsonObject setObject:[NSNumber numberWithInteger:self.scheduleType] forKey:KEY_SCHEDULE_TYPE];
    return jsonObject;
}
@end
