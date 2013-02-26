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
    debugLog(@"received parameters : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    NSString *password = [parameters valueForKey:KEY_PASSWORD];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper loginUserWithEmail:email password:password callbackID:callbackId];
}

-(void) loginFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

-(void) loginSuccess:(NeatoUser *) user  callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:user.name forKey:KEY_USER_NAME];
    [data setValue:user.userId forKey:KEY_USER_ID];
    [data setValue:user.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) userCreated:(NeatoUser *) neatoUser  callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:neatoUser.name forKey:KEY_USER_NAME];
    [data setValue:neatoUser.userId forKey:KEY_USER_ID];
    [data setValue:neatoUser.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) failedToCreateUserWithError:(NSError *)error callbackId:(NSString *)callbackId {
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

-(void)logoutRequestFailedWithEror:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

-(void)userCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
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
    debugLog(@"received parameters : %@",parameters);
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    
    NeatoUser *neatoUser = [[NeatoUser alloc] init];
    neatoUser.email = [parameters objectForKey:@"email"];
    neatoUser.password = [parameters objectForKey:@"password"];
    neatoUser.name = [parameters objectForKey:@"userName"];
    neatoUser.account_type = ACCOUNT_TYPE_NATIVE;
    [callWrapper createUser:neatoUser callbackID:callbackId];
}

- (void) isLoggedIn:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    bool loggedIn = [[[UserManagerCallWrapper alloc] init] isUserLoggedIn];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[[NSNumber numberWithBool:loggedIn] intValue]];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) failedToGetUserDetailsWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)getUserDetails:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    NSString *email = [parameters objectForKey:@"email"];
    [callWrapper getUserDetailsForEmail:email authToken:[NeatoUserHelper getUsersAuthToken] callbackID:callbackId];
}

-(void) robotAssociationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}


- (void) associateRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;   
    
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);

    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    NSString *email = [parameters objectForKey:@"email"];
    [callWrapper setRobotUserEmail:email serialNumber:[parameters objectForKey:KEY_ROBOT_ID] callbackID:callbackId];
}

- (void)associateRobot2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters received = %@", parameters);
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    NSString *email = [parameters objectForKey:@"email"];
    [callWrapper setRobotUserEmail2:email forRobotId:[parameters valueForKey:KEY_ROBOT_ID] callbackID:callbackId];
}

- (void)robotAssociation2FailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)userAssociateWithRobot:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *jsonRobot = [[NSMutableDictionary alloc] init];
    [jsonRobot setValue:neatoRobot.serialNumber forKey:KEY_ROBOT_ID];
    [jsonRobot setValue:neatoRobot.name forKey:KEY_ROBOT_NAME];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonRobot];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


- (void)getAssociatedRobots:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:@"email"];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper associatedRobotsForUserWithEmail:email authToken:[NeatoUserHelper getUsersAuthToken] callbackId:callbackId];

}

- (void)robotCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
}

- (void)disassociateRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:@"email"];
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper dissociateRobotWithId:robotId fromUserWithEmail:email callbackId:callbackId];
}


- (void)disassociateAllRobots:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:@"email"];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper dissociateAllRobotsForUserWithEmail:email callbackID:callbackId];
}

- (void)debugGetConfigDetails:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSDictionary *appInfo = [AppHelper getAppDebugInfo];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:appInfo];
    [self writeJavascript:[result toSuccessCallbackString:command.callbackId]];
    debugLog(@"Done");
}



- (void)gotUserDetails:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:neatoUser.name forKey:KEY_USER_NAME];
    [data setValue:neatoUser.userId forKey:KEY_USER_ID];
    [data setValue:neatoUser.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)robotCreated:(NSString *)callbackId {
    debugLog(@"");
}


-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
    NSMutableDictionary *robotDict = [[NSMutableDictionary alloc] init];
    [robotDict setValue:robot.serialNumber forKey:KEY_ROBOT_ID];
    [robotDict setValue:[NSNumber numberWithInt:ROBOT_ASSOCIATION_SUCCESS] forKey:@"responseStat"];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:robotDict];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) userLoggedOut:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"User logged out."];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)dissociatedAllRobots:(NSString *)message callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];   
}

- (void)failedToDissociateAllRobots:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];  
}

- (void)failedToDissociateRobotWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}
- (void)robotDissociatedWithMessage:(NSString *)message callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)gotUserAssociatedRobots:(NSMutableArray *)robots callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableArray *jsonArray = [[NSMutableArray alloc] init];
    for (int i=0 ; i<[robots count] ; i++) {
        NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
        [data setValue:[[robots objectAtIndex:i] name] forKey:KEY_ROBOT_NAME];
        [data setValue:[[robots objectAtIndex:i] serialNumber] forKey:KEY_ROBOT_ID];
        [jsonArray addObject:data];
    }
      
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:jsonArray];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];

}

- (void)failedToGetAssociatedRobotsWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]]; 
}

@end
