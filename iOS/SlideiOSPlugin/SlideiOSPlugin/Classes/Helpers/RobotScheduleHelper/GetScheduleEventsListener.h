#import <Foundation/Foundation.h>
#import "Schedule.h"

@protocol GetScheduleEventsListenerProtocol <NSObject>

@optional
- (void)gotScheduleEventsForSchedule:(Schedule *)schedule ofType:(NSInteger)scheduleType forRobotWithId:(NSString *)robotId;

@end

@interface GetScheduleEventsListener : NSObject

@property(nonatomic, retain) NSString *robotId;
@property(nonatomic, retain) NSString *scheduleType;

- (id)initWithDelegate:(id<GetScheduleEventsListenerProtocol>) delegate;
- (void)start;

@end
