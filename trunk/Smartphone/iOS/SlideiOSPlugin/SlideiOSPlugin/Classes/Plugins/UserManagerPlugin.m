//
//  UserManagerPlugin.m
//  SlideiOSPlugin
//


#import "UserManagerPlugin.h"
#import "NeatoConstants.h"
#import "AppHelper.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoDBHelper.h"
#import "PluginConstants.h"
#import "NeatoRobotHelper.h"

@implementation UserManagerPlugin

- (void) login:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    NSString *password = [parameters valueForKey:KEY_PASSWORD];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper loginUserWithEmail:email password:password callbackID:callbackId];
}

-(void) loginFailedWithError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

-(void) loginSuccess:(NeatoUser *) user  callbackId:(NSString *)callbackId
{
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:user.name forKey:KEY_USER_NAME];
    [data setValue:user.userId forKey:KEY_USER_ID];
    [data setValue:user.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) userCreated:(NeatoUser *) neatoUser  callbackId:(NSString *)callbackId
{
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:neatoUser.name forKey:KEY_USER_NAME];
    [data setValue:neatoUser.userId forKey:KEY_USER_ID];
    [data setValue:neatoUser.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) failedToCreateUserWithError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"");
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}


- (void) logout:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper logoutUserEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken] callbackID:callbackId];
}

-(void)logoutRequestFailedWithEror:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

-(void)userCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void) createUser:(CDVInvokedUrlCommand *)command {
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are : %@",parameters);
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    
    NeatoUser *neatoUser = [[NeatoUser alloc] init];
    neatoUser.email = [parameters objectForKey:@"email"];
    neatoUser.password = [parameters objectForKey:@"password"];
    neatoUser.name = [parameters objectForKey:@"userName"];
    neatoUser.account_type = ACCOUNT_TYPE_NATIVE;
    [callWrapper createUser:neatoUser callbackID:callbackId];
}

- (void) isLoggedIn:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    
    NSString *callbackId = command.callbackId;
    bool loggedIn = [[[UserManagerCallWrapper alloc] init] isUserLoggedIn];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[[NSNumber numberWithBool:loggedIn] intValue]];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) failedToGetUserDetailsWithError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
    
}

- (void) getUserDetails:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}

-(void) robotAssociationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}


- (void) associateRobot:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;   
    
    NSString *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    //TODO: Not using the value sent by the UI. Rather using the email stored in local storage
    // What are we supposed to use? Whats the point of getting the value of Email from UI
    [callWrapper setRobotUserEmail:[NeatoUserHelper getLoggedInUserEmail] serialNumber:[parameters valueForKey:KEY_ROBOT_ID] callbackID:callbackId];
}

-(void) failedToGetRobotDetailsWihError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
}

- (void) getAssociatedRobots:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    
}

-(void) robotCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId
{
    debugLog(@"Error = %@", error);
}

- (void) getAssociatedRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}


- (void) disassociateRobot:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}


- (void) disassociateAllRobots:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}

- (void) debugGetConfigDetails:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSDictionary *appInfo = [AppHelper getAppDebugInfo];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:appInfo];
    [self writeJavascript:[result toSuccessCallbackString:command.callbackId]];
    debugLog(@"Done");
}



/*-(void) requestFailed:(NSError *) error callbackId:(NSString *)callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}*/


-(void) gotUserDetails:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId
{
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:neatoUser.name forKey:KEY_USER_NAME];
    [data setValue:neatoUser.userId forKey:KEY_USER_ID];
    [data setValue:neatoUser.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) gotRobotDetails:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId
{
    debugLog(@"");
}


-(void) robotCreated:(NSString *)callbackId
{
    debugLog(@"");
}


-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId callbackId:(NSString *)callbackId;
{
    debugLog(@"");
    NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
    NSMutableDictionary *robotDict = [[NSMutableDictionary alloc] init];
    [robotDict setValue:robot.serialNumber forKey:KEY_ROBOT_ID];
    [robotDict setValue:[NSNumber numberWithInt:ROBOT_ASSOCIATION_SUCCESS] forKey:@"responseStat"];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:robotDict];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) userLoggedOut:(NSString *)callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"User logged out."];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

@end
