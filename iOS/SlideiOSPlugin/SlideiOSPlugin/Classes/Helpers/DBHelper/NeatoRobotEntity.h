#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoUserEntity, ScheduleEntity;

@interface NeatoRobotEntity : NSManagedObject

@property (nonatomic, retain) NSString * chatId;
@property (nonatomic, retain) NSString * ipAddress;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * port;
@property (nonatomic, retain) NSString * robotId;
@property (nonatomic, retain) NSString * serialNumber;
@property (nonatomic, retain) NSString * userId;
@property (nonatomic, retain) NSSet *hasSchedule;
@property (nonatomic, retain) NSSet *hasUsers;
@end

@interface NeatoRobotEntity (CoreDataGeneratedAccessors)

- (void)addHasScheduleObject:(ScheduleEntity *)value;
- (void)removeHasScheduleObject:(ScheduleEntity *)value;
- (void)addHasSchedule:(NSSet *)values;
- (void)removeHasSchedule:(NSSet *)values;

- (void)addHasUsersObject:(NeatoUserEntity *)value;
- (void)removeHasUsersObject:(NeatoUserEntity *)value;
- (void)addHasUsers:(NSSet *)values;
- (void)removeHasUsers:(NSSet *)values;

@end
