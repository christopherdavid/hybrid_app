#import <Foundation/Foundation.h>
#import "NeatoRobot.h"

@interface NeatoRobotHelper : NSObject

+ (void)saveNeatoRobot:(NeatoRobot *) neatoRobot;
+ (void)clearAllRobotData;
+ (NeatoRobot *)getRobotForId:(NSString *) robotId;
+ (void)updateUserAssociatedRobots;
+ (void)updateName:(NSString *)name forRobotwithId:(NSString *)robotId;

@end
