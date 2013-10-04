#import <Foundation/Foundation.h>
#import "NeatoServerManager.h"
#import <Cordova/CDV.h>
#import "NeatoNotification.h"

@protocol UserManagerProtocol <NSObject>

@optional
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

- (void)pushNotificationRegistrationFailedWithError:(NSError *) error;
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken;
- (void)pushNotificationUnregistrationSuccess;
- (void)pushNotificationUnregistrationFailedWithError:(NSError *) error;
- (void)validatedUserWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId;
- (void)userValidationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToResendValidationEmailWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)resendValidationEmailSucceededWithMessage:(NSString *)message callbackId:(NSString *)callbackId;
- (void)forgetPasswordSuccessWithCallbackId:(NSString *)callbackId;
- (void)failedToForgetPasswordWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)changePasswordSuccessWithCallbackId:(NSString *)callbackId;
- (void)failedToChangePasswordWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)userCreated2:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId;
- (void)failedToCreateUser2WithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)notificationsTurnedOnOffWithResult:(NSDictionary *)notification callbackId:(NSString *)callbackId;
- (void)failedToSetUserPushNotificationOptionsWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)userNotificationSettingsData:(NSDictionary *)notificationJson callbackId:(NSString *)callbackId;
- (void)failedToGetUserPushNotificationSettingsWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)linkingToRobotoSucceededWithMessage:(NSString *)message callbackId:(NSString *)callbackId;
- (void)robotLinkingFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
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
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken;
- (void)isUserValidatedForEmail:(NSString *)email callbackID:(NSString *)callbackId;
- (void)resendValidationEmail:(NSString *) email callbackID:(NSString *) callbackId;
- (void)forgetPasswordForEmail:(NSString *)email callbackID:(NSString *)callbackId;
- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword callbackID:(NSString *)callbackId;
- (void)createUser2:(NeatoUser *)neatoUser callbackID:(NSString *)callbackId;
- (void)notificationSettingsForUserWithEmail:(NSString *)email callbackID:(NSString *)callbackId;
- (void)turnNotification:(NeatoNotification *)notification onOffForUserWithEmail:(NSString *)email callbackID:(NSString *)callbackId;
- (void)linkEmail:(NSString *)email toLinkCode:(NSString *)linkCode callbackID:(NSString *)callbackId;
- (void)createUser3:(NeatoUser *)neatoUser callbackID:(NSString *)callbackId;
@end
