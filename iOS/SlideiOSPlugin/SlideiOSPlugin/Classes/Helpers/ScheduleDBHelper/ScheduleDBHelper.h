#import <Foundation/Foundation.h>

@class Schedule;

@interface ScheduleDBHelper : NSObject
+ (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId;
+ (id)getScheduleTypeForScheduleId:(NSString *)scheduleId;
+ (id)addBasicScheduleEventData:(NSString *)xmlData withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
+ (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withXMLData:(NSString *)xmlData;
+ (id)deleteBasicSchedleEventWithId:(NSString *)scheduleEventId;
+ (id)getBasicScheduleEventDataWithId:(NSString *)scheduleEventId;
+ (id)getBasicScheduleForScheduleId:(NSString *)scheduleId;
+ (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId;
+ (id)getRobotIdForScheduleId:(NSString *)scheduleId;
// TODO: Fix parameter names
+ (id)updateScheduleWithScheduleId:(NSString *)scheduleId withServerScheduleId:(NSString *)server_scheduleId andXmlDataVersion:(NSString *)xml_data_version;
+ (id)updateScheduleWithScheduleId:(NSString *)scheduleId forXmlDataVersion:(NSString *)xml_data_version;
@end
