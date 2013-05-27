#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoRobotEntity, ScheduleEventsEntity;

@interface ScheduleEntity : NSManagedObject

@property (nonatomic, retain) NSString * scheduleId;
@property (nonatomic, retain) NSString * scheduleType;
@property (nonatomic, retain) NSString * server_scheduleId;
@property (nonatomic, retain) NSString * schedule_version;
@property (nonatomic, retain) ScheduleEventsEntity *hasScheduleEvent;
@property (nonatomic, retain) NeatoRobotEntity *ofRobot;

@end
