#import "NeatoServerManager.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "LoginListener.h"
#import "CreateUserListener.h"
#import "NeatoRobotHelper.h"
#import "RobotAssociationListener.h"
#import "SetRobotNameListener.h"
#import "RobotAssociationListener2.h"
#import "RobotDissociationListener.h"
#import "CreateUserListener2.h"
#import "ChangePasswordListener.h"
#import "EnableDisableScheduleListener.h"
#import "NeatoNotification.h"
#import "SetUserPushNotificationOptionsListener.h"
#import "GetUserPushNotificationsListener.h"
#import "AppHelper.h"
#import "ProfileDetail.h"
#import "LoginListener2.h"
#import "GetRobotDetailsHelper.h"
#import "LinkRobotHelper.h"

// Helpers
#import "AppSettings.h"
#import "XMPPRobotDataChangeManager.h"
#import "TCPConnectionHelper.h"
#import "XMPPConnectionHelper.h"

// Constants
#define PROFILE_DATA_FORMAT @"&profile[%@]=%@"

// ROBOT
#define GET_ROBOT_DETAILS_POST_STRING @"api_key=%@&serial_number=%@"

// USER
#define SET_ACCOUNT_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@%@"
#define GET_USER_PUSH_NOTIFICATION_OPTION_POST_STRING @"api_key=%@&email=%@"
#define GET_USER_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define UPDATE_AUTH_TOKEN_POST_STRING @"api_key=%@&auth_token=%@"
#define GET_IS_USER_VALIDATED_POST_STRING @"api_key=%@&email=%@"
#define GET_RESEND_VALIDATION_EMAIL_POST_STRING @"api_key=%@&email=%@"
#define GET_FORGET_PASSWORD_POST_STRING @"api_key=%@&email=%@"
#define GET_CHANGE_PASSWORD_POST_STRING @"api_key=%@&auth_token=%@&password_old=%@&password_new=%@"
#define DISSOCIATE_ALL_ROBOTS_POST_STRING @"api_key=%@&email=%@&serial_number=%@"
#define GET_USER_LOGOUT_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define GET_ASSOCIATED_ROBOTS_POST_STRING @"api_key=%@&auth_token=%@&email=%@"
#define PUSH_NOTIFICATION_REGISTRATION_POST_STRING  @"api_key=%@&user_email=%@&device_type=%ld&registration_id=%@"
#define PUSH_NOTIFICATION_UNREGISTRATION_POST_STRING  @"api_key=%@&registration_id=%@"
#define PUSH_NOTIFICATION_DEVICE_TOKEN  @"deviceTokenForPush"
#define PUSH_NOTIFICATION_SERVER_TYPE   @"notification_server_type"
#define PUSH_NOTIFICATION_APPLICATION_ID   @"application_id"

@interface NeatoServerManager()

@property (nonatomic, retain) NeatoServerManager *retained_self;
@property (nonatomic, retain) LoginListener *loginListener;
@property (nonatomic, retain) CreateUserListener *createUserListener;
@property (nonatomic, strong) CreateUserListener2 *createUserListener2;
@property (nonatomic, retain) RobotAssociationListener *associationListener;
@property (nonatomic, strong) ChangePasswordListener *changePasswordListener;
@property (nonatomic, strong) SetUserPushNotificationOptionsListener *setPushNotificationsListener;
@property (nonatomic, strong) GetUserPushNotificationsListener *getPushNotificationsListener;
@property (nonatomic, retain) LoginListener2 *loginListener2;
@property (nonatomic, retain) NSString *userEmail;
@property (nonatomic, strong) NeatoServerHelper *serverHelper;

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error;
@end


@implementation NeatoServerManager
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize loginListener = _loginListener;
@synthesize createUserListener = _createUserListener;
@synthesize associationListener = _associationListener;
@synthesize userEmail = _userEmail;
@synthesize createUserListener2 = _createUserListener2;
@synthesize changePasswordListener = _changePasswordListener;
@synthesize setPushNotificationsListener = _setPushNotificationsListener;
@synthesize getPushNotificationsListener = _getPushNotificationsListener;
@synthesize loginListener2 = _loginListener2;

- (void)loginNativeUser:(NSString *)email password:(NSString *)password {
    debugLog(@"");
    self.retained_self = self;
    self.loginListener2 = [[LoginListener2 alloc] initWithDelegate:self];
    self.loginListener2.email = email;
    self.loginListener2.password = password;
    [self.loginListener2 start];
}

// Gets called from LoginListener
- (void)loginFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(loginFailedWithError:) withError:error];
}

// Gets called from LoginListener
- (void)loginSuccess:(NeatoUser *)user {
    debugLog(@"");
    [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)notifyRequestFailed:(SEL) selector withError:(NSError *) error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:selector withObject:error];
        self.delegate = nil;
        self.retained_self = nil;
    });
}

// Gets called from NeatoServerHelper
- (void)failedToGetLoginHandle:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(loginFailedWithError:) withError:error];
}

// Gets called from NeatoServerHelper
- (void)gotUserHandleForLogin:(NSString *) userHandle; {
    debugLog(@"");
    // save auth token to local storage
    [NeatoUserHelper saveUserAuthToken:userHandle];
    // Get user details from server
    
    self.loginListener = [[LoginListener alloc] initWithDelegate:self];
    NeatoServerHelper *helper = [[NeatoServerHelper alloc]init];
    helper.delegate = self.loginListener;
    
    [helper getUserAccountDetails:userHandle email:nil];
    
}


- (void)loginFacebookUser:(NSString *) externalSocialId {
    debugLog(@"");
    self.retained_self = self;
}


- (void)getUserDetailsForEmail:(NSString *)email authToken:(NSString *)authToken
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper getUserAccountDetails:authToken email:email];
}

- (void)failedToGetUserDetailsWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
}

- (void)gotUserDetails:(NeatoUser *)neatoUser {
    debugLog(@"");
    [NeatoUserHelper saveNeatoUser:neatoUser];
    if ([self.delegate respondsToSelector:@selector(gotUserDetails:)])
    {
        [self.delegate performSelectorOnMainThread:@selector(gotUserDetails:) withObject:neatoUser waitUntilDone:NO];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

- (void)createUser:(NeatoUser *)neatoUser {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createUser:neatoUser];
}

- (void)userCreated:(NeatoUser *) user {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userCreated:)]) {
        [self.delegate performSelectorOnMainThread:@selector(userCreated:) withObject:user waitUntilDone:NO];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

- (void)failedToCreateUserWithError:(NSError *) error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUserWithError:) withError:error];
}

- (void)failedToGetCreateUserHandle:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUserWithError:) withError:error];
}

- (void)gotHandleForCreateUser:(NSString *) authToken {
    debugLog(@"");
    // save users auth token to local storage
    [NeatoUserHelper saveUserAuthToken:authToken];
    
    self.createUserListener = [[CreateUserListener alloc] initWithDelegate:self];
    // Get the user details now
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.createUserListener;
    [helper getUserAccountDetails:authToken email:nil];
}

- (void)createRobot:(NeatoRobot *)neatoRobot {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createRobot:neatoRobot];
}

- (void)robotCreationFailedWithError:(NSError *) error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotCreationFailedWithError:) withError:error];
}

- (void)robotCreated {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotCreated)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotCreated)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    
}

- (void)getRobotDetails:(NSString *)serialNumber {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper getRobotDetails:serialNumber];
}

- (void)gotRobotDetails:(NeatoRobot *)neatoRobot {
    debugLog(@"");
    // Save the details to local storage
    [NeatoRobotHelper saveNeatoRobot:neatoRobot];
    if ([self.delegate respondsToSelector:@selector(gotRobotDetails:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(gotRobotDetails:) withObject:neatoRobot];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToGetRobotDetailsWihError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotDetailsWihError:) withError:error];
}

- (void)setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper setRobotUserEmail:email serialNumber:serial_number];
}

- (void)robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId {
    debugLog(@"");
    // Fetch all robots for user and update the DB
    // Then notify the caller
    self.associationListener = [[RobotAssociationListener alloc] initWithDelegate:self];
    self.associationListener.associatedRobotId = robotId;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.associationListener;
    [helper associatedRobotsForUserWithEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken]];
}

- (void)robotAssociationCompletedSuccessfully:(NSString *) robotId {
    // Notify the caller
    if ([self.delegate respondsToSelector:@selector(robotAssociatedWithUser:robotId:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotAssociatedWithUser:robotId:) withObject:[NeatoUserHelper getLoggedInUserEmail] withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)robotAssociationFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotAssociationFailedWithError:) withError:error];
}

- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper logoutUserEmail:email authToken:auth_token];
}

- (void)logoutRequestFailedWithEror:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(logoutRequestFailedWithEror:) withError:error];
}

- (void)userLoggedOut {
    debugLog(@"");
    [NeatoUserHelper deleteUniqueDeviceIdForUser];
    if ([self.delegate respondsToSelector:@selector(userLoggedOut)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(userLoggedOut)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper associatedRobotsForUserWithEmail:email authToken:authToken];
}

- (void)failedToGetAssociatedRobotsWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:error];
}

- (void)gotUserAssociatedRobots:(NSMutableArray *)robots {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotUserAssociatedRobots:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(gotUserAssociatedRobots:) withObject:robots];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)updateUserAuthToken:(NSString *)authToken {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper updateUserAuthToken:authToken];
}

- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    SetRobotNameListener *setNameListener = [[SetRobotNameListener alloc] initWithDelegate:self];
    setNameListener.robotId = robotId;
    setNameListener.robotName = robotName;
    [setNameListener start];
}

- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotName:updatedForRobotWithId:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotName:updatedForRobotWithId:) withObject:name withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToUpdateRobotNameWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToUpdateRobotNameWithError:) withError:error];
}

- (void)setRobotUserEmail2:(NSString *)userEmail forRobotId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    
    RobotAssociationListener2 *associationListener = [[RobotAssociationListener2 alloc]initWithDelegate:self];
    associationListener.userEmail = userEmail;
    associationListener.robotId = robotId;
    [associationListener start];
}

- (void)robotAssociation2FailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotAssociation2FailedWithError:) withError:error];
}

- (void)userAssociateWithRobot:(NeatoRobot *)neatoRobot {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userAssociateWithRobot:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(userAssociateWithRobot:) withObject:neatoRobot];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)onlineStatusForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper onlineStatusForRobotWithId:robotId];
}

- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(onlineStatus:forRobotWithId:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(onlineStatus:forRobotWithId:) withObject:status withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotOnlineStatusWithError:) withError:error];
}

- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    self.userEmail = email;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper dissociateAllRobotsForUserWithEmail:email];
}

- (void)dissociatedAllRobots:(NSString *)message {
    debugLog(@"");
    //Update the database.
    [NeatoUserHelper dissociateAllRobotsForUserWithEmail:self.userEmail];
    if ([self.delegate respondsToSelector:@selector(dissociatedAllRobots:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(dissociatedAllRobots:) withObject:message];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToDissociateAllRobots:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToDissociateAllRobots:) withError:error];
}

- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId {
    self.retained_self = self;
    RobotDissociationListener *dissociationlistener = [[RobotDissociationListener alloc] initWithDelegate:self];
    dissociationlistener.robotId = robotId;
    dissociationlistener.userEmail = emailId;
    [dissociationlistener start];
}

- (void)robotDissociatedWithMessage:(NSString *)message {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotDissociatedWithMessage:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotDissociatedWithMessage:) withObject:message];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}
- (void)failedToDissociateRobotWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToDissociateRobotWithError:) withError:error];
    
}

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken {
    debugLog(@"registerPushNotification called");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    NSString *serverType = [AppHelper notificationServerType];
    NSString *appId = [AppHelper applicationId];
    [helper registerPushNotificationForEmail:email deviceType:deviceType deviceToken:deviceToken notificationServerType:serverType applicationId:appId];
}

- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken {
    debugLog(@"unregisterPushNotification called");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper unregisterPushNotificationForDeviceToken:deviceToken];
}


- (void)pushNotificationRegistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationRegistrationFailed called");
    [self notifyRequestFailed:@selector(pushNotificationRegistrationFailedWithError:) withError:error];
    self.delegate = nil;
    self.retained_self = nil;
}
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken {
    debugLog(@"pushNotificationRegisteredForDeviceToken called");
    if ([self.delegate respondsToSelector:@selector(pushNotificationRegisteredForDeviceToken:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(pushNotificationRegisteredForDeviceToken:) withObject:deviceToken];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}


- (void)pushNotificationUnregistrationFailedWithError:(NSError *)error {
    debugLog(@"pushNotificationUnregistrationFailedWithError called");
    [self notifyRequestFailed:@selector(pushNotificationUnregistrationFailedWithError:) withError:error];
    self.delegate = nil;
    self.retained_self = nil;
}
- (void)pushNotificationUnregistrationSuccess {
    debugLog(@"pushNotificationUnregistrationSuccess called");
    if ([self.delegate respondsToSelector:@selector(pushNotificationUnregistrationSuccess:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(pushNotificationUnregistrationSuccess:)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void)isUserValidatedForEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper isUserValidatedForEmail:email];
}

- (void)userValidationFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(userValidationFailedWithError:) withError:error];
}

- (void)validatedUserWithResult:(NSDictionary *)resultData {
    debugLog(@"");
    [self.delegate performSelectorOnMainThread:@selector(validatedUserWithResult:) withObject:resultData waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)resendValidationEmail:(NSString *)email  {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper resendValidationEmail:email];
}

- (void)resendValidationEmailSucceededWithMessage:(NSString *)message {
    debugLog(@"");
    [self.delegate performSelectorOnMainThread:@selector(resendValidationEmailSucceededWithMessage:) withObject:message waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)failedToResendValidationEmailWithError:(NSError *)error{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToResendValidationEmailWithError:) withError:error];
}

- (void)forgetPasswordForEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper forgetPasswordForEmail:email];
}

- (void)forgetPasswordSuccess {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(forgetPasswordSuccess)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(forgetPasswordSuccess)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToForgetPasswordWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToForgetPasswordWithError:) withError:error];
}

- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword {
    debugLog(@"");
    self.retained_self = self;
    self.changePasswordListener = [[ChangePasswordListener alloc] initWithDelegate:self];
    self.changePasswordListener.changedPassword = newPassword;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.changePasswordListener;
    [helper changePasswordFromOldPassword:oldPassword toNewPassword:newPassword authToken:[NeatoUserHelper getUsersAuthToken]];
}

- (void)changePasswordSuccess {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(changePasswordSuccess)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(changePasswordSuccess)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToChangePasswordWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToChangePasswordWithError:) withError:error];
}

- (void)createUser2:(NeatoUser *)neatoUser {
    debugLog(@"");
    self.retained_self = self;
    self.createUserListener2 = [[CreateUserListener2 alloc] initWithDelegate:self];
    self.createUserListener2.user = neatoUser;
    
    // There is no differencce in the data handling for createUser2 and createUser3, so using CreateUserListener2 with
    // override for new url. This saves code copy-paste.
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    serverHelper.delegate = self.createUserListener2;
    [serverHelper createUser2:neatoUser];
}

- (void)userCreated2:(NeatoUser *)user {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userCreated2:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(userCreated2:) withObject:user];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToCreateUser2WithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUser2WithError:) withError:error];
}

- (void)enabledDisable:(BOOL)enable schedule:(int)scheduleType forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    EnableDisableScheduleListener *enableDisableScheduleListener = [[EnableDisableScheduleListener alloc] initWithDelegate:self];
    enableDisableScheduleListener.robotId = robotId;
    enableDisableScheduleListener.email = email;
    enableDisableScheduleListener.enable = enable;
    enableDisableScheduleListener.scheduleType = scheduleType;
    [enableDisableScheduleListener start];
}

- (void)failedToEnableDisableScheduleWithError:(NSError *) error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToEnableDisableScheduleWithError:) withError:error];
}

- (void)enabledDisabledScheduleWithResult:(NSDictionary *)resultData {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(enabledDisabledScheduleWithResult:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(enabledDisabledScheduleWithResult:) withObject:resultData];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)turnNotification:(NeatoNotification *)notification onOffForUserWithEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    self.setPushNotificationsListener = [[SetUserPushNotificationOptionsListener alloc] initWithDelegate:self];
    self.setPushNotificationsListener.notification = notification;
    self.setPushNotificationsListener.email = email;
    [self.setPushNotificationsListener start];
}

- (void)notificationsTurnedOnOffWithResult:(NSDictionary *)notification {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(notificationsTurnedOnOffWithResult:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(notificationsTurnedOnOffWithResult:) withObject:notification];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    
}

- (void)failedToSetUserPushNotificationOptionsWithError:(NSError *)error {
    [self notifyRequestFailed:@selector(failedToSetUserPushNotificationOptionsWithError:) withError:error];
}

- (void)notificationSettingsForUserWithEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    self.getPushNotificationsListener = [[GetUserPushNotificationsListener alloc] initWithDelegate:self];
    self.getPushNotificationsListener.email = email;
    [self.getPushNotificationsListener start];
}

- (void)userNotificationSettingsData:(NSDictionary *)notificationJson {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userNotificationSettingsData:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(userNotificationSettingsData:) withObject:notificationJson];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToGetUserPushNotificationSettingsWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetUserPushNotificationSettingsWithError:) withError:error];
}

- (void)virtualOnlineStatusForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper virtualOnlineStatusForRobotWithId:robotId];
}

- (void)virtualOnlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(virtualOnlineStatus:forRobotWithId:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(virtualOnlineStatus:forRobotWithId:) withObject:status withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToGetRobotVirtualOnlineStatusWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotVirtualOnlineStatusWithError:) withError:error];
}

- (void)isScheduleType:(NSString *)scheduleType enabledForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper isScheduleType:scheduleType enabledForRobotWithId:robotId];
}

- (void)gotScheduleStatus:(NSDictionary *)status {
    debugLog(@"");
    [self.delegate performSelectorOnMainThread:@selector(gotScheduleStatus:) withObject:status waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}
- (void)failedToGetScheduleStatusWithError:(NSError *)error; {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetScheduleStatusWithError:) withError:error];
}

- (void)sendCommand:(NeatoRobotCommand *)command {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper sendCommand:command withSourceEmailId:[NeatoUserHelper getLoggedInUserEmail]];
}

- (void)failedtoSendCommandWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedtoSendCommandWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)command:(NeatoRobotCommand *)command sentWithResult:(NSDictionary *)result {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(command:sentWithResult:) withObject:command withObject:result];
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)profileDetails2ForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper getProfileDetails2ForRobotWithId:robotId];
}

- (void)gotRobotProfileDetails2WithResult:(NSDictionary *)result {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotRobotProfileDetails2WithResult:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(gotRobotProfileDetails2WithResult:) withObject:result];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToGetRobotProfileDetails2WithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotProfileDetails2WithError:) withError:error];
}

- (void)deleteProfileDetailKey:(NSString *)key forRobotWithId:(NSString *)robotId notfify:(BOOL)notify {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    serverHelper.delegate = self;
    NSInteger notifyInteger = notify ? NOTIFICATION_FLAG_TRUE:NOTIFICATION_FLAG_FALSE;
    [serverHelper deleteProfileDetailKey:key forRobotWithId:robotId notfify:notifyInteger];
}

- (void)deleteProfileDetailKeySuccededforRobotId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(deleteProfileDetailKeySuccededforRobotId:)]) {
        [self.delegate performSelector:@selector(deleteProfileDetailKeySuccededforRobotId:) withObject:robotId];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

- (void)failedToDeleteProfileDetailKeyWithError:(NSError *)error {
    [self notifyRequestFailed:@selector(failedToDeleteProfileDetailKeyWithError:) withError:error];
}

- (NeatoServerHelper *)serverHelper {
    if (!_serverHelper) {
        _serverHelper = [[NeatoServerHelper alloc] init];
    }
    return _serverHelper;
}


- (void)linkEmail:(NSString *)email toLinkCode:(NSString *)linkCode completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    LinkRobotHelper *linkHelper = [[LinkRobotHelper alloc] initWithEmail:email linkCode:linkCode];
    [self.serverHelper dataForRequest:[linkHelper request]
           completionBlock:^(id response, NSError *error) {
               if (error) {
                   // Failure
                   debugLog(@"Failed to link robot with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                   completion ? completion(nil, error) : nil;
                   return;
               }
               NSString *robotId = [response objectForKey:NEATO_RESPONSE_SERIAL_NUMBER];
               // Get robot  details
               GetRobotDetailsHelper *getRobotDetailsHelper = [[GetRobotDetailsHelper alloc] initWithRobotId:robotId];
               [self.serverHelper dataForRequest:[getRobotDetailsHelper request]
                      completionBlock:^(id robotDetailsResponse, NSError *robotDetailsError) {
                          if (error) {
                              // Failure
                              debugLog(@"Failed to get robot details with error = %@, info = %@", [robotDetailsError localizedDescription], [robotDetailsError userInfo]);
                              completion ? completion(nil, robotDetailsError) : nil;
                              return;
                          }
                          NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotDetailsResponse];
                          [NeatoRobotHelper saveNeatoRobot:robot];
                          completion ? completion(response, nil) : nil;
                      }];
           }];
}

- (void)clearDataForRobotId:(NSString *)robotId email:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper clearDataForRobotId:robotId email:email];
}

- (void)clearRobotDataSucceededWithMessage:(NSString *)message {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(clearRobotDataSucceededWithMessage:)]) {
        [self.delegate performSelector:@selector(clearRobotDataSucceededWithMessage:) withObject:message];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

- (void)failedToClearRobotDataWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToClearRobotDataWithError:)]) {
        [self.delegate performSelector:@selector(failedToClearRobotDataWithError:) withObject:error];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

- (void)createUser3:(NeatoUser *)neatoUser {
    debugLog(@"");
    self.retained_self = self;
    // There is no differencce in the data handling for createUser2 and createUser3, so using CreateUserListener2 with
    // override for new url. This saves code copy-paste.
    CreateUserListener2 *createUserListener2 = [[CreateUserListener2 alloc] initWithDelegate:self];
    createUserListener2.user = neatoUser;
    [createUserListener2 start];
}

- (void)setUserAccountDetails:(NeatoUser *)neatoUser authToken:(NSString *)authToken {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper setUserAccountDetails:authToken user:neatoUser];
}

- (void)cleaningCategoryForRobot:(NSString *)serialNumber completion:(RequestCompletionBlockDictionary)completion {
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ROBOT_PROFILE_DETAILS_2_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING, API_KEY, serialNumber] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     // Getting profile details from response
                     NSDictionary *profileDetails = [response objectForKey:NEATO_RESPONSE_PROFILE_DETAILS];
                     NSString *value = [[profileDetails objectForKey:NEATO_RESPONSE_CURRENT_STATE_DETAILS] objectForKey:KEY_VALUE];
                     // Get robot state params by parsing data from string for value key
                     // Get cleaning category by parsing data from string robot state params
                     NSData *jsonData = [value dataUsingEncoding:NSUTF8StringEncoding];
                     if (!jsonData) {
                         completion ? completion(nil, nil) : nil;
                         return;
                     }
                     NSDictionary *robotStateParams = [[AppHelper parseJSON:jsonData] objectForKey:NEATO_RESPONSE_ROBOT_STATE_PARAMS];
                     completion ? completion(robotStateParams, nil) : nil;
                 }];
}

- (void)loginNativeUser:(NSString *)email password:(NSString *)password completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    self.loginListener2 = [[LoginListener2 alloc] initWithDelegate:self];
    self.loginListener2.email = email;
    self.loginListener2.password = password;
    [self.loginListener2 startWithCompletion:completion];
}

- (void)createUser3:(NeatoUser *)neatoUser completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    // There is no differencce in the data handling for createUser2 and createUser3, so using CreateUserListener2 with
    // override for new url. This saves code copy-paste.
    CreateUserListener2 *createUserListener2 = [[CreateUserListener2 alloc] initWithDelegate:self];
    createUserListener2.user = neatoUser;
    [createUserListener2 startWithCompletion:completion];
}

- (void)notificationSettingsForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_PUSH_NOTIFICATION_OPTIONS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_PUSH_NOTIFICATION_OPTION_POST_STRING, API_KEY, email] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
    }];
}

- (void)forgetPasswordForEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_FORGET_PASSWORD_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_FORGET_PASSWORD_POST_STRING, API_KEY, email] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_CHANGE_PASSWORD_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_CHANGE_PASSWORD_POST_STRING, API_KEY, [NeatoUserHelper getUsersAuthToken], oldPassword, newPassword] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     // Update the database.
                     [NeatoUserHelper updatePassword:newPassword];
                     completion ? completion(response, error) : nil;
                 }];
}

-(void)isUserValidatedForEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_IS_USER_VALIDATED_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_IS_USER_VALIDATED_POST_STRING, API_KEY, email]
                          dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)resendValidationEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_RESEND_VALIDATION_EMAIL_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_RESEND_VALIDATION_EMAIL_POST_STRING, API_KEY, email] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)updateUserAuthToken:(NSString *) authToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_UPDATE_AUTH_TOKEN_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:UPDATE_AUTH_TOKEN_POST_STRING,API_KEY,authToken] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)getUserDetailsForEmail:(NSString *)email authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_USER_DETAILS_URL]];
    [request setHTTPMethod:@"POST"];
    email = email ? email : @"";
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_DETAILS_POST_STRING, API_KEY, email, authToken] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)setUserAccountDetails:(NeatoUser *)neatoUser authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_SET_ACCOUNT_DETAILS]];
    [request setHTTPMethod:@"POST"];
    NSString *profileKeys = [self getProfileDataFromKey:KEY_SERVER_COUNTRY_CODE value:neatoUser.userCountryCode];
    profileKeys = [profileKeys stringByAppendingString:[self getProfileDataFromKey:KEY_SERVER_OPT_IN value:[AppHelper stringFromBool:neatoUser.optIn]]];
    [request setHTTPBody:[[NSString stringWithFormat:SET_ACCOUNT_DETAILS_POST_STRING, API_KEY, neatoUser.email, authToken, profileKeys] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_DISSOCIATE_ALL_ROBOTS_URL]];
    [request setHTTPMethod:@"POST"];
    if (!robotId){
        robotId = @"";
    }
    [request setHTTPBody:[[NSString stringWithFormat:DISSOCIATE_ALL_ROBOTS_POST_STRING,API_KEY, emailId, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (!error && [robotId length] > 0) {
                         //Updating the DB before notifying the caller.
                         [NeatoUserHelper deleteRobotWithRobotId:robotId forUser:[NeatoUserHelper getNeatoUser].userId];
                     }
                     completion ? completion(response, error) : nil;
                 }];
}

- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    [self dissociateRobotWithId:nil
              fromUserWithEmail:email
                     completion:^(NSDictionary *result, NSError *error) {
                         if (!error) {
                             //Update the database.
                             [NeatoUserHelper dissociateAllRobotsForUserWithEmail:self.userEmail];
                         }
                         completion ? completion(result, error) : nil;
    }];
}

- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");

    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_LOGOUT_USER_URL]];
    [request setHTTPMethod:@"POST"];
    if (email == nil) {
        email = @"";
    }
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_LOGOUT_POST_STRING, API_KEY, email, auth_token] dataUsingEncoding:NSUTF8StringEncoding]];

    __weak typeof(self) weakSelf = self;
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     // Delete current user
                     [NeatoUserHelper deleteUniqueDeviceIdForUser];
                     NSString *deviceToken = [NeatoUserHelper getDevicePushAuthToken];
                     if (deviceToken && deviceToken.length > 0) {
                         [weakSelf unregisterPushNotificationForDeviceToken:deviceToken
                                                                 completion:^(NSDictionary *result, NSError *error) {
                                                                     // Disconect TCP and XMPP connection
                                                                     TCPConnectionHelper *tcpHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
                                                                     [tcpHelper disconnectFromRobot:@"" delegate:self];
                                                                     XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
                                                                     [xmppHelper disconnectFromRobot];
                                                                     // Clear robot data
                                                                     [NeatoUserHelper clearUserData];
                                                                     completion ? completion(response, nil) : nil;
                                                                 }];
                     }
                     else {
                       completion ? completion(nil, nil) : nil;
                     }
                 }];
}

- (void)turnNotification:(NeatoNotification *)notification onOffForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    self.setPushNotificationsListener = [[SetUserPushNotificationOptionsListener alloc] initWithDelegate:self];
    self.setPushNotificationsListener.notification = notification;
    self.setPushNotificationsListener.email = email;
    [self.setPushNotificationsListener startWithCompletion:completion];
}

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"registerPushNotification called");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    NSString *serverType = [AppHelper notificationServerType];
    NSString *appId = [AppHelper applicationId];
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_REGISTER_FOR_PUSH_NOTIFICATION_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:PUSH_NOTIFICATION_REGISTRATION_POST_STRING, API_KEY,
                           email, (long)deviceType, deviceToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:deviceToken forHTTPHeaderField:PUSH_NOTIFICATION_DEVICE_TOKEN];
    [request setValue:serverType forHTTPHeaderField:PUSH_NOTIFICATION_SERVER_TYPE];
    [request setValue:appId forHTTPHeaderField:PUSH_NOTIFICATION_APPLICATION_ID];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         [NeatoUserHelper saveDevicePushAuthToken:@""];
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     [NeatoUserHelper saveDevicePushAuthToken:deviceToken];
                     completion ? completion(response, nil) : nil;
                 }];
}

- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"unregisterPushNotification called");
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_UNREGISTER_FOR_PUSH_NOTIFICATION_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:PUSH_NOTIFICATION_UNREGISTRATION_POST_STRING, API_KEY,
                           deviceToken] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     [NeatoUserHelper saveDevicePushAuthToken:@""];
                     completion ? completion(response, error) : nil;
                 }];
}

#pragma mark - Robot APIs
- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    if (authToken == nil || email == nil) {
        debugLog(@"authToken or email is NIL. Will not fetch associated robots.");
        [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:[AppHelper nserrorWithDescription:@"authToken or Email is NIL. Will not fetch associated robots." code:200]];
        return;
    }
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ASSOCIATED_ROBOTS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ASSOCIATED_ROBOTS_POST_STRING,API_KEY, authToken, email] dataUsingEncoding:NSUTF8StringEncoding]];
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                     }
                     NSMutableArray *neatoRobotsArr = [[NSMutableArray alloc] init];
                     for (NSDictionary *robotsDict in response) {
                         NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotsDict];
                         [neatoRobotsArr addObject:robot];
                     }
                     NSDictionary *result = @{NEATO_RESPONSE_RESULT : neatoRobotsArr};
                     completion ? completion(result, nil) : nil;
    }];
}

- (void)profileDetailsForRobot:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ROBOT_PROFILE_DETAILS_2_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING ,API_KEY, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     // Getting profile details from response
                     NSDictionary *profileDetails = [response objectForKey:NEATO_RESPONSE_PROFILE_DETAILS];
                     debugLog(@"RobotProfileDetails received from server : %@", profileDetails);
                     completion ? completion(profileDetails, nil) : nil;
                 }];
}

#pragma mark - Private
- (NSString *)getProfileDataFromKey:(NSString *)key value:(NSString *)value {
    return [NSString stringWithFormat:PROFILE_DATA_FORMAT, key, value];
}

@end
