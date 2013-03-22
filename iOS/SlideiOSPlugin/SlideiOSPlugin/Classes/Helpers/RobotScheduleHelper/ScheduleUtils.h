#import <Foundation/Foundation.h>
#import "SchedulerConstants.h"

@interface ScheduleUtils : NSObject
+ (NSString *)getScheduleTypeString:(NSString *)scheduleType;
+ (NSInteger)getScheduleIntFromString:(NSString *)scheduleType;
+ (Day)getDayEnumValue:(int)day;
@end
