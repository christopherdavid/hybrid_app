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
#import "NeatoNotification.h"

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
    [self sendError:error forCallbackId:callbackId];
}

-(void) loginSuccess:(NeatoUser *) user  callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:user.name forKey:KEY_USER_NAME];
    [data setValue:user.userId forKey:KEY_USER_ID];
    [data setValue:user.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
  
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
                            (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
}

-(void) userCreated:(NeatoUser *) neatoUser  callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:neatoUser.name forKey:KEY_USER_NAME];
    [data setValue:neatoUser.userId forKey:KEY_USER_ID];
    [data setValue:neatoUser.email forKey:KEY_EMAIL];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
  
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
                    (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
}

-(void) failedToCreateUserWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
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
    [self sendError:error forCallbackId:callbackId];
}

-(void)userCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    [self sendError:error forCallbackId:callbackId];
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
    [self sendError:error forCallbackId:callbackId];
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
    [self sendError:error forCallbackId:callbackId];
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
    [self sendError:error forCallbackId:callbackId];
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
    [robotDict setValue:[NSNumber numberWithInteger:ROBOT_ASSOCIATION_SUCCESS] forKey:@"responseStat"];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:robotDict];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) userLoggedOut:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"User logged out."];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    [[UIApplication sharedApplication]  unregisterForRemoteNotifications];
}

- (void)dissociatedAllRobots:(NSString *)message callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];   
}

- (void)failedToDissociateAllRobots:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)failedToDissociateRobotWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
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
    [self sendError:error forCallbackId:callbackId];
}

- (void)isUserValidated:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper isUserValidatedForEmail:email callbackID:callbackId];
}

- (void)validatedUserWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    
    [data setValue:[resultData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NEATO_RESPONSE_MESSAGE];
    [data setValue:[resultData valueForKey:NEATO_VALIDATION_STATUS] forKey:NEATO_VALIDATION_STATUS];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)userValidationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    [self sendError:error forCallbackId:callbackId];
}

- (void)resendValidationMail:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper resendValidationEmail:email callbackID:callbackId];
}

- (void)failedToResendValidationEmailWithError:(NSError *)error callbackId:(NSString *)callbackId {    
    debugLog(@"Error = %@", error);
    [self sendError:error forCallbackId:callbackId];
}

- (void)resendValidationEmailSucceededWithMessage:(NSString *)message callbackId:(NSString *)callbackId {
    debugLog(@"message in manager plugin=%@",message);
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:message forKey:KEY_MESSAGE];
    debugLog(@"json message in manager plugin=%@",data);
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)forgetPassword:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper forgetPasswordForEmail:[parameters objectForKey:KEY_EMAIL] callbackID:callbackId];
}

- (void)forgetPasswordSuccessWithCallbackId:(NSString *)callbackId {
    // Empty Dictionary object.
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)failedToForgetPasswordWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)changePassword:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper changePasswordFromOldPassword:[parameters objectForKey:KEY_CURRENT_PASSWORD] toNewPassword:[parameters objectForKey:KEY_NEW_PASSWORD] callbackID:callbackId];
}

- (void)changePasswordSuccessWithCallbackId:(NSString *)callbackId {
    // Empty Dictionary object.
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)failedToChangePasswordWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}
- (void)createUser2:(CDVInvokedUrlCommand *)command {
    // Get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    NeatoUser *neatoUser = [[NeatoUser alloc] init];
    neatoUser.email = [parameters objectForKey:KEY_EMAIL];
    neatoUser.password = [parameters objectForKey:KEY_PASSWORD];
    neatoUser.name = [parameters objectForKey:KEY_USER_NAME];
    neatoUser.account_type = ACCOUNT_TYPE_NATIVE;
    neatoUser.alternateEmail = [parameters objectForKey:KEY_ALTERNATE_EMAIL];
    [callWrapper createUser2:neatoUser callbackID:callbackId];
}

- (void)userCreated2:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:neatoUser.name forKey:KEY_USER_NAME];
    [data setValue:neatoUser.userId forKey:KEY_USER_ID];
    [data setValue:neatoUser.email forKey:KEY_EMAIL];
    [data setValue:neatoUser.alternateEmail forKey:KEY_ALTERNATE_EMAIL];
    [data setValue:[neatoUser userValidationStatus] forKey:NEATO_VALIDATION_STATUS];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
     (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
}

- (void)failedToCreateUser2WithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)turnNotificationOnOff:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    // Get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    NeatoNotification *notification = [[NeatoNotification alloc] init];
    notification.notificationId = [parameters valueForKey:KEY_NOTFICATION_ID];
    notification.notificationValue = [AppHelper stringFromBool:[[parameters valueForKey:KEY_ON] boolValue]];
    [callWrapper turnNotification:notification onOffForUserWithEmail:[parameters valueForKey:KEY_EMAIL] callbackID:callbackId];
}

- (void)notificationsTurnedOnOffWithResult:(NSDictionary *)notification callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:notification];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];    
}

- (void)failedToSetUserPushNotificationOptionsWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)getNotificationSettings:(CDVInvokedUrlCommand *)command {
    // Get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper notificationSettingsForUserWithEmail:[parameters objectForKey:KEY_EMAIL] callbackID:callbackId];
}

- (void)userNotificationSettingsData:(NSDictionary *)notificationJson callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:notificationJson];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}
- (void)failedToGetUserPushNotificationSettingsWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)sendError:(NSError *)error forCallbackId:(NSString *)callbackId {
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:[NSNumber numberWithInt:error.code] forKey:KEY_ERROR_CODE];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

@end
