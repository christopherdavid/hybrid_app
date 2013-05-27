#import "ScheduleUtils.h"
#import "NeatoConstants.h"
#import "LogHelper.h"

// For server basic schedule is 1 and advance schedule is 2.
#define NEATO_SCHEDULE_ADVANCE_SERVER_INT 2
#define NEATO_SCHEDULE_BASIC_SERVER_INT 1

@implementation ScheduleUtils

+ (NSString *)scheduleTypeString:(NSString *)scheduleType {
    debugLog(@"");
    if([scheduleType isEqualToString:@"0"]) {
        return NEATO_SCHEDULE_BASIC;
    }
    else if ([scheduleType isEqualToString:@"1"]) {
        return NEATO_SCHEDULE_ADVANCE;
    }
    return nil;
}

+ (Day)dayEnumValue:(int)day {
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

+ (NSInteger)scheduleIntFromString:(NSString *)scheduleType {
    debugLog(@"");
    if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC]) {
        return NEATO_SCHEDULE_BASIC_INT;
    }
    else if ([scheduleType isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        return NEATO_SCHEDULE_ADVANCE_INT;
    }
    return -1;
}

+ (NSInteger)serverScheduleIntFromString:(NSString *)scheduleType {
    if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC]) {
        return NEATO_SCHEDULE_BASIC_SERVER_INT;
    }
    else if ([scheduleType isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        return NEATO_SCHEDULE_ADVANCE_SERVER_INT;
    }
    return -1; 
}

@end
