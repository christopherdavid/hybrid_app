#import <Foundation/Foundation.h>
#import "SchedulerConstants.h"

@interface ScheduleUtils : NSObject
+ (NSString *)scheduleTypeString:(NSString *)scheduleType;
+ (NSInteger)scheduleIntFromString:(NSString *)scheduleType;
+ (Day)dayEnumValue:(int)day;
+ (NSInteger)serverScheduleIntFromString:(NSString *)scheduleType;
@end
