#import "NeatoServerHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "NeatoConstants.h"
#import "AppSettings.h"
#import "NeatoRobotCommand.h"
#import "NeatoUserAttributes.h"
#import "NeatoUserHelper.h"
#import "NeatoErrorCodes.h"
#import "NeatoErrorCodesHelper.h"

@interface NeatoServerHelper() <NSURLConnectionHelperProtocol>
@property (nonatomic, retain) NeatoServerHelper *retained_self;
@property (nonatomic, retain) NSString *robotId;
@end


@implementation NeatoServerHelper

- (void)didLoadData:(NSData *)responseData forRequest:(NSURLRequest *)request {
    debugLog(@"");
    id serverResponse = responseData;
    if ([AppHelper hasServerRequestFailedForResponse:[AppHelper parseJSON:responseData]]) {
        NSDictionary *errorDict = [[AppHelper parseJSON:serverResponse] objectForKey:KEY_NEATO_SERVER_ERROR];
        serverResponse = [AppHelper nserrorWithDescription:[errorDict objectForKey:NEATO_RESPONSE_MESSAGE] code:[[NeatoErrorCodesHelper sharedErrorCodesHelper] uiErrorCodeForServerErrorCode:[[errorDict objectForKey:KEY_NEATO_SERVER_ERROR_CODE] integerValue]]];
    }
    NSString *selectorStr = [request valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSInteger parameterCount = [[selectorStr mutableCopy] replaceOccurrencesOfString:@":" withString:@"," options:NSLiteralSearch range:NSMakeRange(0, [selectorStr length])];
    SEL selector = NSSelectorFromString(selectorStr);
    if (parameterCount == 1) {
        [self performSelector:selector withObject:serverResponse];
    }
    else if (parameterCount == 2) {
        [self performSelector:selector withObject:serverResponse withObject:request];
    }
    else {
        debugLog(@"connectionDidFinishLoading called with invalid number of arguments. Selector = %@, Parameters required = %ld", selectorStr, (long)parameterCount);
    }
}

// This gets called when the request fails for any reason.
- (void)didFailToLoadWithError:(NSError *)error forRequest:(NSURLRequest *)request {
    debugLog(@"");
    NSString *selectorStr = [request valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    NSInteger parameterCount = [[selectorStr mutableCopy] replaceOccurrencesOfString:@":" withString:@"," options:NSLiteralSearch range:NSMakeRange(0, [selectorStr length])];
    if (parameterCount == 1) {
        [self performSelector:selector withObject:error];
    }
    else if (parameterCount == 2) {
        [self performSelector:selector withObject:error withObject:request];
    }
    else {
        debugLog(@"requestFailedForConnection called with invalid number of arguments. Selector = %@, Parameters required = %ld", selectorStr, (long)parameterCount);
    }
}

- (void)getUserDetailsHandler:(id)value {
    if (value == nil) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"Failed to get user details");
        [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Failed to get user details");
        [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        NSMutableDictionary *userData = [[jsonData valueForKey:NEATO_RESPONSE_RESULT] mutableCopy];
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
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
    }
}


// TODO: Needs implmentation
- (void)loginFacebookUser:(NSString *)externalSocialId {
    
}

- (void)updateUserAuthToken:(NSString *)authToken {
    debugLog(@"");
    if (authToken == nil) {
        debugLog(@"authToken is NIL. Will not update user auth token.");
        return;
    }
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_UPDATE_AUTH_TOKEN_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:UPDATE_AUTH_TOKEN_POST_STRING,API_KEY,authToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:UPDATE_AUTH_TOKEN_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)updateAuthTokenHandler:(id)value {
    if (!value || [value isKindOfClass:[NSError class]]) {
        debugLog(@"Failed to update users auth token with error = %@", value);
        return;
    }
    
    debugLog(@"");
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        debugLog(@"Users auth token expiry extended.");
    }
    else {
        debugLog(@"Failed to extend user auth toekn expiry!!");
        // TODO: We may want to notify the caller about the falure
    }
    self.retained_self = nil;
}

- (void)notifyRequestFailed:(SEL) selector withError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector]) {
            [self.delegate performSelector:selector withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (NSString *)getProfileDataFromKey:(NSString *)key value:(NSString *)value {
    return [NSString stringWithFormat:PROFILE_DATA_FORMAT, key, value];
}

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken  notificationServerType:(NSString *)serverType applicationId:(NSString *)applicationId {
    debugLog(@"registerPushNotification called. Email = %@, deviceToken = %@", email, deviceToken);
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_REGISTER_FOR_PUSH_NOTIFICATION_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:PUSH_NOTIFICATION_REGISTRATION_POST_STRING, API_KEY,
                           email, (long)deviceType, deviceToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:deviceToken forHTTPHeaderField:PUSH_NOTIFICATION_DEVICE_TOKEN];
    [request setValue:serverType forHTTPHeaderField:PUSH_NOTIFICATION_SERVER_TYPE];
    [request setValue:applicationId forHTTPHeaderField:PUSH_NOTIFICATION_APPLICATION_ID];
    [request setValue:PUSH_NOTIFICATION_REGISTRATION_REPOSNE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}


- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken {
    debugLog(@"unregisterPushNotification called. deviceToken = %@", deviceToken);
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_UNREGISTER_FOR_PUSH_NOTIFICATION_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:PUSH_NOTIFICATION_UNREGISTRATION_POST_STRING, API_KEY,
                           deviceToken] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:PUSH_NOTIFICATION_UNREGISTRATION_REPOSNE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}


- (void)pushNotificationRegistrationHandler:(id)value request:(NSURLRequest *)request {
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
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(pushNotificationRegisteredForDeviceToken:)]) {
                NSString *deviceToken = [request valueForHTTPHeaderField:PUSH_NOTIFICATION_DEVICE_TOKEN];
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

- (void)pushNotificationUnregistrationHandler:(id)value {
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
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
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
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        debugLog(@"push notification unregistration failed. Error = %@", error);
        [self notifyRequestFailed:@selector(pushNotificationUnregistrationFailed:) withError:error];
    }
}

- (void)sendCommand:(NeatoRobotCommand *)command withSourceEmailId:(NSString *)email {
    NSMutableDictionary *httpHeaderFields = [[NSMutableDictionary alloc] initWithCapacity:4];
    [httpHeaderFields setValue:command.commandId forKey:KEY_COMMAND_ID];
    [httpHeaderFields setValue:command.robotId forKey:KEY_ROBOT_ID];
    [httpHeaderFields setValue:command.xmlCommand forKey:KEY_XML_COMMAND];
    [httpHeaderFields setValue:SEND_COMMAND_RESPONSE_HANDLER forKey:SERVER_REPONSE_HANDLER_KEY];
    [self setRobotProfileDetails3:command forUserWithEmail:email withHttpHeaderFields:httpHeaderFields];
}

- (void)setRobotProfileDetails3:(NeatoRobotCommand *)profile forUserWithEmail:(NSString *)email withHttpHeaderFields:(NSDictionary *)httpHeaderFields {
    self.retained_self = self;
    NSArray *keysArray = [profile.profileDict allKeys];
    NSMutableString *profileKeys = [[NSMutableString alloc] init];
    for (NSString *key in keysArray) {
        NSString *profileKey = [self getProfileDataFromKey:key value:[profile.profileDict valueForKey:key]];
        [profileKeys appendString:profileKey];
    }
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_SET_ROBOT_PROFILE_DETAILS_3]];
    [request setHTTPMethod:@"POST"];
    // TODO: Assuming Notification flag value is always true.
    [request setHTTPBody:[[NSString stringWithFormat:SET_ROBOT_PROFILE_DETAILS_3_POST_STRING, API_KEY, profile.robotId, @"", email, [NeatoUserHelper uniqueDeviceIdForUser], @"", [NSNumber numberWithInt:NOTIFICATION_FLAG_TRUE], profileKeys] dataUsingEncoding:NSUTF8StringEncoding]];
    // Set Header fields.
    NSArray *httpHeaderFieldKeysArray = [httpHeaderFields allKeys];
    for (NSString *key in httpHeaderFieldKeysArray) {
        [request setValue:[httpHeaderFields valueForKey:key] forHTTPHeaderField:key];
    }
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)sendCommandHandler:(id)value request:(NSURLRequest *)request {
    if (!value) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedtoSendCommandWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        [self notifyRequestFailed:@selector(failedtoSendCommandWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    
    NSDictionary *result = [jsonData valueForKey:NEATO_RESPONSE_EXTRA_PARAMS];
    NeatoRobotCommand *command = [[NeatoRobotCommand alloc] init];
    command.commandId = [request valueForHTTPHeaderField:KEY_COMMAND_ID];
    command.robotId = [request valueForHTTPHeaderField:KEY_ROBOT_ID];
    command.xmlCommand = [request valueForHTTPHeaderField:KEY_XML_COMMAND];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(command:sentWithResult:)]) {
                [self.delegate performSelector:@selector(command:sentWithResult:) withObject:command withObject:result];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedtoSendCommandWithError:) withError:error];
    }
}

- (void)setUserAttributes:(NeatoUserAttributes *)attributes forAuthToken:authToken {
    debugLog(@"");
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_SET_USER_ATTRIBUTES]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:SET_USER_ATTRIBUTES_POST_STRING, API_KEY, authToken, attributes.systemName, attributes.systemVersion, attributes.deviceModelName] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:SET_USER_ATTRIBUTES_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)setUserAttributesHandler:(id)value {
    debugLog(@"");
    if (!value) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:UI_ERROR_TYPE_UNKNOWN];
        debugLog(@"set user attribute failed!");
        [self notifyRequestFailed:@selector(failedToSetUserAttributesWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"set user attribute failed!");
        [self notifyRequestFailed:@selector(failedToSetUserAttributesWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(setUserAttributesSucceeded)]) {
                [self.delegate performSelector:@selector(setUserAttributesSucceeded)];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        debugLog(@"set user attribute failed");
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_UNKNOWN];
        [self notifyRequestFailed:@selector(failedToSetUserAttributesWithError:) withError:error];
    }
}

- (void)notifyScheduleUpdatedForProfileDetails:(NeatoRobotCommand *)profileDetails forUserWithEmail:(NSString *)email {
    NSMutableDictionary *httpHeaderFields = [[NSMutableDictionary alloc] initWithCapacity:1];
    [httpHeaderFields setValue:SET_PROFILE_DETAILS_HANDLER forKey:SERVER_REPONSE_HANDLER_KEY];
    [self setRobotProfileDetails3:profileDetails forUserWithEmail:email withHttpHeaderFields:httpHeaderFields];
}

- (void)notifyScheduleUpdatedHandler:(id)value {
    if (!value) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Server did not respond with any data!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToNotifyScheduleUpdatedWithError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        [self notifyRequestFailed:@selector(failedToNotifyScheduleUpdatedWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        NSDictionary *extraParams = [jsonData valueForKey:NEATO_RESPONSE_EXTRA_PARAMS];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(notifyScheduleUpdatedSucceededWithResult:)]) {
                [self.delegate performSelector:@selector(notifyScheduleUpdatedSucceededWithResult:) withObject:extraParams];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToNotifyScheduleUpdatedWithError:) withError:error];
    }
}

- (void)dataForRequest:(NSURLRequest *)request completionBlock:(ServerHelperCompletionBlock)completionBlock {
    [[[NSURLConnectionHelper alloc] init] getDataForRequest:request
                                            completionBlock:^(id response, NSError *error) {
                  if (error) {
                      completionBlock ? completionBlock(nil, error) : nil;
                      return;
                  }
                  NSDictionary *completeServerResponseDict = [AppHelper parseJSON:response];
                  completionBlock ? completionBlock(completeServerResponseDict, nil) : nil;
              }];
}

@end

