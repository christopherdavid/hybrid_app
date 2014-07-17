#import <Foundation/Foundation.h>
@class PostScheduleResult;

@protocol ScheduleServerHelperProtocol <NSObject>

@optional
- (void)gotSchedulesData:(id)scheduleData forRobotId:(NSString *)robotId;
- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error;

- (void)gotScheduleData:(id)scheduleData forScheduleId:(NSString *)scheduleId;
- (void)failedToGetScheduleDataForScheduleId:(NSString *)scheduleId withError:(NSError *)error;

- (void)postedSchedule:(PostScheduleResult *)message;
- (void)postScheduleError:(NSError *)error;

- (void)updatedScheduleWithResult:(id)result;
- (void)updateScheduleError:(NSError *)error;

- (void)deletedScheduleData:(NSString *)message;
- (void)deleteScheduleDataError:(NSError *)error;
@end

@interface ScheduleServerHelper : NSObject

@property(nonatomic, weak) id delegate;

- (void)getSchedulesForRobotWithId:(NSString *)robotId;
- (void)getDataForScheduleWithId:(NSString *)scheduleId;

- (void)postScheduleForRobotId:(NSString *)robotId withScheduleData:(NSString *)xmlData ofScheduleType:(NSString *)scheduleType;
- (void)updateScheduleDataForScheduleId:(NSString *)scheduleId withScheduleVersion:(NSString *)scheduleVersion withScheduleData:(NSString *)data ofScheduleType:(NSString *)scheduleType;
- (void)deleteScheduleDataForScheduleId:(NSString *)scheduleId;
- (void)dataForRequest:(NSURLRequest *)request completionBlock:(ServerHelperCompletionBlock)completionBlock;
@end
