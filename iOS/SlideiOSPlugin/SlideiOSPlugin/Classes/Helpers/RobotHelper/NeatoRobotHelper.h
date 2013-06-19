#import <Foundation/Foundation.h>
#import "NeatoRobot.h"

@interface NeatoRobotHelper : NSObject

+ (void)saveNeatoRobot:(NeatoRobot *) neatoRobot;
+ (void)clearAllRobotData;
+ (NeatoRobot *)getRobotForId:(NSString *) robotId;
+ (void)updateUserAssociatedRobots;
+ (void)updateName:(NSString *)name forRobotwithId:(NSString *)robotId;
+ (id)setSpotDefinitionForRobotWithId:(NSString *)robotId cleaningAreaLength:(int)cleaningAreaLength cleaningAreaHeight:(int)cleaningAreaHeight;
+ (id)spotDefinitionForRobotWithId:(NSString *)robotId;
+ (void)saveXMPPCallbackId:(NSString *)xmppCallbackId;
+ (NSString *)xmppCallbackId;
+ (void)removeXMPPCallbackId;
@end
