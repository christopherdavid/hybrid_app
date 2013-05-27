#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ScheduleEventsEntity;

@interface BasicScheduleEventEntity : NSManagedObject

@property (nonatomic, retain) NSString * scheduleEventId;
@property (nonatomic, retain) NSString * parameterStr;
@property (nonatomic, retain) ScheduleEventsEntity *ofScheduleEvent;

@end
