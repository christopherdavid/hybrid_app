#import <Foundation/Foundation.h>

@class Schedule;

@interface ScheduleDBHelper : NSObject
+ (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId;
+ (id)scheduleTypeForScheduleId:(NSString *)scheduleId;
+ (id)addBasicScheduleEventData:(NSString *)data withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
+ (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withData:(NSString *)xmlData;
+ (id)deleteBasicSchedleEventWithId:(NSString *)scheduleEventId;
+ (id)basicScheduleEventDataWithId:(NSString *)scheduleEventId;
+ (id)basicScheduleForScheduleId:(NSString *)scheduleId;
+ (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId;
+ (id)robotIdForScheduleId:(NSString *)scheduleId;
+ (id)updateServerScheduleId:(NSString *)serverScheduleId andScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId;
+ (id)updateScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId;
@end
