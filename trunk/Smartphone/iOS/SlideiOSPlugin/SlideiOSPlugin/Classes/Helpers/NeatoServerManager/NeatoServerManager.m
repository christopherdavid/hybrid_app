#import "NeatoServerManager.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"
#import "CreateUserManager.h"
#import "NeatoNotification.h"
#import "AppHelper.h"
#import "ProfileDetail.h"
#import "LoginManager.h"
#import "NeatoRobotCommand.h"

// Helpers
#import "AppSettings.h"
#import "XMPPRobotDataChangeManager.h"
#import "TCPConnectionHelper.h"
#import "XMPPConnectionHelper.h"
#import "NeatoErrorCodes.h"
#import "PluginConstants.h"

@interface NeatoServerManager()
@property (nonatomic, retain) NeatoServerManager *retained_self;
@property (nonatomic, strong) CreateUserManager *createUserManager;
@property (nonatomic, retain) LoginManager *loginManager;
@property (nonatomic, retain) NSString *userEmail;
@end

@implementation NeatoServerManager

- (void)notifyRequestFailed:(SEL) selector withError:(NSError *) error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:selector withObject:error];
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)loginFacebookUser:(NSString *) externalSocialId {
    debugLog(@"");
    self.retained_self = self;
}

- (void)updateUserAuthToken:(NSString *)authToken {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper updateUserAuthToken:authToken];
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

- (void)linkEmail:(NSString *)email toLinkCode:(NSString *)linkCode completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_LINK_ROBOT_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:LINK_ROBOT_POST_STRING, API_KEY, email, linkCode] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
           completionBlock:^(id response, NSError *error) {
               if (error) {
                   // Failure
                   debugLog(@"Failed to link robot with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                   completion ? completion(nil, error) : nil;
                   return;
               }
             
               NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
               NSString *robotId = [responseResultDict objectForKey:NEATO_RESPONSE_SERIAL_NUMBER];
               
               // Get robot  details
               NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ROBOT_DETAILS_URL]];
               [request setHTTPMethod:@"POST"];
               [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING, API_KEY, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
             
                NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
               [serverHelper dataForRequest:request
                      completionBlock:^(id robotDetailsResponse, NSError *robotDetailsError) {
                          if (error) {
                              // Failure
                              debugLog(@"Failed to get robot details with error = %@, info = %@", [robotDetailsError localizedDescription], [robotDetailsError userInfo]);
                              completion ? completion(nil, robotDetailsError) : nil;
                              return;
                          }
                          NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotDetailsResponse];
                          [NeatoRobotHelper saveNeatoRobot:robot];
                          // Send response of the linking request.
                          completion ? completion(responseResultDict, nil) : nil;
                      }];
           }];
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
                   
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                   
                     // Getting profile details from response
                     NSDictionary *profileDetails = [responseResultDict objectForKey:NEATO_RESPONSE_PROFILE_DETAILS];
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
    self.loginManager = [[LoginManager alloc] init];
    self.loginManager.email = email;
    self.loginManager.password = password;
    [self.loginManager startWithCompletion:completion];
}

- (void)createUser3:(NeatoUser *)neatoUser completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    // There is no differencce in the data handling for createUser2 and createUser3, so using CreateUserListener2 with
    // override for new url. This saves code copy-paste.
    self.createUserManager = [[CreateUserManager alloc] init];
    self.createUserManager.user = neatoUser;
    [self.createUserManager startWithCompletion:completion];
}

- (void)notificationSettingsForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_PUSH_NOTIFICATION_OPTIONS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_PUSH_NOTIFICATION_OPTION_POST_STRING, API_KEY, email] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     completion ? completion(responseResultDict, error) : nil;
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
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     completion ? completion(responseResultDict, error) : nil;
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
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     completion ? completion(responseResultDict, error) : nil;
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
                   NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                   completion ? completion(responseResultDict, error) : nil;
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
                   NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                   completion ? completion(responseResultDict, error) : nil;
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
                   NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                   completion ? completion(responseResultDict, error) : nil;
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
                   NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                   completion ? completion(responseResultDict, error) : nil;
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
                   NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                   completion ? completion(responseResultDict, error) : nil;
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
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     completion ? completion(responseResultDict, error) : nil;
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
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     
                     // Delete current user
                     [NeatoUserHelper deleteUniqueDeviceIdForUser];
                     
                     // Disconect TCP and XMPP connection
                     TCPConnectionHelper *tcpHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
                     [tcpHelper disconnectFromRobot:@"" delegate:self];
                     XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
                     [xmppHelper disconnectFromRobot];

                     // Clear all 'user defaults' and DB data.
                     [NeatoUserHelper clearUserData];
                     
                     NSString *deviceToken = [NeatoUserHelper getDevicePushAuthToken];
                     if (deviceToken && deviceToken.length > 0) {
                         [weakSelf unregisterPushNotificationForDeviceToken:deviceToken
                                                                 completion:^(NSDictionary *result, NSError *error) {
                                                                     completion ? completion(responseResultDict, nil) : nil;
                                                                 }];
                     }
                     else {
                         completion ? completion(responseResultDict, nil) : nil;
                     }
                 }];
}

- (void)turnNotification:(NeatoNotification *)notification onOffForUserWithEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    // When first time user tries to update default values are set
    // and then database is updated with user value.
    if (![NeatoUserHelper notificationsExistForUserWithEmail:email]) {
        // No notification exists in database. Set a default one.
        [self setDefaultNotificationOptionsForEmail:email];
    }
    [NeatoUserHelper insertOrUpdateNotificaton:notification forEmail:email];
    // Get all notificationOptions from database and form JSON.
    NSArray *notificationsArray = [NeatoUserHelper notificationsForUserWithEmail:email];
    NSString *notificationJson = [AppHelper jsonStringFromNotificationsArray:notificationsArray];
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_SET_PUSH_NOTIFICATION_OPTIONS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:SET_USER_PUSH_NOTIFICATION_OPTION_POST_STRING, API_KEY, email, notificationJson] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     NSMutableDictionary *notificationJson = [[NSMutableDictionary alloc] init];
                     [notificationJson setValue:notification.notificationId forKey:KEY_NOTIFICATION_KEY];
                     [notificationJson setValue:[NSNumber numberWithBool:[AppHelper boolValueFromString:notification.notificationValue]] forKey:KEY_NOTIFICATION_VALUE];
                     completion ? completion(notificationJson, nil) : nil;
                 }];
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
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     completion ? completion(responseResultDict, error) : nil;
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
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     completion ? completion(responseResultDict, error) : nil;
                 }];
}

#pragma mark - Robot APIs
- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    if (!authToken || !email) {
        debugLog(@"AuthToken or email is NIL. Will not fetch associated robots.");
        NSError *inValidParamError = [AppHelper nserrorWithDescription:@"AuthToken or Email is NIL. Will not fetch associated robots." code:200];
        completion ? completion(nil, inValidParamError) : nil;
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
                         return;
                     }
                     
                     id responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     
                     NSMutableArray *neatoRobotsArr = [[NSMutableArray alloc] init];
                     for (NSDictionary *robotsDict in responseResultDict) {
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
                     
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     
                     // Getting profile details from response
                     NSDictionary *profileDetails = [responseResultDict objectForKey:NEATO_RESPONSE_PROFILE_DETAILS];
                     debugLog(@"RobotProfileDetails received from server : %@", profileDetails);
                     completion ? completion(profileDetails, nil) : nil;
                 }];
}

- (void)robotDetailForRobot:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion {
  debugLog(@"");
  
  NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ROBOT_DETAILS_URL]];
  [request setHTTPMethod:@"POST"];
  [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING,API_KEY, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
  
  NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
  [serverHelper dataForRequest:request
               completionBlock:^(id response, NSError *error) {
                 if (error) {
                   completion ? completion(nil, error) : nil;
                   return;
                 }
                   
                 // Getting robot details from response
                 NSDictionary *robotDetailsDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                 debugLog(@"Robot details received from server : %@", robotDetailsDict);
                 completion ? completion(robotDetailsDict, nil) : nil;
               }];
}

- (void)clearRobotAssociationWithRobotId:(NSString *)robotId email:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
  debugLog(@"");
  
  NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_CLEAR_ROBOT_DATA_URL]];
  [request setHTTPMethod:@"POST"];
  [request setHTTPBody:[[NSString stringWithFormat:CLEAR_ROBOT_DATA_POST_STRING, API_KEY, robotId, email, [NSNumber numberWithBool:NO]] dataUsingEncoding:NSUTF8StringEncoding]];
  
  NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
  [serverHelper dataForRequest:request
               completionBlock:^(id response, NSError *error) {
                 if (error) {
                   completion ? completion(nil, error) : nil;
                   return;
                 }
                 // Got result of server response.
                 NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                 completion ? completion(responseResultDict, nil) : nil;
               }];
}

- (void)onlineStatusForRobotWithId:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion {
  debugLog(@"");
  NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ROBOT_ONLINE_STATUS_URL]];
  [request setHTTPMethod:@"POST"];
  [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_ONLINE_STATUS_POST_STRING,API_KEY,robotId] dataUsingEncoding:NSUTF8StringEncoding]];
  
  NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
  [serverHelper dataForRequest:request
               completionBlock:^(id response, NSError *error) {
                 if (error) {
                   completion ? completion(nil, error) : nil;
                   return;
                 }
                 // Got result of server response.
                 NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                 completion ? completion(responseResultDict, nil) : nil;
               }];
}

- (void)setEnableStatus:(BOOL)enable withRobotId:(NSString *)robotId scheduleType:(NSInteger)scheduleType userEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    // Return error if it is advance schedule type.
    if (scheduleType == NEATO_SCHEDULE_ADVANCE_INT) {
        NSError *invalidScheduleTypeError = [AppHelper nserrorWithDescription:@"Invalid schedule type." code:UI_ERROR_INVALID_SCHEDULE_TYPE];
        completion ? completion(nil, invalidScheduleTypeError) : nil;
        return;
    }
    
    // Prepare command and request.
    NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
    robotCommand.robotId = robotId;
    robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
    [robotCommand.profileDict setValue:[AppHelper stringFromBool:enable] forKey:KEY_ENABLE_BASIC_SCHEDULE];
    NSMutableDictionary *httpHeaderFields = [[NSMutableDictionary alloc] init];

    NSURLRequest *request = [self requestForSetRobotProfileDetails3WithCommand:robotCommand
                                                                         email:email
                                                              httpHeaderFields:httpHeaderFields];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     // Got extra params from server response.
                     NSDictionary *extraParams = [response valueForKey:NEATO_RESPONSE_EXTRA_PARAMS];
                     
                     // Save timestamp returned from server in DB.
                     ProfileDetail *profileDetail = [[ProfileDetail alloc] init];
                     profileDetail.key = KEY_ENABLE_BASIC_SCHEDULE;
                     profileDetail.timestamp = [extraParams objectForKey:KEY_TIMESTAMP];
                     [NeatoRobotHelper updateProfileDetail:profileDetail forRobotWithId:robotId];
                     
                     // Send plugin data to UI.
                     NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                     [pluginDataDict setValue:robotId forKey:KEY_ROBOT_ID];
                     [pluginDataDict setValue:[NSNumber numberWithInteger:scheduleType] forKey:KEY_SCHEDULE_TYPE];
                     [pluginDataDict setValue:[NSNumber numberWithBool:enable] forKey:KEY_SCHEDULE_IS_ENABLED];
                     completion ? completion(pluginDataDict, nil) : nil;
                 }];
}

- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId completion:(RequestCompletionBlockDictionary)completion {
  debugLog(@"");
  if (!robotId || !robotName) {
    NSError *invalidParamError = [AppHelper nserrorWithDescription:@"Invalid Parameter error. RobotId or RobotName cannot be nil."
                                                              code:UI_INVALID_PARAMETER];
    completion ? completion(nil, invalidParamError) : nil;
    return;
  }
  
  // Prepare command and request.
  NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
  robotCommand.robotId = robotId;
  robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
  [robotCommand.profileDict setValue:robotName forKey:KEY_NAME];
  NSMutableDictionary *httpHeaderFields = [[NSMutableDictionary alloc] initWithCapacity:1];
  NSURLRequest *request = [self requestForSetRobotProfileDetails3WithCommand:robotCommand
                                                                       email:[NeatoUserHelper getLoggedInUserEmail]
                                                            httpHeaderFields:httpHeaderFields];
  
  NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
  [serverHelper dataForRequest:request
               completionBlock:^(id response, NSError *error) {
                 if (error) {
                   completion ? completion(nil, error) : nil;
                   return;
                 }
                 
                 // Got extra params from server response.
                 NSDictionary *extraParams = [response valueForKey:NEATO_RESPONSE_EXTRA_PARAMS];
                 
                 [NeatoRobotHelper updateName:robotName forRobotwithId:robotId];
                 // Update timestamp returned from server in DB.
                 ProfileDetail *profileDetail = [[ProfileDetail alloc] init];
                 profileDetail.key = KEY_NAME;
                 profileDetail.timestamp = [extraParams objectForKey:KEY_TIMESTAMP];
                 [NeatoRobotHelper updateProfileDetail:profileDetail forRobotWithId:robotId];
                 
                 // Send plugin data to UI.
                 NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                 [pluginDataDict setValue:robotId forKey:KEY_ROBOT_ID];
                 [pluginDataDict setValue:robotName forKey:KEY_ROBOT_NAME];
                 completion ? completion(pluginDataDict, nil) : nil;
               }];
}

- (void)associateRobot:(NSString *)robotId withEmail:(NSString *)email completion:(RequestCompletionBlockDictionary)completion {
    debugLog(@"");
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_SET_ROBOT_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:SET_ROBOT_USER_POST_STRING, API_KEY, email, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     
                     // Fetch all associated robots for a logged user.
                     NSString *authToken = [NeatoUserHelper getUsersAuthToken];
                     NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ASSOCIATED_ROBOTS_URL]];
                     [request setHTTPMethod:@"POST"];
                     [request setHTTPBody:[[NSString stringWithFormat:GET_ASSOCIATED_ROBOTS_POST_STRING,API_KEY, authToken, email] dataUsingEncoding:NSUTF8StringEncoding]];
                     
                     NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
                     [serverHelper dataForRequest:request
                                  completionBlock:^(id response, NSError *error) {
                                      if (error) {
                                          completion ? completion(nil, error) : nil;
                                          return;
                                      }
                                      
                                      NSArray *allAssociatedRobots = [response objectForKey:NEATO_RESPONSE_RESULT];
                                      // We have some robots at the server
                                      for (NSDictionary *robotDict in allAssociatedRobots) {
                                          // Update the DB with latest robot info.
                                          NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotDict];
                                          [NeatoRobotHelper saveNeatoRobot:robot];
                                      }
                                      
                                      // Get interested robot info and return to plugin.
                                      NeatoRobot *associatedRobot = [NeatoRobotHelper getRobotForId:robotId];
                                      NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                                      [pluginDataDict setValue:associatedRobot.serialNumber forKey:KEY_ROBOT_ID];
                                      [pluginDataDict setValue:associatedRobot.name forKey:KEY_NAME];
                                      completion ? completion(pluginDataDict, nil) : nil;
                                  }];
                 }];
}

#pragma mark - Private
- (NSString *)getProfileDataFromKey:(NSString *)key value:(NSString *)value {
    return [NSString stringWithFormat:PROFILE_DATA_FORMAT, key, value];
}

- (NSURLRequest *)requestForSetRobotProfileDetails3WithCommand:(NeatoRobotCommand *)command email:(NSString *)email httpHeaderFields:(NSDictionary *)httpHeaderFields {
    NSArray *keysArray = [command.profileDict allKeys];
    NSMutableString *profileKeys = [[NSMutableString alloc] init];
    for (NSString *key in keysArray) {
        NSString *profileKey = [self getProfileDataFromKey:key value:[command.profileDict valueForKey:key]];
        [profileKeys appendString:profileKey];
    }
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_SET_ROBOT_PROFILE_DETAILS_3]];
    [request setHTTPMethod:@"POST"];
    // TODO: Assuming Notification flag value is always true.
    [request setHTTPBody:[[NSString stringWithFormat:SET_ROBOT_PROFILE_DETAILS_3_POST_STRING, API_KEY, command.robotId, @"", email, [NeatoUserHelper uniqueDeviceIdForUser], @"", [NSNumber numberWithInt:NOTIFICATION_FLAG_TRUE], profileKeys] dataUsingEncoding:NSUTF8StringEncoding]];
    // Set Header fields.
    NSArray *httpHeaderFieldKeysArray = [httpHeaderFields allKeys];
    for (NSString *key in httpHeaderFieldKeysArray) {
        [request setValue:[httpHeaderFields valueForKey:key] forHTTPHeaderField:key];
    }
    return request;
}

- (void)setDefaultNotificationOptionsForEmail:(NSString *)email {
    NSMutableArray *notificationsArray = [[NSMutableArray alloc] init];
    for (int i = 0 ; i < TOTAL_NOTIFICATION_OPTIONS ; i++ ) {
        NeatoNotification *notification = [[NeatoNotification alloc] init];
        switch (i) {
            case 0:
                notification.notificationId = NOTIFICATION_ID_GLOBAL;
                break;
            case 1:
                notification.notificationId = NOTIFICATION_ID_ROBOT_STUCK;
                break;
            case 2:
                notification.notificationId = NOTIFICATION_ID_NEEDS_CLEAN;
                break;
            case 3:
                notification.notificationId = NOTIFICATON_ID_CLEANING_DONE;
                break;
            default:
                break;
        }
        notification.notificationValue = STRING_FALSE;
        [notificationsArray addObject:notification];
    }
    [NeatoUserHelper setNotificationsFromNotificationsArray:notificationsArray forEmail:email];
}

@end
