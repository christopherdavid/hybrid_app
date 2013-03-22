#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class BasicScheduleEventEntity, ScheduleEntity;

@interface ScheduleEventsEntity : NSManagedObject

@property (nonatomic, retain) NSSet *hasBasicScheduleEvents;
@property (nonatomic, retain) ScheduleEntity *ofSchedule;
@end

@interface ScheduleEventsEntity (CoreDataGeneratedAccessors)

- (void)addHasBasicScheduleEventsObject:(BasicScheduleEventEntity *)value;
- (void)removeHasBasicScheduleEventsObject:(BasicScheduleEventEntity *)value;
- (void)addHasBasicScheduleEvents:(NSSet *)values;
- (void)removeHasBasicScheduleEvents:(NSSet *)values;

@end
