#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NSURLConnectionHelper.h"
#import "NeatoServerHelper.h"
#import "NeatoNotification.h"

@class NeatoRobotCommand;

@protocol NeatoServerManagerProtocol <NSObject>
- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId;
- (void)pushNotificationRegistrationFailedWithError:(NSError *) error;
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken;
- (void)pushNotificationUnregistrationSuccess;
- (void)pushNotificationUnregistrationFailed:(NSError *) error;
- (void)commandSentWithResult:(NSDictionary *)result;
- (void)failedtoSendCommandWithError:(NSError *)error;
- (void)deleteProfileDetailKeySuccededforRobotId:(NSString *)robotId;
- (void)failedToDeleteProfileDetailKeyWithError:(NSError *)error;

@end

@interface NeatoServerManager : NSObject 
@property(nonatomic, weak) id delegate;

/*
 User
 */
- (void)notificationSettingsForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)setUserAccountDetails:(NeatoUser *)neatoUser authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion;
- (void)getUserDetailsForEmail:(NSString *)email authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion;
- (void)updateUserAuthToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion;
- (void)isUserValidatedForEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)forgetPasswordForEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)resendValidationEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword completion:(RequestCompletionBlockDictionary)completion;
- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId completion:(RequestCompletionBlockDictionary) completion;
- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token completion:(RequestCompletionBlockDictionary)completion;
- (void)loginNativeUser:(NSString *)email password:(NSString *)password completion:(RequestCompletionBlockDictionary)completion;
- (void)createUser3:(NeatoUser *)neatoUser completion:(RequestCompletionBlockDictionary)completion;
- (void)turnNotification:(NeatoNotification *)notification onOffForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion;
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken completion:(RequestCompletionBlockDictionary)completion;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken completion:(RequestCompletionBlockDictionary)completion;

/*
 Robot
 */
- (void)cleaningCategoryForRobot:(NSString *)serialNumber completion:(RequestCompletionBlockDictionary)completion;
- (void)profileDetailsForRobot:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion;

- (void)loginFacebookUser:(NSString *)externalSocialId;
- (void)updateUserAuthToken:(NSString *)authToken;
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)registrationId;
- (void)sendCommand:(NeatoRobotCommand *)command;
- (void)linkEmail:(NSString *)email toLinkCode:(NSString *)linkCode completion:(RequestCompletionBlockDictionary)completion;

// Block based APIs.
- (void)robotDetailForRobot:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion;
- (void)clearRobotAssociationWithRobotId:(NSString *)robotId email:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)onlineStatusForRobotWithId:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion;
- (void)setEnableStatus:(BOOL)enable withRobotId:(NSString *)robotId scheduleType:(NSInteger)scheduleType userEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion;
- (void)associateRobot:(NSString *)robotId withEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion;
@end
