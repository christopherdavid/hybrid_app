#import <Foundation/Foundation.h>
@class BasicScheduleEvent;

@interface ScheduleEvent : NSObject
@property(nonatomic, retain) NSMutableArray *basicScheduleEvents;
@property(nonatomic, retain) NSMutableArray *advanceScheduleEvents;

- (void)addBasicScheduleEvents:(NSArray *)scheduleEventsArray;
- (void)addBasicScheduleEvent:(BasicScheduleEvent *)scheduleEvent;
@end
