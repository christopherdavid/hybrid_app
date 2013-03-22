#import "ScheduleEvent.h"
#import "BasicScheduleEvent.h"
#import "AdvanceScheduleEvent.h"

@implementation ScheduleEvent
@synthesize basicScheduleEvents = _basicScheduleEvents, advanceScheduleEvents = _advanceScheduleEvents;

- (void)addBasicScheduleEvents:(NSArray *)scheduleEventsArray {
    if(!self.basicScheduleEvents) {
        self.basicScheduleEvents = [[NSMutableArray alloc]initWithArray:scheduleEventsArray];
    }
}

- (void)addBasicScheduleEvent:(BasicScheduleEvent *)scheduleEvent {
    if(!self.basicScheduleEvents) {
        self.basicScheduleEvents = [[NSMutableArray alloc]init];
    }
    
    [self.basicScheduleEvents addObject:scheduleEvent];
}


@end
