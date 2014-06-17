#import "ScheduleJsonHelper.h"
#import "SchedulerConstants.h"
#import "BasicScheduleEvent.h"
#import "AppHelper.h"
#import "LogHelper.h"

@implementation ScheduleJsonHelper
+ (NSString *)jsonFromSchedule:(Schedule *)schedule {
    debugLog(@"");
    NSMutableDictionary *scheduleGroup = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *scheduleDict = [[NSMutableDictionary alloc] init];
    NSMutableArray *eventsArray = [[NSMutableArray alloc] init];
    [scheduleDict setValue:schedule.scheduleId forKey:KEY_SCHEDULE_UUID];
    for (int i = 0 ; i < [schedule.scheduleEvent.basicScheduleEvents count]; i++) {
        BasicScheduleEvent *basicScheduleEvent = [schedule.scheduleEvent.basicScheduleEvents objectAtIndex:i];
        [eventsArray addObject:[AppHelper parseJSON:[basicScheduleEvent.parameterStr dataUsingEncoding:NSUTF8StringEncoding]]];
    }
    [scheduleDict setValue:eventsArray forKey:KEY_EVENTS];
    [scheduleGroup setValue:scheduleDict forKey:KEY_SCHEDULE_GROUP];
    debugLog(@"Json string generated for schedule : %@",[AppHelper jsonStringFromNSDictionary:scheduleGroup]);
    return [AppHelper jsonStringFromNSDictionary:scheduleGroup];
}

+ (Schedule *)scheduleFromDictionary:(NSDictionary *)dict {
    debugLog(@"");
    // We expect schedule_data to be a JSON string,
    // but we may get "0" if schedule data is not present on server.
    id scheduleDataValue = [dict valueForKey:KEY_SCHEDULE_DATA];
    if ([scheduleDataValue isKindOfClass:[NSString class]]) {
        NSString *scheduleDataString = (NSString *)scheduleDataValue;
        Schedule *schedule = [[Schedule alloc] init];
        schedule.scheduleEvent = [[ScheduleEvent alloc] init];
        schedule.serverScheduleId = [dict valueForKey:SCHEDULE_ID];
        schedule.scheduleType = [dict valueForKey:SCHEDULE_TYPE];
        schedule.scheduleVersion = [dict valueForKey:KEY_SCHEDULE_VERSION];
        NSDictionary *scheduleData = [AppHelper parseJSON:[scheduleDataString dataUsingEncoding:NSUTF8StringEncoding]];
        NSDictionary *scheduleGroup = [scheduleData valueForKey:KEY_SCHEDULE_GROUP];
        schedule.scheduleId = [scheduleGroup valueForKey:KEY_SCHEDULE_UUID];
        NSArray *events = [scheduleGroup valueForKey:KEY_EVENTS];
        for (int i = 0 ; i < [events count] ; i++) {
            BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] initWithDictionary:[events objectAtIndex:i]];
            [schedule.scheduleEvent addBasicScheduleEvent:basicScheduleEvent];
        }
        return schedule;
    }
    else {
        return nil;
    }
}
@end
