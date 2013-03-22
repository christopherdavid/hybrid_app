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

- (void)updatedSchedule:(NSString *)message;
- (void)updatedScheduleError:(NSError *)error;

- (void)deletedScheduleData:(NSString *)message;
- (void)deleteScheduleDataError:(NSError *)error;

@end

@interface ScheduleServerHelper : NSObject

@property(nonatomic, weak) id delegate;

- (void)getSchedulesForRobotWithId:(NSString *)robotId;
- (void)getDataForScheduleWithId:(NSString *)scheduleId;

- (void)postScheduleForRobotId:(NSString *)robotId withScheduleData:(NSString *)xmlData ofScheduleType:(NSString *)scheduleType;
- (void)updateScheduleDataForScheduleId:(NSString *)scheduleId withXMLDataVersion:(NSString *)xml_data_version withScheduleData:(NSString *)xmlData ofScheduleType:(NSString *)scheduleType;
- (void)deleteScheduleDataForScheduleId:(NSString *)scheduleId;
@end
