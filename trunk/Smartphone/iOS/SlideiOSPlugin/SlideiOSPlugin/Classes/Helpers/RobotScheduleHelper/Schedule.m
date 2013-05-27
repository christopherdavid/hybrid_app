#import "Schedule.h"
#import "NeatoConstants.h"
#import "BasicScheduleEvent.h"
#import "LogHelper.h"

@implementation Schedule
@synthesize scheduleId = _scheduleId, scheduleType = _scheduleType, serverScheduleId = _serverScheduleId, scheduleVersion = _scheduleVersion, scheduleEvent = _scheduleEvent;

- (NSArray *)arrayOfScheduleEventIdsForType:(NSInteger)scheduleType {
    NSMutableArray *arrayOfEventIds = [[NSMutableArray alloc] init];
    if (scheduleType == NEATO_SCHEDULE_BASIC_INT) {
        for (BasicScheduleEvent *event in self.scheduleEvent.basicScheduleEvents) {
            [arrayOfEventIds addObject:event.scheduleEventId];
        }
    }
    else if (scheduleType == NEATO_SCHEDULE_ADVANCE_INT) {
        // TODO: needs implementation
    }
    return arrayOfEventIds;
}
@end
