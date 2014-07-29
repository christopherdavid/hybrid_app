#import <Foundation/Foundation.h>
#import "NeatoRobot.h"

@class ProfileDetail;

@interface NeatoRobotHelper : NSObject

+ (void)saveNeatoRobot:(NeatoRobot *) neatoRobot;
+ (void)clearAllRobotData;
+ (NeatoRobot *)getRobotForId:(NSString *) robotId;
+ (void)updateName:(NSString *)name forRobotwithId:(NSString *)robotId;
+ (id)setSpotDefinitionForRobotWithId:(NSString *)robotId cleaningAreaLength:(NSInteger)cleaningAreaLength cleaningAreaHeight:(NSInteger)cleaningAreaHeight;
+ (id)spotDefinitionForRobotWithId:(NSString *)robotId;
+ (void)saveXMPPCallbackId:(NSString *)xmppCallbackId;
+ (NSString *)xmppCallbackId;
+ (void)removeXMPPCallbackId;
+ (id)updateProfileDetail:(ProfileDetail *)profileDetail forRobotWithId:(NSString *)robotId;
+ (id)timestampForRobotProfileKey:(NSString *)key forRobotWithId:(NSString *)robotId;
+ (id)profileDetailForKey:(NSString *)key robotWithId:(NSString *)robotId;
+ (id)deleteProfileDetail:(ProfileDetail *)profileDetail forRobot:(NSString *)robotId;
+ (id)setDriveRequestForRobotWithId:(NSString *)robotId;
+ (id)driveRequestForRobotWithId:(NSString *)robotId;
+ (id)removeDriveRequestForRobotWihId:(NSString *)robotId;

@end
