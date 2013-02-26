#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NeatoSocialNetworks.h"

@interface NeatoDBHelper : NSObject

+(NeatoDBHelper *) sharedNeatoDBHelper;

- (void)saveNeatoUser:(NeatoUser *)neatoUser;
- (NeatoUser *)getNeatoUser;
- (void)saveNeatoRobot:(NeatoRobot * )robot forUser:(NSString *)userId;
- (void)saveSocialNetwork:(NeatoSocialNetworks *)network forUser:(NSString *)userId;
- (NSMutableArray *)getAllRobotsForUser:(NSString *)userId;
- (NSMutableArray *)getAllSocialNetworksForUser:(NSString *)userId;
- (NeatoRobot *)getRobotForId:(NSString *)robotId;
- (void)deleteUserDetails;
- (void)deleteAllRobots;
- (void)deleteRobotWithSerialNumber:(NSString *)serialNumber forUser:(NSString *)userId;
@end
