#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoRobotEntity;

@interface CleaningAreaEntity : NSManagedObject

@property (nonatomic, retain) NSNumber * height;
@property (nonatomic, retain) NSNumber * length;
@property (nonatomic, retain) NeatoRobotEntity *ofRobot;

@end
