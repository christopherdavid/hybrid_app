#import "NeatoRobotHelper.h"
#import "LogHelper.h"
#import "NeatoDBHelper.h"
#import "NeatoServerManager.h"
#import "NeatoUserHelper.h"

@implementation NeatoRobotHelper

// TODO: should work on BG thread
+(void) saveNeatoRobot:(NeatoRobot *) neatoRobot
{
    debugLog(@"");
    NeatoDBHelper *helper = [NeatoDBHelper sharedNeatoDBHelper];
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
    return [[NeatoDBHelper sharedNeatoDBHelper] getRobotForId:robotId];
}

+(void) updateUserAssociatedRobots
{
    debugLog(@"");
    NeatoServerManager *serverMan = [[NeatoServerManager alloc] init];
    [serverMan getAssociatedRobots:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken]];
}
     


@end
