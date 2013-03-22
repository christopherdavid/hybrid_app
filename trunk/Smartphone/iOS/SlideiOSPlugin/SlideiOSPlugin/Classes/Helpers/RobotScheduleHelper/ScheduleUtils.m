#import "ScheduleUtils.h"
#import "NeatoConstants.h"
#import "LogHelper.h"

@implementation ScheduleUtils

+ (NSString *)getScheduleTypeString:(NSString *)scheduleType {
    debugLog(@"");
    if([scheduleType isEqualToString:@"0"]) {
        return NEATO_SCHEDULE_BASIC;
    }
    else if ([scheduleType isEqualToString:@"1"]) {
        return NEATO_SCHEDULE_ADVANCE;
    }
    return nil;
}

+ (Day)getDayEnumValue:(int)day {
    switch (day) {
        case 0:
            return SUNDAY;
        case 1:
            return MONDAY;
        case 2:
            return TUESDAY;
        case 3:
            return WEDNESDAY;
        case 4:
            return THURSDAY;
        case 5:
            return FRIDAY;
        case 6:
            return SATURDAY;
    }
    // It is an error case day should never have a value -1
    return -1;
}

+ (NSInteger)getScheduleIntFromString:(NSString *)scheduleType {
    debugLog(@"");
    if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC]) {
        return NEATO_SCHEDULE_BASIC_INT;
    }
    else if ([scheduleType isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        return NEATO_SCHEDULE_ADVANCE_INT;
    }
    return -1;
}


@end
