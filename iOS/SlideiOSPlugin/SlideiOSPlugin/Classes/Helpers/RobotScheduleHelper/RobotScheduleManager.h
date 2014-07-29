#import <Foundation/Foundation.h>

@class Schedule;

@protocol RobotScheduleManagerProtocol <NSObject>
@optional
- (void)updatedSchedule:(NSString *)scheduleId;
- (void)updateScheduleError:(NSError *)error;
@end

@interface RobotScheduleManager : NSObject

- (id)createScheduleForRobotId:(NSString *)robotId forScheduleType:(NSString *)scheduleType;
- (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId;
- (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData;
- (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
- (id)scheduleEventDataWithScheduleEventId:(NSString *)scheduleEventId withScheduleId:(NSString *)scheduleId;
- (id)scheduleDataForScheduleId:(NSString *)scheduleId;
- (void)updateScheduleForScheduleId:(NSString *)scheduleId delegate:(id)delegate;
- (void)scheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType completion:(RequestCompletionBlockDictionary)completion;
@end
