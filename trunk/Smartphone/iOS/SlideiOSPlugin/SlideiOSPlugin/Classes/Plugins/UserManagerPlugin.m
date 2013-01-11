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

- (void) login:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
   
    //get the callback id
    NSString *callbackId = [arguments pop];
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper loginUserWithEmail:DEMO_USER_EMAIL password:DEMO_USER_PASSWORD callbackID:callbackId];
}


- (void) logout:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    //get the callback id
    NSString *callbackId = [arguments pop];
    
    NSLog(@"logout method called.");
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper logoutUserEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken] callbackID:callbackId];
}

- (void) createUser:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    //get the callback id
    NSString *callbackId = [arguments pop];
    
    
    NSString *parameters = [arguments objectAtIndex:0];
    
    NSDictionary *parametersDict = [AppHelper parseJSON:[parameters dataUsingEncoding:NSUTF8StringEncoding]];
    
    NSString *resultStr = [NSString stringWithFormat:@"createUser called with email = [%@], password = [%@], name = [%@]", [parametersDict objectForKey:@"email"], [parametersDict objectForKey:@"password"], [parametersDict objectForKey:@"username"]];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:resultStr];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) isLoggedIn:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");

    NSString *callbackId = [arguments pop];
    bool loggedIn = [[[UserManagerCallWrapper alloc] init] isUserLoggedIn];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[[NSNumber numberWithBool:loggedIn] intValue]];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) getUserDetails:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}


- (void) associateRobot:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
    NSString *callbackId = [arguments pop];
    
    NSString *parameters = [arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSDictionary *data = [AppHelper parseJSON: [parameters dataUsingEncoding:NSUTF8StringEncoding]];
    debugLog(@"data = %@", data);

    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    //TODO: Not using the value sent by the UI. Rather using the email stored in local storage
    // What are we supposed to use? Whats the point of getting the value of Email from UI
    [callWrapper setRobotUserEmail:[NeatoUserHelper getLoggedInUserEmail] serialNumber:[data valueForKey:KEY_ROBOT_ID] callbackID:callbackId];
}


- (void) getAssociatedRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}


- (void) disassociateRobot:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}


- (void) disassociateAllRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}



-(void) requestFailed:(NSError *) error callbackId:(NSString *)callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}


-(void) gotUserDetails:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId
{
    debugLog(@"");
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"User login success!"];
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
