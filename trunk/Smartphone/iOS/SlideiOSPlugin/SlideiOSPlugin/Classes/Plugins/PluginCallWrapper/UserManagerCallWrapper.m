#import "UserManagerCallWrapper.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotManager.h"
#import "NeatoRobotHelper.h"
#import "TCPHelper.h"
#import "XMPPConnectionHelper.h"

@interface UserManagerCallWrapper()

@property(nonatomic, retain) UserManagerCallWrapper *retained_self;
@property(nonatomic, retain) NSString *callbackId;

-(void) notifyCallback:(SEL) action;
-(void) notifyCallback:(SEL) action object:(id) object;
@end
@implementation UserManagerCallWrapper
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize callbackId = _callbackId;

- (BOOL)isUserLoggedIn {
    debugLog(@"");
    if ([NeatoUserHelper getNeatoUser])
    {
        // User is logged in, lets extend the auth key expiry
        // When the Auth key is extended we do not notify the caller, so we dont need
        // to retain 'self' here
        NeatoServerManager *manager = [[NeatoServerManager alloc] init];
        manager.delegate = self;
        [manager updateUserAuthToken:[NeatoUserHelper getUsersAuthToken]];
        return YES;
    }
    else
    {
        return NO;
    }
}

- (void)loginUserWithEmail:(NSString *) email password:(NSString *) password callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager loginNativeUser:email password:password];
}

- (void)loginSuccess:(NeatoUser *) user {
    debugLog(@"");
    [self notifyCallback:@selector(loginSuccess:callbackId:) object:user];
}

- (void)failedToCreateUserWithError:(NSError *) error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToCreateUserWithError:callbackId:) object:error];
}

- (void) userCreated:(NeatoUser *) neatoUser {
    debugLog(@"");
    [self notifyCallback:@selector(userCreated:callbackId:) object:neatoUser];
}

- (void)loginFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(loginFailedWithError:callbackId:) object:error];
}

- (void)loginFacebookUser:(NSString *) externalSocialId callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager loginFacebookUser:externalSocialId];
    
}

- (void)getUserDetailsForEmail:(NSString *)email authToken:(NSString *)authToken callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager getUserDetailsForEmail:email authToken:authToken];
}


- (void)createUser:(NeatoUser *)neatoUser callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager createUser:neatoUser];
    
}

- (void)userCreationFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(userCreationFailedWithError:callbackId:) object:error];
}


- (void)createRobot:(NeatoRobot *)neatoRobot callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager createRobot:neatoRobot];
}

- (void)setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager setRobotUserEmail:email serialNumber:serial_number];
}


- (void)logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;

    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager logoutUserEmail:email authToken:auth_token];
}

- (void)logoutRequestFailedWithEror:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(logoutRequestFailedWithEror:callbackId:) object:error];
}

- (void)notifyCallback:(SEL) action {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:action])
        {
            [self.delegate performSelector:action withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}
- (void)notifyCallback:(SEL) action object:(id) object {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:action])
        {
            [self.delegate performSelector:action withObject:object withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}


- (void)failedToGetUserDetailsWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToGetUserDetailsWithError:callbackId:) object:error];
}

/*-(void) requestFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyCallback:@selector(requestFailed:callbackId:) object:error];
}*/


-(void) gotUserDetails:(NeatoUser *)neatoUser {
    debugLog(@"");
    [self notifyCallback:@selector(gotUserDetails:callbackId:) object:neatoUser];
}

- (void)robotCreationFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(robotCreationFailedWithError:callbackId:) object:error];
}

- (void)robotCreated {
    debugLog(@"");
    [self notifyCallback:@selector(robotCreated:)];
}

- (void)robotAssociationFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(robotAssociationFailedWithError:callbackId:) object:error];
}

- (void)robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(robotAssociatedWithUser:robotId:callbackId:)])
        {
            [self.delegate robotAssociatedWithUser:message robotId:robotId callbackId:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
    //[self notifyCallback:@selector(robotAssociatedWithUser:robotId:callbackId:) object:messag];
}


- (void)userLoggedOut {
    debugLog(@"");
    NSString *deviceToken = [NeatoUserHelper getDevicePushAuthToken];
    if (deviceToken && deviceToken.length > 0) {
        UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
        [callWrapper unregisterPushNotificationForDeviceToken:deviceToken];
    }
    TCPConnectionHelper *tcpHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
    [tcpHelper disconnectFromRobot:@"" delegate:self];
    XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
    xmppHelper.delegate = self;
    [xmppHelper disconnectFromRobot];
    [NeatoUserHelper clearUserData];
    [self notifyCallback:@selector(userLoggedOut:)];
}

- (void)setRobotUserEmail2:(NSString *)email forRobotId:(NSString *)robotId callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager setRobotUserEmail2:email forRobotId:robotId];
}

- (void)robotAssociation2FailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(robotAssociation2FailedWithError:callbackId:) object:error];
}

- (void)userAssociateWithRobot:(NeatoRobot *)neatoRobot {
    debugLog(@"");
    [self notifyCallback:@selector(userAssociateWithRobot:callbackId:) object:neatoRobot];
}

- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager dissociateAllRobotsForUserWithEmail:email];
}

- (void)dissociatedAllRobots:(NSString *)message {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(dissociatedAllRobots:callbackId:)])
        {
            [self.delegate dissociatedAllRobots:message callbackId:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToDissociateAllRobots:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToDissociateAllRobots:callbackId:) object:error];
}

- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager dissociateRobotWithId:robotId fromUserWithEmail:emailId];
}

- (void)robotDissociatedWithMessage:(NSString *)message {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(robotDissociatedWithMessage:callbackId:)])
        {
            [self.delegate robotDissociatedWithMessage:message callbackId:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToDissociateRobotWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToDissociateRobotWithError:callbackId:) object:error];
}

- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)auth_token callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager associatedRobotsForUserWithEmail:email authToken:auth_token];
}

- (void)gotUserAssociatedRobots:(NSMutableArray *)robots {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(gotUserAssociatedRobots:callbackId:)])
        {
            [self.delegate gotUserAssociatedRobots:robots callbackId:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToGetAssociatedRobotsWithError:(NSError *)error {
    [self notifyCallback:@selector(failedToGetAssociatedRobotsWithError:callbackId:) object:error];
}

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken {
    debugLog(@"registerPushNotification called");
    self.retained_self = self;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager registerPushNotificationForEmail:email deviceType:deviceType deviceToken:deviceToken];
}

- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken {
    debugLog(@"unregisterPushNotificationForRegistrationId called");
    self.retained_self = self;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager unregisterPushNotificationForDeviceToken:deviceToken];
    
}

- (void)pushNotificationRegistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationRegistrationFailedWithError called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    [NeatoUserHelper saveDevicePushAuthToken:@""];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken {
    debugLog(@"pushNotificationRegisteredForDeviceToken called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    [NeatoUserHelper saveDevicePushAuthToken:deviceToken];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)pushNotificationUnregistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationUnregistrationFailedWithError called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)pushNotificationUnregistrationSuccess {
    debugLog(@"pushNotificationUnregistrationSuccess called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    [NeatoUserHelper saveDevicePushAuthToken:@""];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)isUserValidatedForEmail:(NSString *)email callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager isUserValidatedForEmail:email];
}

- (void)validatedUserWithResult:(NSDictionary *)resultData {
    debugLog(@"");
    [self notifyCallback:@selector(validatedUserWithResult:callbackId:) object:resultData];
}

- (void)userValidationFailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(userValidationFailedWithError:callbackId:) object:error];
}

- (void)resendValidationEmail:(NSString *) email callbackID:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager resendValidationEmail:email];
}

- (void)resendValidationEmailSucceededWithMessage:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(resendValidationEmailSucceededWithMessage:callbackId:)]) {
            [self.delegate resendValidationEmailSucceededWithMessage:message callbackId:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToResendValidationEmailWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToResendValidationEmailWithError:callbackId:) object:error];
}

- (void)forgetPasswordForEmail:(NSString *)email callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager forgetPasswordForEmail:email];
}

- (void)forgetPasswordSuccess {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(forgetPasswordSuccessWithCallbackId:)]) {
            [self.delegate performSelector:@selector(forgetPasswordSuccessWithCallbackId:) withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToForgetPasswordWithError:(NSError *)error {
    [self notifyCallback:@selector(failedToForgetPasswordWithError:callbackId:) object:error];
}

- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager changePasswordFromOldPassword:oldPassword toNewPassword:newPassword];
}

- (void)changePasswordSuccess {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(changePasswordSuccessWithCallbackId:)]) {
            [self.delegate performSelector:@selector(changePasswordSuccessWithCallbackId:) withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToChangePasswordWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToChangePasswordWithError:callbackId:) object:error];
}

- (void)createUser2:(NeatoUser *)neatoUser callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager createUser2:neatoUser];
}

- (void)userCreated2:(NeatoUser *)neatoUser {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(userCreated2:callbackId:)]) {
            [self.delegate performSelector:@selector(userCreated2:callbackId:) withObject:neatoUser withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToCreateUser2WithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToCreateUser2WithError:callbackId:) object:error];
}

- (void)turnNotification:(NeatoNotification *)notification onOffForUserWithEmail:(NSString *)email callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager turnNotification:notification onOffForUserWithEmail:email];
}

- (void)notificationsTurnedOnOffWithResult:(NSDictionary *)notificationJson {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(notificationsTurnedOnOffWithResult:callbackId:)]) {
            [self.delegate performSelector:@selector(notificationsTurnedOnOffWithResult:callbackId:) withObject:notificationJson withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToSetUserPushNotificationOptionsWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToSetUserPushNotificationOptionsWithError:callbackId:) object:error];
}

- (void)notificationSettingsForUserWithEmail:(NSString *)email callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager notificationSettingsForUserWithEmail:email];
}

- (void)userNotificationSettingsData:(NSDictionary *)notificationJson {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(userNotificationSettingsData:callbackId:)]) {
            [self.delegate performSelector:@selector(userNotificationSettingsData:callbackId:) withObject:notificationJson withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToGetUserPushNotificationSettingsWithError:(NSError *)error {
    debugLog(@"");
    [self notifyCallback:@selector(failedToGetUserPushNotificationSettingsWithError:callbackId:) object:error];
}


- (void)createUser3:(NeatoUser *)neatoUser callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager createUser3:neatoUser];
}

- (void)setUserAccountDetails:(NeatoUser *)neatoUser authToken:(NSString *)auth_token callbackID:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager setUserAccountDetails:neatoUser authToken:auth_token];
}


@end



