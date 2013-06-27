#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoRobotEntity;

@interface ProfileDetailEntity : NSManagedObject

@property (nonatomic, retain) NSString * key;
@property (nonatomic, retain) NSNumber * timestamp;
@property (nonatomic, retain) NeatoRobotEntity *ofRobot;

@end
