#import "NeatoServerHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "NeatoConstants.h"


#define SERVER_REPONSE_HANDLER_KEY @"key_server_response_handler"
#define LOGIN_NATIVE_USER_REPOSNE_HANDLER @"loginNativeUserHandler:"
#define GET_USER_DETAILS_REPOSNE_HANDLER @"getUserDetailsHandler:"
#define GET_CREATE_USER_RESPONSE_HANDLER @"createUserHandler:"
#define GET_CREATE_ROBOT_RESPONSE_HANDLER @"createRobothandler:"
#define GET_ROBOT_DETAILS_RESPONSE_HANDLER @"getRobotDetailsHandler:"
#define SET_ROBOT_USER_RESPONSE_HANDLER @"setRobotUserResponseHandler:"
#define GET_USER_LOGOUT_RESPONSE_HANDLER @"getUserLogoutResponseHandler:"
#define UPDATE_AUTH_TOKEN_RESPONSE_HANDLER @"updateAuthTokenHandler:"
#define GET_ASSOCIATED_ROBOTS_RESPONSE_HANDLER @"getAssociatedRobotsHandler:"

#define GET_AUTH_TOKEN_NATIVE_POST_STRING @"api_key=%@&account_type=%@&email=%@&password=%@"
#define GET_USER_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define GET_CREATE_USER_POST_STRING @"api_key=%@&name=%@&email=%@&password=%@&account_type=%@&external_social_id=%@"
#define GET_CREATE_ROBOT_POST_STRING @"api_key=%@&serial_number=%@&name=%@"
#define GET_ROBOT_DETAILS_POST_STRING @"api_key=%@&serial_number=%@"
#define SET_ROBOT_USER_POST_STRING @"api_key=%@&email=%@&serial_number=%@"
#define GET_USER_LOGOUT_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define UPDATE_AUTH_TOKEN_POST_STRING @"api_key=%@&auth_token=%@"
#define GET_ASSOCIATED_ROBOTS_POST_STRING @"api_key=%@&auth_token=%@&email=%@"




@interface NeatoServerHelper()

@property(nonatomic, retain) NeatoServerHelper *retained_self;
//@property(nonatomic, retain) NSMutableData *responseData;
//@property(nonatomic, readwrite) int responseCode;
@property(nonatomic, retain) NSString *robotId;

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error;
@end


@implementation NeatoServerHelper
@synthesize retained_self = _retained_self;
//@synthesize responseData = _responseData;
//@synthesize responseCode = _responseCode;
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
    SEL selector = NSSelectorFromString(selectorStr);
    [self performSelector:selector withObject:responseData];
}

// This gets called when the connection fails for any reason.
-(void) requestFailedForConnection:(NSURLConnection *)connection error:(NSError *) error
{
    debugLog(@"");
    NSString *selectorStr = [[connection originalRequest] valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    [self performSelector:selector withObject:error];
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
    
    [request setValue:LOGIN_NATIVE_USER_REPOSNE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
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
    if (email == nil)
    {
        email = @"";
    }
    [request setHTTPBody:[[NSString stringWithFormat:GET_USER_DETAILS_POST_STRING,NEATO_API_KEY,email,authToken] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_USER_DETAILS_REPOSNE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
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

/*- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
 {
 debugLog(@"connection:didFailWithError called. Error = %@", error);
 [self notifyRequestFailed:error];
 }
 
 - (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
 {
 debugLog(@"connection:didReceiveData called.");
 [self.responseData appendData:data];
 }
 
 - (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
 {
 debugLog(@"connection:didReceiveResponse called.");
 NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*) response;
 self.responseCode = [httpResponse statusCode];
 }
 
 - (void)connectionDidFinishLoading:(NSURLConnection *)connection
 {
 debugLog(@"connectionDidFinishLoading called.");
 NSString *selectorStr = [[connection originalRequest] valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
 SEL selector = NSSelectorFromString(selectorStr);
 if (self.responseCode == 200)
 {
 [self performSelector:selector withObject:self.responseData];
 }
 else
 {
 NSMutableDictionary* details = [NSMutableDictionary dictionary];
 [details setValue:NETWORK_CONNECTION_FAILURE_MSG forKey:NSLocalizedDescriptionKey];
 
 NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
 [self notifyRequestFailed:error];
 }
 }*/

-(void) getAssociatedRobots:(NSString *)email authToken:(NSString *)authToken;
{
    debugLog(@"");
    if (authToken == nil)
    {
        debugLog(@"authToken is NIL. Will not fetch associated robots.");
        return;
    }
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_ASSOCIATED_ROBOTS_URL]];
    [request setHTTPMethod:@"POST"];
    if (email == nil)
    {
        email = @"";
    }
    
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

@end

