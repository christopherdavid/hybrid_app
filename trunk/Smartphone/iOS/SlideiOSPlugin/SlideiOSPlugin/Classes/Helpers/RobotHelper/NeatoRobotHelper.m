#import "NeatoRobotHelper.h"
#import "LogHelper.h"
#import "NeatoServerManager.h"
#import "NeatoUserHelper.h"
#import "NeatoDataStore.h"

@implementation NeatoRobotHelper

// TODO: should work on BG thread
+(void) saveNeatoRobot:(NeatoRobot *) neatoRobot
{
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    [helper saveNeatoRobot:neatoRobot forUser:[NeatoUserHelper getNeatoUser].userId];
}

// TODO: should work on BG thread
+(void) clearAllRobotData
{
    debugLog(@"");
    //TODO: delete robot details from DB
}

+(NeatoRobot *) getRobotForId:(NSString *) robotId
{
    debugLog(@"");
  return [[NeatoDataStore sharedNeatoDataStore] getRobotForId:robotId];
}

+ (void)updateUserAssociatedRobots
{
    debugLog(@"");
    NeatoServerManager *serverMan = [[NeatoServerManager alloc] init];
    [serverMan associatedRobotsForUserWithEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken]];
}
     
+ (void)updateName:(NSString *)name forRobotwithId:(NSString *)robotId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    NeatoRobot *robot = [helper getRobotForId:robotId];
    if (robot) {
        robot.name = name;
        [helper saveNeatoRobot:robot forUser:[NeatoUserHelper getNeatoUser].userId];
    }
}

@end
