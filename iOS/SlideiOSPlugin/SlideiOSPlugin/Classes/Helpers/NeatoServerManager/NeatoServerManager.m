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

// Constants
#define GET_ROBOT_DETAILS_POST_STRING @"api_key=%@&serial_number=%@&key=%@"

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
-(void) loginFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(loginFailedWithError:) withError:error];
}

// Gets called from LoginListener
-(void) loginSuccess:(NeatoUser *)user {
    debugLog(@"");
    [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:selector withObject:error];
        self.delegate = nil;
        self.retained_self = nil;
    });
}

// Gets called from NeatoServerHelper
-(void) failedToGetLoginHandle:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(loginFailedWithError:) withError:error];
}

// Gets called from NeatoServerHelper
-(void) gotUserHandleForLogin:(NSString *) userHandle;
{
    debugLog(@"");
    // save auth token to local storage
    [NeatoUserHelper saveUserAuthToken:userHandle];
    // Get user details from server
    
    self.loginListener = [[LoginListener alloc] initWithDelegate:self];
    NeatoServerHelper *helper = [[NeatoServerHelper alloc]init];
    helper.delegate = self.loginListener;
    
    [helper getUserAccountDetails:userHandle email:nil];
    
}


-(void) loginFacebookUser:(NSString *) externalSocialId
{
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

-(void) failedToGetUserDetailsWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
}

-(void) gotUserDetails:(NeatoUser *)neatoUser
{
    debugLog(@"");
    [NeatoUserHelper saveNeatoUser:neatoUser];
    if ([self.delegate respondsToSelector:@selector(gotUserDetails:)])
    {
        [self.delegate performSelectorOnMainThread:@selector(gotUserDetails:) withObject:neatoUser waitUntilDone:NO];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

-(void) createUser:(NeatoUser *)neatoUser
{
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createUser:neatoUser];
}

-(void)userCreated:(NeatoUser *) user {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userCreated:)]) {
        [self.delegate performSelectorOnMainThread:@selector(userCreated:) withObject:user waitUntilDone:NO];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

-(void) failedToCreateUserWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUserWithError:) withError:error];
}

-(void) failedToGetCreateUserHandle:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUserWithError:) withError:error];
}

-(void) gotHandleForCreateUser:(NSString *) authToken
{
    debugLog(@"");
    // save users auth token to local storage
    [NeatoUserHelper saveUserAuthToken:authToken];
    
    self.createUserListener = [[CreateUserListener alloc] initWithDelegate:self];
    // Get the user details now
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.createUserListener;
    [helper getUserAccountDetails:authToken email:nil];
}

-(void) createRobot:(NeatoRobot *)neatoRobot
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createRobot:neatoRobot];
}

-(void) robotCreationFailedWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotCreationFailedWithError:) withError:error];
}

-(void) robotCreated
{
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

-(void) gotRobotDetails:(NeatoRobot *)neatoRobot
{
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

-(void) failedToGetRobotDetailsWihError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotDetailsWihError:) withError:error];
}

-(void) setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper setRobotUserEmail:email serialNumber:serial_number];
}

-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId
{
    debugLog(@"");
    // Fetch all robots for user and update the DB
    // Then notify the caller
    self.associationListener = [[RobotAssociationListener alloc] initWithDelegate:self];
    self.associationListener.associatedRobotId = robotId;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.associationListener;
    [helper associatedRobotsForUserWithEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken]];
}

-(void) robotAssociationCompletedSuccessfully:(NSString *) robotId
{
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

-(void) robotAssociationFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotAssociationFailedWithError:) withError:error];
}

-(void) logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper logoutUserEmail:email authToken:auth_token];
}


-(void) logoutRequestFailedWithEror:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(logoutRequestFailedWithEror:) withError:error];
}

-(void) userLoggedOut
{
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

-(void) failedToGetAssociatedRobotsWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:error];
}

-(void) gotUserAssociatedRobots:(NSMutableArray *)robots
{
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

-(void) updateUserAuthToken:(NSString *) authToken
{
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
    NSString *serverType = [AppHelper getNotificationServerType];
    NSString *appId = [AppHelper getApplicationId];
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
    self.createUserListener2 .user = neatoUser;
    [self.createUserListener2 start];
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
    CreateUserListener2 *createUserListener2 = [[CreateUserListener2 alloc] initWithDelegate:self];
    createUserListener2 .user = neatoUser;
    // There is no differencce in the data handling for createUser2 and createUser3, so using CreateUserListener2 with
    // override for new url. This saves code copy-paste.
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    serverHelper.delegate = createUserListener2;
    [serverHelper createUser3:neatoUser];
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
    [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING, API_KEY, serialNumber, KEY_ROBOT_CURRENT_STATE] dataUsingEncoding:NSUTF8StringEncoding]];
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request completionBlock:^(id response, NSError *error) {
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
            completion ? completion(nil, error) : nil;
            return;
        }
        NSString *robotStateParams = [[AppHelper parseJSON:jsonData] objectForKey:NEATO_RESPONSE_ROBOT_STATE_PARAMS];
        jsonData = [robotStateParams dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *cleaningCategory = [[AppHelper parseJSON:jsonData] objectForKey:NEATO_RESPONSE_CLEANING_CATEGORY];
        completion ? completion(cleaningCategory, error) : nil;
    }];
}

@end
