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

+ (id)setSpotDefinitionForRobotWithId:(NSString *)robotId cleaningAreaLength:(NSInteger)cleaningAreaLength cleaningAreaHeight:(NSInteger)cleaningAreaHeight {
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

+ (id)updateProfileDetail:(ProfileDetail *)profileDetail forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper updateProfileDetail:profileDetail forRobotWithId:robotId];
}

+ (id)timestampForRobotProfileKey:(NSString *)key forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper timestampForRobotProfileKey:key forRobotWithId:robotId];
}

+ (id)profileDetailForKey:(NSString *)key robotWithId:(NSString *)robotId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper profileDetailForKey:key robotWithId:robotId ];
}


+ (id)deleteProfileDetail:(ProfileDetail *)profileDetail forRobot:(NSString *)robotId {
    debugLog(@"");
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper deleteProfileDetail:profileDetail forRobot:robotId];
}

+ (id)setDriveRequestForRobotWithId:(NSString *)robotId {
      NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper setDriveRequestForRobotWithId:robotId];
}

+ (id)driveRequestForRobotWithId:(NSString *)robotId {
     NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper driveRequestForRobotWithId:robotId];
}

+ (id)removeDriveRequestForRobotWihId:(NSString *)robotId {
    NeatoDataStore *helper = [NeatoDataStore sharedNeatoDataStore];
    return [helper removeDriveRequestForRobotWihId:robotId];
}

@end
