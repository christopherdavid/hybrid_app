#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NSURLConnectionHelper.h"
#import "NeatoServerHelper.h"

@protocol NeatoServerManagerProtocol <NSObject>

-(void) loginSuccess:(NeatoUser *) user;

- (void)failedToCreateUserWithError:(NSError *)error;
- (void)userCreated:(NeatoUser *)neatoUser;
- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId;
- (void)robotAssociation2FailedWithError:(NSError *)error;
- (void)userAssociateWithRobot:(NeatoRobot *)neatoRobot;
- (void)dissociatedAllRobots:(NSString *)message;
- (void)failedToDissociateAllRobots:(NSError *)error;
- (void)dissociateRobot:(NSString *)message;
- (void)failedToDissociateRobot:(NSError *)error;
- (void)gotUserAssociatedRobots:(NSMutableArray *) robots;
- (void)failedToGetAssociatedRobotsWithError:(NSError *) error;
- (void)pushNotificationRegistrationFailedWithError:(NSError *) error;
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken;
- (void)pushNotificationUnregistrationSuccess;
- (void)pushNotificationUnregistrationFailed:(NSError *) error;
- (void)validatedUserWithResult:(NSDictionary *)resultData;
- (void)userValidationFailedWithError:(NSError *)error;
- (void)resendValidationEmailSucceededWithMessage:(NSString *)message;
- (void)failedToResendValidationEmailWithError:(NSError *)error;
- (void)forgetPasswordSuccess;
- (void)failedToForgetPasswordWithError:(NSError *)error;
- (void)changePasswordSuccess;
- (void)failedToChangePasswordWithError:(NSError *)error;
- (void)userCreated2:(NeatoUser *)neatoUser;
- (void)failedToCreateUser2WithError:(NSError *)error;
- (void)failedToEnableDisableScheduleWithError:(NSError *)error;
- (void)enabledDisabledScheduleWithResult:(NSDictionary *)resultData;
@end

@interface NeatoServerManager : NSObject 

@property(nonatomic, weak) id delegate;

- (void)loginNativeUser:(NSString *)email password:(NSString *)password;
- (void)loginFacebookUser:(NSString *)externalSocialId;
- (void)getUserDetailsForEmail:(NSString *)email authToken:(NSString *)authToken;
- (void)createUser:(NeatoUser *)neatoUser;
- (void)createRobot:(NeatoRobot *)neatoRobot;
- (void)getRobotDetails:(NSString *)serialNumber;
- (void)setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number;
- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token;
- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken;
- (void)updateUserAuthToken:(NSString *)authToken;
- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId;
- (void)setRobotUserEmail2:(NSString *)userEmail forRobotId:(NSString *)robotId;
- (void)onlineStatusForRobotWithId:(NSString *)robotId;
- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email;
- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId;
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)registrationId;
- (void)isUserValidatedForEmail:(NSString *)email;
- (void)resendValidationEmail:(NSString *)email;
- (void)forgetPasswordForEmail:(NSString *)email;
- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword;
- (void)createUser2:(NeatoUser *)neatoUser;
- (void)enabledDisable:(BOOL)enable schedule:(int)scheduleType forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email;
@end
