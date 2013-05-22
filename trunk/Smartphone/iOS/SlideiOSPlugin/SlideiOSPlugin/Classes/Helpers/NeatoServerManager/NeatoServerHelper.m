#import "NeatoServerHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "NeatoConstants.h"


#define SERVER_REPONSE_HANDLER_KEY @"key_server_response_handler"
#define RESEND_VALIDATION_EMAIL_HANDLER @"resendValidationEmailHandler:"
#define LOGIN_NATIVE_USER_RESPONSE_HANDLER @"loginNativeUserHandler:"
#define GET_USER_DETAILS_RESPONSE_HANDLER @"getUserDetailsHandler:" 
#define GET_CREATE_USER_RESPONSE_HANDLER @"createUserHandler:"
#define GET_CREATE_ROBOT_RESPONSE_HANDLER @"createRobothandler:"
#define GET_ROBOT_DETAILS_RESPONSE_HANDLER @"getRobotDetailsHandler:"
#define SET_ROBOT_USER_RESPONSE_HANDLER @"setRobotUserResponseHandler:"
#define GET_USER_LOGOUT_RESPONSE_HANDLER @"getUserLogoutResponseHandler:"
#define UPDATE_AUTH_TOKEN_RESPONSE_HANDLER @"updateAuthTokenHandler:"
#define GET_ASSOCIATED_ROBOTS_RESPONSE_HANDLER @"getAssociatedRobotsHandler:"
#define SET_ROBOT_NAME_RESPONSE_HANDLER @"setRobotNameHandler:"
#define DISSOCIATE_ALL_ROBOTS_RESPONSE_HANDLER @"dissociateAllRobotsHandler:"
#define DISSOCIATE_ROBOT_RESPONSE_HANDLER @"dissociateRobotHandler:"
#define GET_ROBOT_ONLINE_STATUS_RESPONSE_HANDLER @"getRobotOnlineStatusHandler:"
#define PUSH_NOTIFICATION_REGISTRATION_REPOSNE_HANDLER @"pushNotificationRegistrationHandler:connection:"
#define PUSH_NOTIFICATION_UNREGISTRATION_REPOSNE_HANDLER @"pushNotificationUnregistrationHandler:"
#define PUSH_NOTIFICATION_DEVICE_TOKEN  @"deviceTokenForPush"
#define IS_USER_VALIDATED_HANDLER @"isUserValidatedHandler:"
#define FORGET_PASSWORD_HANDLER @"forgetPasswordHandler:"
#define CHANGE_PASSWORD_HANDLER @"changePasswordHandler:"
#define CREATE_USER2_RESPONSE_HANDLER @"createUserHandler2:"
#define ENABLE_DISABLE_SCHEDULE_RESPONSE_HANDLER @"enableDisableScheduleHandler:"

#define GET_IS_USER_VALIDATED_POST_STRING @"api_key=%@&email=%@"
#define GET_AUTH_TOKEN_NATIVE_POST_STRING @"api_key=%@&account_type=%@&email=%@&password=%@"
#define GET_RESEND_VALIDATION_EMAIL_POST_STRING @"api_key=%@&email=%@"
#define GET_USER_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define GET_CREATE_USER_POST_STRING @"api_key=%@&name=%@&email=%@&password=%@&account_type=%@&external_social_id=%@"
#define GET_CREATE_ROBOT_POST_STRING @"api_key=%@&serial_number=%@&name=%@"
#define GET_ROBOT_DETAILS_POST_STRING @"api_key=%@&serial_number=%@"
#define SET_ROBOT_USER_POST_STRING @"api_key=%@&email=%@&serial_number=%@"
#define GET_USER_LOGOUT_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define UPDATE_AUTH_TOKEN_POST_STRING @"api_key=%@&auth_token=%@"
#define GET_ASSOCIATED_ROBOTS_POST_STRING @"api_key=%@&auth_token=%@&email=%@"
#define SET_ROBOT_PROFILE_POST_STRING @"api_key=%@&serial_number=%@&%@"
#define ROBOT_PROFILE_DATA_FORMAT @"profile[%@]=%@"
#define DISSOCIATE_ALL_ROBOTS_POST_STRING @"api_key=%@&email=%@&serial_number=%@"
#define GET_ROBOT_ONLINE_STATUS_POST_STRING @"api_key=%@&serial_number=%@"

#define PUSH_NOTIFICATION_REGISTRATION_POST_STRING  @"api_key=%@&user_email=%@&device_type=%ld&registration_id=%@"
#define PUSH_NOTIFICATION_UNREGISTRATION_POST_STRING  @"api_key=%@&registration_id=%@"
#define CREATE_USER2_POST_STRING @"api_key=%@&name=%@&email=%@&alternate_email=%@&password=%@&account_type=%@&external_social_id=%@"
#define GET_FORGET_PASSWORD_POST_STRING @"api_key=%@&email=%@"
#define GET_CHANGE_PASSWORD_POST_STRING @"api_key=%@&auth_token=%@&password_old=%@&password_new=%@"
#define ENABLE_BASIC_SCHEDULE @"enable_basic_schedule"
#define ENABLE_ADVANCED_SCHEDULE @"enable_advanced_schedule"
#define ENABLE_DISABLE_SCHEDULE_POST_STRING @"api_key=%@&serial_number=%@&source_serial_number=%@&source_smartapp_id=%@&value_extra=%@&%@"

@interface NeatoServerHelper()

@property(nonatomic, retain) NeatoServerHelper *retained_self;
@property(nonatomic, retain) NSString *robotId;

- (void)notifyRequestFailed:(SEL)selector withError:(NSError *)error;
- (NSString *)getRobotProfileDataFromKey:(NSString *)key value:(NSString *)value;
- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)email handler:(NSString *)handler;
- (NSString *)paramsForEnabledSchedule:(BOOL)enable ofType:(int)scheduleType;
@end


@implementation NeatoServerHelper
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize robotId = _robotId;




-(void) logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token
{
    debugLog(@"logout user called");
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_LOGOUT_USER_URL]];
    [request setHTTPMethod:@"POST"];
    if (email == nil)
    {
        email = @"";
    }
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_LOGOUT_POST_STRING, NEATO_API_KEY, email, auth_token] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_USER_LOGOUT_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection responseData:(NSData *) responseData
{
    debugLog(@"");
    NSString *selectorStr = [[connection originalRequest] valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSInteger parameterCount = [[selectorStr mutableCopy] replaceOccurrencesOfString:@":" withString:@"," options:NSLiteralSearch range:NSMakeRange(0, [selectorStr length])];
    SEL selector = NSSelectorFromString(selectorStr);
    if (parameterCount == 1) {
        [self performSelector:selector withObject:responseData];
    }
    else if (parameterCount == 2) {
        [self performSelector:selector withObject:responseData withObject:connection];
    }
    else {
        debugLog(@"connectionDidFinishLoading called with invalid number of arguments. Selector = %@, Parameters required = %d", selectorStr, parameterCount);
    }
}

// This gets called when the connection fails for any reason.
-(void) requestFailedForConnection:(NSURLConnection *)connection error:(NSError *) error
{
    debugLog(@"");
    NSString *selectorStr = [[connection originalRequest] valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    NSInteger parameterCount = [[selectorStr mutableCopy] replaceOccurrencesOfString:@":" withString:@"," options:NSLiteralSearch range:NSMakeRange(0, [selectorStr length])];
    if (parameterCount == 1) {
        [self performSelector:selector withObject:error];
    }
    else if (parameterCount == 2) {
        [self performSelector:selector withObject:error withObject:connection];
    }
    else {
        debugLog(@"requestFailedForConnection called with invalid number of arguments. Selector = %@, Parameters required = %d", selectorStr, parameterCount);
    }
}

-(void) getUserLogoutResponseHandler:(id)value
{
    debugLog(@"");
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Logout request failed!");
        [self notifyRequestFailed:@selector(logoutRequestFailedWithEror:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Logout request failed!");
        [self notifyRequestFailed:@selector(logoutRequestFailedWithEror:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
        debugLog(@"%@",message);
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(userLoggedOut)])
            {
                [self.delegate performSelector:@selector(userLoggedOut)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(logoutRequestFailedWithEror:) withError:error];
    }
}

-(void) setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number
{
    debugLog(@"");
    self.retained_self = self;
    self.robotId = serial_number;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_SET_ROBOT_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:SET_ROBOT_USER_POST_STRING, NEATO_API_KEY, email, serial_number] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:SET_ROBOT_USER_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void)setRobotUserResponseHandler:(id)value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Robot association failed!");
        [self notifyRequestFailed:@selector(robotAssociationFailedWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Robot association failed!");
        [self notifyRequestFailed:@selector(robotAssociationFailedWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(robotAssociatedWithUser:robotId:)])
            {
                [self.delegate performSelector:@selector(robotAssociatedWithUser:robotId:) withObject:message withObject:self.robotId];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
        
        
    }
    else
    {
        debugLog(@"set robot user  unsuccessful");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(robotAssociationFailedWithError:) withError:error];
    }
    
    
}

-(void) createRobot:(NeatoRobot *)neatoRobot
{
    debugLog(@"create robot called");
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_CREATE_ROBOT_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_CREATE_ROBOT_POST_STRING, NEATO_API_KEY, neatoRobot.serialNumber,neatoRobot.name] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_CREATE_ROBOT_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
    
}

-(void) createRobothandler:(id)value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Robot creation failed!");
        [self notifyRequestFailed:@selector(robotCreationFailedWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Robot creation failed!");
        [self notifyRequestFailed:@selector(robotCreationFailedWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        debugLog(@"robot creation successful");
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
        debugLog(@"%@",message);
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(robotCreated)])
            {
                [self.delegate performSelector:@selector(robotCreated)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        debugLog(@"robot creation unsuccessful");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(robotCreationFailedWithError:) withError:error];
    }
    
    
}

-(void) createUser:(NeatoUser *)neatoUser
{
    debugLog(@"createUser called with the Email=%@ and password=%@ and name=%@ and account_type=%@",neatoUser.email,neatoUser.password,neatoUser.name,neatoUser.account_type);
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_CREATE_USER_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_CREATE_USER_POST_STRING, NEATO_API_KEY, neatoUser.name, neatoUser.email, neatoUser.password, neatoUser.account_type, neatoUser.external_social_id] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_CREATE_USER_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
    
}

-(void) createUserHandler:(id) value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"User creation failed!");
        [self notifyRequestFailed:@selector(failedToGetCreateUserHandle:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"User creation failed!");
        [self notifyRequestFailed:@selector(failedToGetCreateUserHandle:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        debugLog(@"user creation successful");
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *authToken = [data valueForKey:USER_HANDLE];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotHandleForCreateUser:)])
            {
                [self.delegate performSelector:@selector(gotHandleForCreateUser:) withObject:authToken];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        debugLog(@"user creation unsuccessful");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetCreateUserHandle:) withError:error];
    }
    
}

-(void) loginNativeUser:(NSString *) email password:(NSString *)password
{
    debugLog(@"loginNativeUser called. Email = %@, Password = %@", email, password);
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_USER_AUTH_TOKEN_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_AUTH_TOKEN_NATIVE_POST_STRING, NEATO_API_KEY,ACCOUNT_TYPE_NATIVE,email,password] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:LOGIN_NATIVE_USER_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}


-(void) loginNativeUserHandler:(id) value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Login request failed!");
        [self notifyRequestFailed:@selector(failedToGetLoginHandle:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Login request failed!");
        [self notifyRequestFailed:@selector(failedToGetLoginHandle:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        NSString *authToken = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotUserHandleForLogin:)])
            {
                [self.delegate performSelector:@selector(gotUserHandleForLogin:) withObject:authToken];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetLoginHandle:) withError:error];
    }
    
}


-(void) getRobotDetails:(NSString *)serialNumber
{
    debugLog(@"getRobotDetails called with serial number %@",serialNumber);
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_ROBOT_DETAILS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING,NEATO_API_KEY, serialNumber] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_ROBOT_DETAILS_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
    
}

-(void)getRobotDetailsHandler:(id)value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to get robot details!");
        [self notifyRequestFailed:@selector(failedToGetRobotDetailsWihError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Failed to get robot details!");
        [self notifyRequestFailed:@selector(failedToGetRobotDetailsWihError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        
        NSDictionary *robotData = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotRobotDetails:)])
            {
                [self.delegate performSelector:@selector(gotRobotDetails:) withObject:robot];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetRobotDetailsWihError:) withError:error];
    }
}

-(void) getUserAccountDetails:(NSString *) authToken email:(NSString *) email
{
    debugLog(@"authToken = %@, email = %@", authToken, email);
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_USER_DETAILS_URL]];
    [request setHTTPMethod:@"POST"];
    if (email == nil) {
        email = @"";
    }
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_DETAILS_POST_STRING,NEATO_API_KEY,email,authToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_USER_DETAILS_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
    
}

-(void) getUserDetailsHandler:(id) value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to get user details");
        [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Failed to get user details");
        [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        NSDictionary *userData = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NeatoUser *user = [[NeatoUser alloc] initWithDictionary:userData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotUserDetails:)])
            {
                [self.delegate performSelector:@selector(gotUserDetails:) withObject:user];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
    }
}


// TODO: Needs implmentation
-(void) loginFacebookUser:(NSString *) externalSocialId
{
    
}

-(void) associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken;
{
    debugLog(@"");
    if (authToken == nil || email == nil)
    {
        debugLog(@"authToken or email is NIL. Will not fetch associated robots.");
        [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:[AppHelper nserrorWithDescription:@"authToken or Email is NIL. Will not fetch associated robots." code:200]];
        return;
    }
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_ASSOCIATED_ROBOTS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ASSOCIATED_ROBOTS_POST_STRING,NEATO_API_KEY, authToken, email] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_ASSOCIATED_ROBOTS_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void) getAssociatedRobotsHandler:(id) value
{
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to get associated robots!");
        [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Failed to get associated robots!");
        [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:value];
        return;
    }
    
    debugLog(@"");
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    NSMutableArray *neatoRobotsArr = [[NSMutableArray alloc] init];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        // We should now update the DB with latest data from server
        NSArray *robotsDictArr = [jsonData objectForKey:NEATO_RESPONSE_RESULT];
        if ([robotsDictArr count] == 0)
        {
            debugLog(@"No associated found at the server!!");
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self.delegate respondsToSelector:@selector(gotUserAssociatedRobots:)])
                {
                    [self.delegate performSelector:@selector(gotUserAssociatedRobots:) withObject:neatoRobotsArr];
                }
                self.delegate = nil;
                self.retained_self = nil;
            });
            return;
        }
        else
        {
            // We have some robots at the server
            for (NSDictionary *robotsDict in robotsDictArr) {
                NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotsDict];
                [neatoRobotsArr addObject:robot];
            }
            debugLog(@"Found %d robot\robots associated with current user.", [neatoRobotsArr count]);
            
            // Notify caller on main thread
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self.delegate respondsToSelector:@selector(gotUserAssociatedRobots:)])
                {
                    [self.delegate performSelector:@selector(gotUserAssociatedRobots:) withObject:neatoRobotsArr];
                }
                self.delegate = nil;
                self.retained_self = nil;
            });
            
        }
    }
    else
    {
        debugLog(@"Failed to get associated robots. We would not have latest robots in the local storage!!");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:error];
    }
    self.retained_self = nil;
}

-(void) updateUserAuthToken:(NSString *)authToken
{
    debugLog(@"");
    if (authToken == nil)
    {
        debugLog(@"authToken is NIL. Will not update user auth token.");
        return;
    }
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_UPDATE_AUTH_TOKEN_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:UPDATE_AUTH_TOKEN_POST_STRING,NEATO_API_KEY,authToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:UPDATE_AUTH_TOKEN_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void) updateAuthTokenHandler:(id) value
{
    if (!value || [value isKindOfClass:[NSError class]])
    {
        debugLog(@"Failed to update users auth token with error = %@", value);
        return;
    }
    
    debugLog(@"");
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        debugLog(@"Users auth token expiry extended.");
    }
    else
    {
        debugLog(@"Failed to extend user auth toekn expiry!!");
        // TODO: We may want to notify the caller about the falure
    }
    self.retained_self = nil;
}

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector])
        {
            [self.delegate performSelector:selector withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (NSString *)getRobotProfileDataFromKey:(NSString *)key value:(NSString *)value {
    return [NSString stringWithFormat:ROBOT_PROFILE_DATA_FORMAT, key, value];
}

- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if (robotId == nil || robotName == nil)
    {
        debugLog(@"robotId or robotName is NIL. Will not update robot name.");
        return;
    }
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_SET_ROBOT_PROFILE_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:SET_ROBOT_PROFILE_POST_STRING,NEATO_API_KEY, robotId, [self getRobotProfileDataFromKey:@"name" value:robotName]] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:SET_ROBOT_NAME_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)setRobotNameHandler:(id)value {
    if (value == nil) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to set robot name!");
        [self notifyRequestFailed:@selector(failedToUpdateRobotNameWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Failed to set robot name!");
        [self notifyRequestFailed:@selector(failedToUpdateRobotNameWithError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        // Notify caller on main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(robotNameUpdated)])
            {
                [self.delegate performSelector:@selector(robotNameUpdated)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
        
    }
    else {
        debugLog(@"Failed to change neato robot name.Won't update in database.");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToUpdateRobotNameWithError:) withError:error];
        
    }
}

- (void)onlineStatusForRobotWithId:(NSString *)robotId {
    if (robotId == nil)
    {
        return;
    }
    self.retained_self = self;
    self.robotId = robotId;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_ROBOT_ONLINE_STATUS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_ONLINE_STATUS_POST_STRING,NEATO_API_KEY,robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_ROBOT_ONLINE_STATUS_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void)getRobotOnlineStatusHandler:(id)value {
    debugLog(@"");
    if (value == nil)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to get robot online status!");
        [self notifyRequestFailed:@selector(failedToGetRobotOnlineStatusWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]])
    {
        debugLog(@"Failed to get robot online status!");
        [self notifyRequestFailed:@selector(failedToGetRobotOnlineStatusWithError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        NSString *onlineStatus = [[jsonData valueForKey:NEATO_RESPONSE_RESULT] valueForKey:NEATO_ROBOT_ONLINE_STATUS];
        // Notify caller on main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(onlineStatus:forRobotWithId:)])
            {
                [self.delegate performSelector:@selector(onlineStatus:forRobotWithId:) withObject:onlineStatus withObject:self.robotId];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
        
    }
    else
    {
        debugLog(@"Failed to get robot online status.");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetRobotOnlineStatusWithError:) withError:error];
        
    }
    
}

-(void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)email handler:(NSString *)handler {
    if (email == nil)
    {
        debugLog(@"email is NIL. Will not update robot name.");
        SEL selector = NSSelectorFromString(handler);
        [self performSelector:selector withObject:[AppHelper nserrorWithDescription:@"email is NIL. Will not update robot name." code:200]];
        return;
    }
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_DISSOCIATE_ALL_ROBOTS_URL]];
    [request setHTTPMethod:@"POST"];
    if (!robotId) {
        robotId = @"";
    }
    [request setHTTPBody:[[NSString stringWithFormat:DISSOCIATE_ALL_ROBOTS_POST_STRING,NEATO_API_KEY, email, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:handler forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)email {
    debugLog(@"");
    [self dissociateRobotWithId:robotId fromUserWithEmail:email handler:DISSOCIATE_ROBOT_RESPONSE_HANDLER];
}

- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email {
    debugLog(@"");
    [self dissociateRobotWithId:nil fromUserWithEmail:email handler:DISSOCIATE_ALL_ROBOTS_RESPONSE_HANDLER];
}

- (void)dissociateRobotHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to dissociated robot!");
        [self notifyRequestFailed:@selector(failedToDissociateRobotWithError:) withError:error];
        return;
    }
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Failed to dissociated robot with Error = %@!", value);
        [self notifyRequestFailed:@selector(failedToDissociateRobotWithError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
        debugLog(@"%@", message);
        // Notify caller on main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(robotDissociatedWithMessage:)])
            {
                [self.delegate performSelector:@selector(robotDissociatedWithMessage:) withObject:message];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        debugLog(@"Failed to dissociate all robots from user");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToDissociateRobotWithError:) withError:error];
    }
}

- (void)dissociateAllRobotsHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to dissociated robots!");
        [self notifyRequestFailed:@selector(failedToDissociateAllRobots:) withError:error];
        return;
    }
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Failed to get dissociated robots!");
        [self notifyRequestFailed:@selector(failedToDissociateAllRobots:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
        debugLog(@"%@", message);
        // Notify caller on main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(dissociatedAllRobots:)])
            {
                [self.delegate performSelector:@selector(dissociatedAllRobots:) withObject:message];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        debugLog(@"Failed to dissociate all robots from user");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToDissociateAllRobots:) withError:error];
    }
}

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken {
    debugLog(@"registerPushNotification called. Email = %@, deviceToken = %@", email, deviceToken);
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_REGISTER_FOR_PUSH_NOTIFICATION_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:PUSH_NOTIFICATION_REGISTRATION_POST_STRING, NEATO_API_KEY,
                           email, (long)deviceType, deviceToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:deviceToken forHTTPHeaderField:PUSH_NOTIFICATION_DEVICE_TOKEN];
    [request setValue:PUSH_NOTIFICATION_REGISTRATION_REPOSNE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}


- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken {
    debugLog(@"unregisterPushNotification called. deviceToken = %@", deviceToken);
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_UNREGISTER_FOR_PUSH_NOTIFICATION_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:PUSH_NOTIFICATION_UNREGISTRATION_POST_STRING, NEATO_API_KEY,
                           deviceToken] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:PUSH_NOTIFICATION_UNREGISTRATION_REPOSNE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}


- (void)pushNotificationRegistrationHandler:(id)value connection:(NSURLConnection *)connection {
  if (!value) {
    NSMutableDictionary* details = [NSMutableDictionary dictionary];
    [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
    
    NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
    debugLog(@"push notification registration failed");
    [self notifyRequestFailed:@selector(pushNotificationRegistrationFailedWithError:) withError:error];
    return;
  }
  
  if ([value isKindOfClass:[NSError class]]) {
    debugLog(@"push notification registration failed");

    [self notifyRequestFailed:@selector(pushNotificationRegistrationFailedWithError:) withError:value];
    return;
  }
  
  NSDictionary *jsonData = [AppHelper parseJSON:value];
  NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
  debugLog(@"status = %d", [status intValue]);
  if ([status intValue] == NEATO_STATUS_SUCCESS) {
     dispatch_async(dispatch_get_main_queue(), ^{
      if ([self.delegate respondsToSelector:@selector(pushNotificationRegisteredForDeviceToken:)]) {
        NSString *deviceToken = [[connection originalRequest] valueForHTTPHeaderField:PUSH_NOTIFICATION_DEVICE_TOKEN];
        [self.delegate performSelector:@selector(pushNotificationRegisteredForDeviceToken:) withObject:deviceToken];
      }
      self.delegate = nil;
      self.retained_self = nil;
    });
  }
  else {
    NSMutableDictionary* details = [NSMutableDictionary dictionary];
    [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
    
    NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
    [self notifyRequestFailed:@selector(pushNotificationRegistrationFailedWithError:) withError:error];
  }
  
}

-(void) pushNotificationUnregistrationHandler:(id) value
{
  if (value == nil) {
    NSMutableDictionary* details = [NSMutableDictionary dictionary];
    [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
    
    NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
    debugLog(@"push notification unregistration failed");
    [self notifyRequestFailed:@selector(pushNotificationUnregistrationFailed:) withError:error];
    return;
  }
  
  if ([value isKindOfClass:[NSError class]]) {
    debugLog(@"push notification unregistration failed");
    [self notifyRequestFailed:@selector(pushNotificationUnregistrationFailed:) withError:value];
    return;
  }
  
  NSDictionary *jsonData = [AppHelper parseJSON:value];
  NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
  debugLog(@"status = %d", [status intValue]);
  if ([status intValue] == NEATO_STATUS_SUCCESS) {
    dispatch_async(dispatch_get_main_queue(), ^{
      debugLog(@"push notification unregistration succeeded");
      if ([self.delegate respondsToSelector:@selector(pushNotificationUnregistrationSuccess:)])
      {
        [self.delegate performSelector:@selector(pushNotificationUnregistrationSuccess:)];
      }
      self.delegate = nil;
      self.retained_self = nil;
    });
  }
  else
  {
    NSMutableDictionary* details = [NSMutableDictionary dictionary];
    [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
    
    NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
    debugLog(@"push notification unregistration failed. Error = %@", error);
    [self notifyRequestFailed:@selector(pushNotificationUnregistrationFailed:) withError:error];
  }
}

- (void)isUserValidatedForEmail:(NSString *)email {
    debugLog(@"Email = %@", email);
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_IS_USER_VALIDATED_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_IS_USER_VALIDATED_POST_STRING, NEATO_API_KEY, email]
                          dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:IS_USER_VALIDATED_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)isUserValidatedHandler:(id)value {
    if (!value) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"User Validation failed!");
        [self notifyRequestFailed:@selector(userValidationFailedWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"User Validation failed!");
        [self notifyRequestFailed:@selector(userValidationFailedWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        NSMutableDictionary* resultData = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(validatedUserWithResult:)])
            {
                [self.delegate performSelector:@selector(validatedUserWithResult:) withObject:resultData];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(userValidationFailedWithError:) withError:error];
    }
}

- (void)resendValidationEmail:(NSString *)email {
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_RESEND_VALIDATION_EMAIL_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_RESEND_VALIDATION_EMAIL_POST_STRING, NEATO_API_KEY, email] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:RESEND_VALIDATION_EMAIL_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)resendValidationEmailHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"resend validation email request failed!");
        [self notifyRequestFailed:@selector(failedToResendValidationEmailWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"resend validation email request failed!");
        [self notifyRequestFailed:@selector(failedToResendValidationEmailWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        NSDictionary *result = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *message = [result valueForKey:NEATO_RESPONSE_MESSAGE];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(resendValidationEmailSucceededWithMessage:)]) {
                [self.delegate performSelector:@selector(resendValidationEmailSucceededWithMessage:) withObject:message];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToResendValidationEmailWithError:) withError:error];
    }
    
}

- (void)forgetPasswordForEmail:(NSString *)email {
    debugLog(@"ForgetPassword for email : %@",email);
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_FORGET_PASSWORD_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_FORGET_PASSWORD_POST_STRING, NEATO_API_KEY, email] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:FORGET_PASSWORD_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)forgetPasswordHandler:(id)value {
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:ERROR_TYPE_UNKNOWN];
        debugLog(@"Forget password failed!");
        [self notifyRequestFailed:@selector(failedToForgetPasswordWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Forget password failed!");
        NSError *error = [AppHelper nserrorWithDescription:[value objectForKey:NSLocalizedDescriptionKey] code:ERROR_NETWORK_ERROR];
        [self notifyRequestFailed:@selector(failedToForgetPasswordWithError:) withError:error];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(forgetPasswordSuccess)]) {
                [self.delegate performSelector:@selector(forgetPasswordSuccess)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        debugLog(@"Forget password unsuccessful");
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:ERROR_SERVER_ERROR];
        [self notifyRequestFailed:@selector(failedToForgetPasswordWithError:) withError:error];
    }
}

- (void)changePasswordFromOldPassword:(NSString *)oldPassword toNewPassword:(NSString *)newPassword authToken:(NSString *)authToken {
    debugLog(@"Change password from oldPassword : %@ toNewPassword : %@", oldPassword, newPassword);
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_CHANGE_PASSWORD_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_CHANGE_PASSWORD_POST_STRING, NEATO_API_KEY, authToken, oldPassword, newPassword] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:CHANGE_PASSWORD_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)changePasswordHandler:(id)value {
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:ERROR_TYPE_UNKNOWN];
        debugLog(@"Change password failed!");
        [self notifyRequestFailed:@selector(failedToChangePasswordWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Change password failed!");
        NSError *error = [AppHelper nserrorWithDescription:[value objectForKey:NSLocalizedDescriptionKey] code:ERROR_NETWORK_ERROR];
        [self notifyRequestFailed:@selector(failedToChangePasswordWithError:) withError:error];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(changePasswordSuccess)]) {
                [self.delegate performSelector:@selector(changePasswordSuccess)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        debugLog(@"Change password unsuccessful");
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:ERROR_SERVER_ERROR];
        [self notifyRequestFailed:@selector(failedToChangePasswordWithError:) withError:error];
    }
}

- (void)createUser2:(NeatoUser *)neatoUser {
    self.retained_self = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_CREATE_USER2_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:CREATE_USER2_POST_STRING, NEATO_API_KEY, neatoUser.name, neatoUser.email, neatoUser.alternateEmail, neatoUser.password, neatoUser.account_type, neatoUser.external_social_id] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:CREATE_USER2_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)createUserHandler2:(id)value {
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:ERROR_TYPE_UNKNOWN];
        debugLog(@"User creation2 failed!");
        [self notifyRequestFailed:@selector(failedToGetCreateUserHandle2Error:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"User creation2 failed!");
        NSError *error = [AppHelper nserrorWithDescription:[value objectForKey:NSLocalizedDescriptionKey] code:ERROR_NETWORK_ERROR];
        [self notifyRequestFailed:@selector(failedToGetCreateUserHandle2Error:) withError:error];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        debugLog(@"User creation successful");
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSString *authToken = [data valueForKey:USER_HANDLE];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotHandleForCreateUser2:)]) {
                [self.delegate performSelector:@selector(gotHandleForCreateUser2:) withObject:authToken];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        debugLog(@"User creation2 unsuccessful");
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:ERROR_SERVER_ERROR];
        [self notifyRequestFailed:@selector(failedToGetCreateUserHandle2Error:) withError:error];
    }
}

- (NSString *)paramsForEnabledSchedule:(BOOL)enable ofType:(int)scheduleType {
    if(scheduleType == NEATO_SCHEDULE_BASIC_INT) {
        return [self getRobotProfileDataFromKey:ENABLE_BASIC_SCHEDULE value:enable?@"TRUE":@"FALSE"];
    }
    else if(scheduleType == NEATO_SCHEDULE_ADVANCE_INT) {
        return [self getRobotProfileDataFromKey:ENABLE_ADVANCED_SCHEDULE value:enable?@"TRUE":@"FALSE"];
    }
    else {
        return @"";
    }
}

- (void)enableDisable:(BOOL)enable scheduleType:(int)scheduleType forRobot:(NSString *)robotId withUserEmail:(NSString *)email {
    self.retained_self = self;
    NSString *enableDisableCommand = [self paramsForEnabledSchedule:enable ofType:scheduleType];
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_SET_ROBOT_PROFILE_2_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:ENABLE_DISABLE_SCHEDULE_POST_STRING, NEATO_API_KEY, robotId, NULL, email, NULL, enableDisableCommand] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:ENABLE_DISABLE_SCHEDULE_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}


- (void)enableDisableScheduleHandler:(id)value {
    if (!value) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToEnableDisableScheduleWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        [self notifyRequestFailed:@selector(failedToEnableDisableScheduleWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
       dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(enabledDisabledScheduleSuccess)])
            {
                [self.delegate performSelector:@selector(enabledDisabledScheduleSuccess)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToEnableDisableScheduleWithError:) withError:error];
    }
    
}

@end

