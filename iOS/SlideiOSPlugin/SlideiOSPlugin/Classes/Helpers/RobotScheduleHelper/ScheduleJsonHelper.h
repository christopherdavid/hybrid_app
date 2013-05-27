#import <Foundation/Foundation.h>
#import "Schedule.h"
#import "BasicScheduleEvent.h"

@interface ScheduleJsonHelper : NSObject
+ (NSString *)jsonFromSchedule:(Schedule *)schedule;
+ (Schedule *)scheduleFromDictionary:(NSDictionary *)dict;
@end
