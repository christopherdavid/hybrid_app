#import <Foundation/Foundation.h>
#import "NeatoServerManager.h"
#import <Cordova/CDV.h>

@protocol UserManagerProtocol <NSObject>

//-(void) requestFailed:(NSError *) error callbackId:(NSString *)callbackId;
- (void)gotUserDetails:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId;
- (void)robotCreated:(NSString *)callbackId;
- (void)robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId callbackId:(NSString *)callbackId;
- (void)userLoggedOut:(NSString *)callbackId;
- (void)loginFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)logoutRequestFailedWithEror:(NSError *)error callbackId:(NSString *)callbackId;
- (void)userCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToGetUserDetailsWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)robotAssociationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)robotCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToCreateUserWithError:(NSError *) error callbackId:(NSString *)callbackId;
- (void)loginSuccess:(NeatoUser *) user  callbackId:(NSString *)callbackId;
- (void)userCreated:(NeatoUser *) neatoUser  callbackId:(NSString *)callbackId;
- (void)robotAssociation2FailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)userAssociateWithRobot:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId;
- (void)dissociatedAllRobots:(NSString *)message callbackId:(NSString *)callbackId;
- (void)failedToDissociateAllRobots:(NSError *)error callbackId:(NSString *)callbackId;
- (void)robotDissociatedWithMessage:(NSString *)message callbackId:(NSString *)callbackId;
- (void)gotUserAssociatedRobots:(NSMutableArray *)robots callbackId:(NSString *)callbackId;
- (void)failedToGetAssociatedRobotsWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToDissociateRobotWithError:(NSError *)error callbackId:(NSString *)callbackId;
@end

@interface UserManagerCallWrapper : CDVPlugin 

@property(nonatomic, weak) id<UserManagerProtocol> delegate;

- (void)loginUserWithEmail:(NSString *)email password:(NSString *)password callbackID:(NSString *)callbackId;
- (void)loginFacebookUser:(NSString *) externalSocialId callbackID:(NSString *)callbackId;
- (void)getUserDetailsForEmail:(NSString *)email  authToken:(NSString *)authToken callbackID:(NSString *)callbackId;
- (void)createUser:(NeatoUser *)neatoUser callbackID:(NSString *)callbackId;
- (void)createRobot:(NeatoRobot *)neatoRobot callbackID:(NSString *)callbackId;
- (void)setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number callbackID:(NSString *)callbackId;
- (void)setRobotUserEmail2:(NSString *)email forRobotId:(NSString *)robotId callbackID:(NSString *)callbackId;
- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token callbackID:(NSString *) callbackId;
- (BOOL)isUserLoggedIn;
- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email callbackID:(NSString *)callbackId;
- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId callbackId:(NSString *)callbackId;
- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)auth_token callbackId:(NSString *)callbackId;

@end
