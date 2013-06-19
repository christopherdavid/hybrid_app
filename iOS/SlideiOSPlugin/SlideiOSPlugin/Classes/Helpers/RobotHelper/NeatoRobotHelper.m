#import "NeatoRobotHelper.h"
#import "LogHelper.h"
#import "NeatoServerManager.h"
#import "NeatoUserHelper.h"
#import "NeatoDataStore.h"
#import "CleaningArea.h"

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

+ (id)setSpotDefinitionForRobotWithId:(NSString *)robotId cleaningAreaLength:(int)cleaningAreaLength cleaningAreaHeight:(int)cleaningAreaHeight {
    debugLog(@"");
    CleaningArea *cleaningArea = [[CleaningArea alloc] init];
    cleaningArea.height = cleaningAreaHeight;
    cleaningArea.length = cleaningAreaLength;
    cleaningArea.robotId = robotId;
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper setCleaningArea:cleaningArea];
}

+ (id)spotDefinitionForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper cleaningAreaForRobotWithId:robotId];
}

+ (void)saveXMPPCallbackId:(NSString *)xmppCallbackId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    [helper saveXMPPCallbackId:xmppCallbackId];
}

+ (NSString *)xmppCallbackId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper xmppCallbackId];
}

+ (void)removeXMPPCallbackId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper removeXMPPCallbackId];
}
@end
