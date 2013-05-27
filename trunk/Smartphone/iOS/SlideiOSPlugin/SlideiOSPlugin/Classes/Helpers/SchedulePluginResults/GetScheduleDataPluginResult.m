#import "GetScheduleDataPluginResult.h"
#import "PluginConstants.h"
#import "Schedule.h"
#import "ScheduleEvent.h"
#import "BasicScheduleEvent.h"
#import "ScheduleXMLHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"

@implementation GetScheduleDataPluginResult
@synthesize scheduleId = _scheduleId;
@synthesize scheduleType = _scheduleType;
@synthesize schedule = _schedule;

- (NSMutableDictionary *)toDictionary {
    debugLog(@"");
    NSMutableDictionary *jsonObject = [[NSMutableDictionary alloc] init];
    [jsonObject setObject:[AppHelper getEmptyStringIfNil:self.scheduleId] forKey:KEY_SCHEDULE_ID];
    [jsonObject setObject:[NSNumber numberWithInteger:self.scheduleType] forKey:KEY_SCHEDULE_TYPE];
    NSMutableArray *schedules = [[NSMutableArray alloc] init];
    for (int i=0; i < [self.schedule.scheduleEvent.basicScheduleEvents count]; i++) {
        NSMutableDictionary *schedule = [[NSMutableDictionary alloc] init];
        BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] initWithDictionary:[AppHelper parseJSON:[[[self.schedule.scheduleEvent.basicScheduleEvents objectAtIndex:i] parameterStr] dataUsingEncoding:NSUTF8StringEncoding]]];
        
        [schedule setObject:[NSNumber numberWithInt:basicScheduleEvent.day] forKey:KEY_DAY];
        [schedule setObject:[AppHelper getEmptyStringIfNil:[basicScheduleEvent.startTime toString]] forKey:KEY_START_TIME];
        [schedule setObject:[AppHelper getEmptyStringIfNil:basicScheduleEvent.cleaningMode] forKey:KEY_CLEANING_MODE];
        [schedules addObject:schedule];
    }
    [jsonObject setObject:schedules forKey:KEY_SCHEDULES];
    return jsonObject;
}
@end
