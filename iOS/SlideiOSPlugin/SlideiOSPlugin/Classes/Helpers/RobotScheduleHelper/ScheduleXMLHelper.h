#import <Foundation/Foundation.h>

@class BasicScheduleEvent;
@class Schedule;

@interface ScheduleXMLHelper : NSObject
+ (NSString *)getXMlfromBasicScheduleEvent:(BasicScheduleEvent *)basicScheduleEvent;
+ (BasicScheduleEvent *)basicScheduleEventFromString:(NSString *)xmlData;
+ (Schedule *)basicScheduleFromString:(NSString *)xmlData;
+ (NSString *)getXmlDataFromSchedule:(Schedule *)schedule;
+ (NSDictionary *)advanceScheduleGroupFromXMLFile:(NSString *)filePath forScheduleId:(NSString *)scheduleId;
+ (NSString *)xmlFromScheduleGroup:(NSArray *)scheduleGroup;
@end
