#import "ScheduleEvent.h"
#import "BasicScheduleEvent.h"

@implementation ScheduleEvent

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
