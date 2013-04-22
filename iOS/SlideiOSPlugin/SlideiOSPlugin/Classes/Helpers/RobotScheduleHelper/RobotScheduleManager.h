#import <Foundation/Foundation.h>

@class Schedule;

@protocol RobotScheduleManagerProtocol <NSObject>

@optional

- (void)setScheduleSuccess:(NSString *)message;
- (void)getScheduleSuccess:(NSDictionary *)jsonObject;
-( void)deleteScheduleSuccess:(NSString *)message;
- (void)updatedSchedule:(NSString *)scheduleId;
- (void)setScheduleError:(NSError *)error;
- (void)getScheduleError:(NSError *)error;
- (void)deleteScheduleError:(NSError *)error;
- (void)updateScheduleError:(NSError *)error;
- (void)failedToGetScheduleEventsForRobotWithId:(NSString *)robotId error:(NSError *)error;
- (void)gotScheduleEventsForSchedule:(Schedule *)schedule ofType:(NSInteger)scheduleType forRobotWithId:(NSString *)robotId;

@end

@interface RobotScheduleManager : NSObject

- (id)createScheduleForRobotId:(NSString *)robotId forScheduleType:(NSString *)scheduleType;
- (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId;
- (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData;
- (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
- (id)getSchedueEventDataWithScheduleEventId:(NSString *)scheduleEventId withScheduleId:(NSString *)scheduleId;
- (id)getScheduleDataForScheduleId:(NSString *)scheduleId;
- (void)getScheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType delegate:(id<RobotScheduleManagerProtocol>)delgate;
- (void)updateScheduleForScheduleId:(NSString *)scheduleId delegate:(id)delegate;

- (void)setRobotSchedule:(NSArray *)schedulesArray forRobotId:(NSString *)robotId ofType:(NSString *)scheduleType delegate:(id)delegate;
- (void)getSchedulesForRobotId:(NSString *)robotId OfType:(NSString *)scheduleType delegate:(id)delegate;
- (void)deleteScheduleForRobotId:(NSString *)robotId OfType:(NSString *)scheduleType delegate:(id)delegate;
@end