#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NSURLConnectionHelper.h"

typedef void (^ServerHelperCompletionBlock)(id response, NSError *error);

@class NeatoRobotCommand;
@class NeatoUserAttributes;

@protocol NeatoServerHelperProtocol <NSObject>

@optional
//-(void) requestFailed:(NSError *) error;

- (void)gotUserDetails:(NeatoUser *)neatoUser;
- (void)gotRobotDetails:(NeatoRobot *)neatoRobot;
- (void)robotCreated;
- (void)robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId;
- (void)userLoggedOut;
- (void)gotUserAssociatedRobots:(NSMutableArray *) robots;
- (void)gotHandleForCreateUser:(NSString *) authToken;
- (void)robotNameUpdatedWithResult:(NSDictionary *)result;
- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId;
- (void)dissociatedAllRobots:(NSString *)message;
- (void)robotDissociatedWithMessage:(NSString *)message;
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken;
- (void)pushNotificationUnregistrationSuccess;
- (void)validatedUserWithResult:(NSDictionary *)result;
- (void)resendValidationEmailSucceededWithMessage:(NSString *)message;
- (void)forgetPasswordSuccess;
- (void)changePasswordSuccess;
- (void)failedToForgetPasswordWithError:(NSError *)error;
- (void)failedToChangePasswordWithError:(NSError *)error;
- (void)gotHandleForCreateUser2:(NSString *)authToken;
- (void)failedToGetCreateUserHandle2Error:(NSError *)error;
- (void)enabledDisabledScheduleSuccessWithResult:(NSDictionary *)result;
- (void)setUserPushNotificationOptionsSuccess;
- (void)userNotificationSettingsData:(NSDictionary *)notification;
- (void)gotScheduleStatus:(NSDictionary *)status;
- (void)virtualOnlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId;

- (void)commandSentWithResult:(NSDictionary *)result;
- (void)gotRobotProfileDetails2WithResult:(NSDictionary *)result;
- (void)setUserAttributesSucceeded;
- (void)notifyScheduleUpdatedSucceededWithResult:(NSDictionary *)result;
- (void)deleteProfileDetailKeySuccededforRobotId:(NSString *)robotId;
- (void)clearRobotDataSucceededWithMessage:(NSString *)message;

// Failure cases
- (void)failedToGetCreateUserHandle:(NSError *) error;
- (void)loginFailedWithError:(NSError *) error;
- (void)failedToGetLoginHandle:(NSError *) error;
- (void)gotUserHandleForLogin:(NSString *) userHandle;
- (void)failedToGetUserDetailsWithError:(NSError *) error;
- (void)failedToGetRobotDetailsWihError:(NSError *) error;
- (void)robotCreationFailedWithError:(NSError *) error;
- (void)robotAssociationFailedWithError:(NSError *) error;
- (void)logoutRequestFailedWithEror:(NSError *) error;
- (void)failedToGetAssociatedRobotsWithError:(NSError *) error;
- (void)failedToUpdateRobotNameWithError:(NSError *)error;
- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error;
- (void)failedToDissociateAllRobots:(NSError *)error;
- (void)failedToDissociateRobotWithError:(NSError *)error;
- (void)pushNotificationRegistrationFailedWithError:(NSError *)error;
- (void)pushNotificationUnregistrationFailedWithError:(NSError *)error;
- (void)userValidationFailedWithError:(NSError *)error;
- (void)failedToResendValidationEmailWithError:(NSError *)error;
- (void)failedToEnableDisableScheduleWithError:(NSError *)error;
- (void)failedToSetUserPushNotificationOptionsWithError:(NSError *)error;
- (void)failedToGetUserPushNotificationSettingsWithError:(NSError *)error;
- (void)failedToGetScheduleStatusWithError:(NSError *)error;
- (void)failedToGetRobotVirtualOnlineStatusWithError:(NSError *)error;
- (void)failedtoSendCommandWithError:(NSError *)error;
- (void)failedToGetRobotProfileDetails2WithError:(NSError *)error;
- (void)failedToSetUserAttributesWithError:(NSError *)error;
- (void)failedToNotifyScheduleUpdatedWithError:(NSError *)error;
- (void)failedToDeleteProfileDetailKeyWithError:(NSError *)error;
- (void)failedToClearRobotDataWithError:(NSError *)error;

@end

@interface NeatoServerHelper : NSObject

@property(nonatomic, weak) id delegate;

- (void)loginNativeUser:(NSString *)email password:(NSString *)password;
- (void)loginFacebookUser:(NSString *)externalSocialId;
- (void)getUserAccountDetails:(NSString *)authToken email:(NSString *)email;
- (void)createUser:(NeatoUser *)neatoUser;
- (void)createRobot:(NeatoRobot *)neatoRobot;
- (void)getRobotDetails:(NSString *)serialNumber;
- (void)setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number;
- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token;
- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken;
- (void)updateUserAuthToken:(NSString *)authToken;
- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId forUserWithEmail:(NSString *)email;
- (void)onlineStatusForRobotWithId:(NSString *)robotId;
- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email;
- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)email;
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken notificationServerType:(NSString *)serverType applicationId:(NSString *)applicationId;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken;
- (void)isUserValidatedForEmail:(NSString *)email;
- (void)resendValidationEmail:(NSString *)email;
- (void)forgetPasswordForEmail:(NSString *)email;
- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword authToken:(NSString *)authToken;
- (void)createUser2:(NeatoUser *)neatoUser;
- (void)enableDisable:(BOOL)enable scheduleType:(int)scheduleType forRobot:(NSString *)robotId withUserEmail:(NSString *)email;
- (void)setUserPushNotificationOptions:(NSString *)jsonString forUserWithEmail:(NSString *)email;
- (void)notificationSettingsForUserWithEmail:(NSString *)email;
- (void)isScheduleType:(NSString *)scheduleType enabledForRobotWithId:(NSString *)robotId;
- (void)virtualOnlineStatusForRobotWithId:(NSString *)robotId;
- (void)sendCommand:(NeatoRobotCommand *)command withSourceEmailId:(NSString *)email;
- (void)getProfileDetails2ForRobotWithId:(NSString *)robotId;
- (void)setUserAttributes:(NeatoUserAttributes *)attributes forAuthToken:(NSString *)authToken;
- (void)notifyScheduleUpdatedForProfileDetails:(NeatoRobotCommand *)profileDetails forUserWithEmail:(NSString *)email;
- (void)deleteProfileDetailKey:(NSString *)key forRobotWithId:(NSString *)robotId notfify:(NSInteger)notify;
- (void)clearDataForRobotId:(NSString *)robotId email:(NSString *)email;
- (void)createUser3:(NeatoUser *)neatoUser;
- (void)dataForRequest:(NSURLRequest *)request completionBlock:(ServerHelperCompletionBlock)completionBlock;
- (void)setUserAccountDetails:(NSString *)authToken user:(NeatoUser *)user;

@end
