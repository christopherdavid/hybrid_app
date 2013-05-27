#import <Foundation/Foundation.h>
#import "ScheduleEvent.h"

@interface Schedule : NSObject
@property(nonatomic, retain) NSString *scheduleId;
@property(nonatomic, retain) NSString *serverScheduleId;
@property(nonatomic, retain) NSString *scheduleType;
@property(nonatomic, retain) NSString *scheduleVersion;
@property(nonatomic, retain) ScheduleEvent *scheduleEvent;

- (NSArray *)arrayOfScheduleEventIdsForType:(NSInteger)scheduleType;
@end
