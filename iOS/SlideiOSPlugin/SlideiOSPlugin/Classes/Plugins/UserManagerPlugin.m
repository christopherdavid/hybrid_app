//
//  UserManagerPlugin.m
//  SlideiOSPlugin
//


#import "UserManagerPlugin.h"
#import "NeatoConstants.h"
#import "AppHelper.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "PluginConstants.h"
#import "NeatoRobotHelper.h"
#import "NeatoNotification.h"
#import "NeatoServerManager.h"

@interface UserManagerPlugin ()
@property (nonatomic, strong) NeatoServerManager *serverManager;
@end

@implementation UserManagerPlugin

#pragma mark - Public
- (void)userCreated:(NeatoUser *) neatoUser  callbackId:(NSString *)callbackId {
  NSAssert(NO, @"This method should't be called");
}

- (void)failedToCreateUserWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
   NSAssert(NO, @"This method should't be called");
}

- (void)userCreationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
   NSAssert(NO, @"This method should't be called");
}

- (void)createUser:(CDVInvokedUrlCommand *)command {
   NSAssert(NO, @"This method should't be called");
}

- (void)associateRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;   
    
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:KEY_EMAIL];
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    __weak typeof(self) weakSelf = self;
    [self.serverManager associateRobot:robotId
                             withEmail:email
                            completion:^(NSDictionary *result, NSError *error) {
                                if (error) {
                                    debugLog(@"Failed to associate robot with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                    [weakSelf sendError:error forCallbackId:callbackId];
                                    return;
                                }
                                [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                            }];
}

- (void)associateRobot2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters received = %@", parameters);

    NSString *email = [parameters objectForKey:KEY_EMAIL];
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    __weak typeof(self) weakSelf = self;
    [self.serverManager associateRobot:robotId
                             withEmail:email
                            completion:^(NSDictionary *result, NSError *error) {
                                if (error) {
                                    debugLog(@"Failed to associate robot with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                    [weakSelf sendError:error forCallbackId:callbackId];
                                    return;
                                }
                                [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                            }];
}

- (void)debugGetConfigDetails:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSDictionary *appInfo = [AppHelper getAppDebugInfo];
    [self sendSuccessResultAsDictionary:appInfo forCallbackId:command.callbackId];
    debugLog(@"Done");
}

#pragma mark - Temp - New - Remove
- (void)login:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    NSString *password = [parameters valueForKey:KEY_PASSWORD];
    
    // Logout current user (if any) before calling login
    __weak typeof(self) weakSelf = self;
    if ([NeatoUserHelper getNeatoUser]) {
        [self.serverManager logoutUserEmail:[NeatoUserHelper getLoggedInUserEmail]
                                  authToken:[NeatoUserHelper getUsersAuthToken]
                                 completion:^(NSDictionary *result, NSError *error) {
                                     [weakSelf loginNativeUser:email
                                                  password:password
                                                completion:^(NSDictionary *result, NSError *error) {
                                                    if (error) {
                                                        debugLog(@"Login failed with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                        [weakSelf sendError:error forCallbackId:callbackId];
                                                        return;
                                                    }
                                                    [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                                                    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
                                                    
                                                }];
                                 }];
    }
    else {
        [self loginNativeUser:email
                     password:password
                   completion:^(NSDictionary *result, NSError *error) {
                       if (error) {
                           debugLog(@"Login failed with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                           [weakSelf sendError:error forCallbackId:callbackId];
                           return;
                       }
                       [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                       [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
                   }];
    }
    
    
}

- (void)createUser3:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NeatoUser *neatoUser = [[NeatoUser alloc] init];
    neatoUser.email = [parameters objectForKey:KEY_EMAIL];
    neatoUser.password = [parameters objectForKey:KEY_PASSWORD];
    neatoUser.name = [parameters objectForKey:KEY_USER_NAME];
    neatoUser.account_type = ACCOUNT_TYPE_NATIVE;
    neatoUser.alternateEmail = [parameters objectForKey:KEY_ALTERNATE_EMAIL];
    NSDictionary *extraParams = [parameters objectForKey:KEY_EXTRA_PARAM];
    neatoUser.userCountryCode = [extraParams objectForKey:@"country_code"];
    NSNumber *optIn = [extraParams objectForKey:@"opt_in"];
    neatoUser.optIn = optIn.boolValue;
  
    __weak typeof(self) weakSelf = self;
    // Logout current user (if any) before creating new one.
    if ([NeatoUserHelper getNeatoUser]) {
        [self.serverManager logoutUserEmail:[NeatoUserHelper getLoggedInUserEmail]
                                  authToken:[NeatoUserHelper getUsersAuthToken]
                                 completion:^(NSDictionary *result, NSError *error) {
                                     [weakSelf createNeatoUser:neatoUser
                                                    completion:^(NSDictionary *result, NSError *error) {
                                                        if (error) {
                                                            debugLog(@"Failed to create user with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                            [weakSelf sendError:error forCallbackId:callbackId];
                                                        }
                                                        [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                                                        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
                                                    }];
                                 }];
    }
    else {
        [self createNeatoUser:neatoUser
                   completion:^(NSDictionary *result, NSError *error) {
                       if (error) {
                           debugLog(@"Failed to create user with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                           [weakSelf sendError:error forCallbackId:callbackId];
                       }
                       [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                       [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
                   }];
    }
}

- (void)isLoggedIn:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    if ([NeatoUserHelper getNeatoUser]) {
        __weak typeof(self) weakSelf = self;
        // User is logged in, lets extend the auth key expiry
        [self.serverManager updateUserAuthToken:[NeatoUserHelper getUsersAuthToken]
                                     completion:^(NSDictionary *result, NSError *error) {
                                         if (error) {
                                             debugLog(@"Failed to update user auth token with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                             if (error.code == NSURLErrorNotConnectedToInternet) {
                                                 // Failed to update auth token - but it was network failure. Lets treat this as 'user logged-in'.
                                                 [weakSelf sendSuccessResultOKWithInt:[[NSNumber numberWithBool:YES] integerValue] forCallbackId:callbackId];
                                             }
                                             else {
                                                 // Failed to update auth token but it wasn't a network error. Treat this as 'user not logged-in'
                                                 // Logout and clear user data.
                                                 [weakSelf.serverManager logoutUserEmail:[NeatoUserHelper getLoggedInUserEmail]
                                                                               authToken:[NeatoUserHelper getUsersAuthToken]
                                                                              completion:^(NSDictionary *result, NSError *error) {
                                                                                  // User not logged-in.
                                                                                  [weakSelf sendSuccessResultOKWithInt:[[NSNumber numberWithBool:NO] integerValue] forCallbackId:callbackId];
                                                                              }];
                                             }
                                         }
                                         else {
                                             // Auth token extended.
                                             debugLog(@"Updated user auth token");
                                             // User logged-in.
                                             [weakSelf sendSuccessResultOKWithInt:[[NSNumber numberWithBool:YES] integerValue] forCallbackId:callbackId];
                                         }
                                     }];
    }
    else {
        // User not logged-in
        [self sendSuccessResultOKWithInt:[[NSNumber numberWithBool:NO] integerValue] forCallbackId:callbackId];
    }
}

- (void)forgetPassword:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager forgetPasswordForEmail:email
                                    completion:^(NSDictionary *result, NSError *error) {
                                        if (error) {
                                            debugLog(@"Forget password failed with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                            [weakSelf sendError:error forCallbackId:callbackId];
                                            return;
                                        }
                                        // Empty Dictionary object.
                                        NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
                                        [weakSelf sendSuccessResultAsDictionary:dictionary forCallbackId:callbackId];
                                    }];
}

- (void)changePassword:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *oldPassword = [parameters objectForKey:KEY_CURRENT_PASSWORD];
    NSString *newPassword = [parameters objectForKey:KEY_NEW_PASSWORD];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager changePasswordFromOldPassword:oldPassword
                                        toNewPassword:newPassword
                                           completion:^(NSDictionary *result, NSError *error) {
                                               if (error) {
                                                   debugLog(@"Failed to change password with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                   [weakSelf sendError:error forCallbackId:callbackId];
                                                   return;
                                               }
                                               // Empty Dictionary object.
                                               NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
                                               [weakSelf sendSuccessResultAsDictionary:dictionary forCallbackId:callbackId];
                                           }];
}

- (void)isUserValidated:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager isUserValidatedForEmail:email
                                     completion:^(NSDictionary *result, NSError *error) {
                                         if (error) {
                                             debugLog(@"Failed to validate user with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                             [weakSelf sendError:error forCallbackId:callbackId];
                                             return;
                                         }
                                         NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                         
                                         [data setValue:[result valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NEATO_RESPONSE_MESSAGE];
                                         [data setValue:[result valueForKey:NEATO_VALIDATION_STATUS] forKey:NEATO_VALIDATION_STATUS];
                                         
                                         [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                     }];
}

- (void)resendValidationMail:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager resendValidationEmail:email
                                   completion:^(NSDictionary *result, NSError *error) {
                                       if (error) {
                                           debugLog(@"Failed to send validation email with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                           [weakSelf sendError:error forCallbackId:callbackId];
                                           return;
                                       }
                                       NSString *message = [result valueForKey:NEATO_RESPONSE_MESSAGE];
                                       NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                       [data setValue:message forKey:KEY_MESSAGE];
                                       
                                       [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                   }];
}

- (void)getUserDetails:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    
    NSString *email = [parameters objectForKey:@"email"];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager getUserDetailsForEmail:email
                                     authToken:[NeatoUserHelper getUsersAuthToken]
                                    completion:^(NSDictionary *result, NSError *error) {
                                        if (error) {
                                            debugLog(@"Failed to get user details with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                            [weakSelf sendError:error forCallbackId:callbackId];
                                            return;
                                        }
                                        // Get user from dictionary
                                        NeatoUser *neatoUser = [[NeatoUser alloc] initWithDictionary:result];
                                        NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                        [data setValue:neatoUser.name forKey:KEY_USER_NAME];
                                        [data setValue:neatoUser.userId forKey:KEY_USER_ID];
                                        [data setValue:neatoUser.email forKey:KEY_EMAIL];
                                        
                                        NSMutableDictionary *extraParams = [[NSMutableDictionary alloc] init];
                                        [extraParams setObject:neatoUser.userCountryCode ? neatoUser.userCountryCode : @"" forKey:KEY_COUNTRY_CODE];
                                        [extraParams setObject:[AppHelper stringFromBool:neatoUser.optIn] forKey:KEY_OPT_IN];
                                        [data setValue:extraParams forKey:KEY_EXTRA_PARAM];
                                        
                                        [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                    }];
}

- (void)getNotificationSettings:(CDVInvokedUrlCommand *)command {
    // Get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *email = [parameters objectForKey:KEY_EMAIL];
    __weak typeof(self) weakSelf = self;
    [self.serverManager notificationSettingsForUserWithEmail:email
                                                  completion:^(NSDictionary *result, NSError *error) {
                                                      if (error) {
                                                          debugLog(@"Failed to get notification settings with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                          [weakSelf sendError:error forCallbackId:callbackId];
                                                          return;
                                                      }
                                                      [NeatoUserHelper setNotificationsFromNotificationsArray:[weakSelf arrayFromNotificationJson:result] forEmail:email];
                                                      [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                                                  }];
}

- (void)tryLinkingToRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *email = [parameters valueForKey:KEY_EMAIL];
    NSString *linkCode = [parameters valueForKey:KEY_LINK_CODE];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager linkEmail:email
                       toLinkCode:linkCode
                       completion:^(NSDictionary *result, NSError *error) {
                           if (error) {
                               debugLog(@"Robot linking failed with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                               [weakSelf sendError:error forCallbackId:callbackId];
                               return;
                           }
                           NSMutableDictionary *messageInfo = [[NSMutableDictionary alloc] init];
                           [messageInfo setValue:[result objectForKey:NEATO_RESPONSE_MESSAGE] forKey:KEY_MESSAGE];
                           [messageInfo setValue:[result objectForKey:NEATO_RESPONSE_SERIAL_NUMBER] forKey:KEY_ROBOT_ID];
                           [messageInfo setValue:[result objectForKey:NEATO_RESPONSE_SUCCESS] forKey:KEY_SUCCESS];
                           [weakSelf sendSuccessResultAsDictionary:messageInfo forCallbackId:callbackId];
                       }];
}

- (void)setUserAccountDetails:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
  
    NeatoUser *neatoUser = [[NeatoUser alloc] init];
    neatoUser.email = [parameters objectForKey:KEY_EMAIL];
    neatoUser.userCountryCode = [parameters objectForKey:@"country_code"] ? [parameters objectForKey:@"country_code"] : nil;
    NSNumber *optIn = [parameters objectForKey:@"opt_in"];
    neatoUser.optIn = optIn.boolValue;
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager setUserAccountDetails:neatoUser
                                    authToken:[NeatoUserHelper getUsersAuthToken]
                                   completion:^(NSDictionary *result, NSError *error) {
                                       if (error) {
                                           debugLog(@"Failed to set user account details with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                           [weakSelf sendError:error forCallbackId:callbackId];
                                           return;
                                       }
                                       // Get user from dictionary
                                       NeatoUser *user = [[NeatoUser alloc] initWithDictionary:result];
                                       NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                       [data setValue:user.name forKey:KEY_USER_NAME];
                                       [data setValue:user.userId forKey:KEY_USER_ID];
                                       [data setValue:user.email forKey:KEY_EMAIL];
                                       
                                       NSMutableDictionary *extraParams = [[NSMutableDictionary alloc] init];
                                       [extraParams setObject:user.userCountryCode ? user.userCountryCode : @"" forKey:KEY_COUNTRY_CODE];
                                       [extraParams setObject:[AppHelper stringFromBool:user.optIn] forKey:KEY_OPT_IN];
                                       [data setValue:extraParams forKey:KEY_EXTRA_PARAM];
                                       
                                       [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                   }];
}

- (void)disassociateRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:KEY_EMAIL];
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager dissociateRobotWithId:robotId
                            fromUserWithEmail:email
                                   completion:^(NSDictionary *result, NSError *error) {
                                       if (error) {
                                           debugLog(@"Failed to disassociate robot with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                           [weakSelf sendError:error forCallbackId:callbackId];
                                           return;
                                       }
                                       NSString *message = [result valueForKey:NEATO_RESPONSE_MESSAGE];
                                       [weakSelf sendSuccessResultAsString:message forCallbackId:callbackId];
                                   }];
    
}

- (void)disassociateAllRobots:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:@"email"];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager dissociateAllRobotsForUserWithEmail:email
                                                 completion:^(NSDictionary *result, NSError *error) {
                                                     if (error) {
                                                         debugLog(@"Failed to dissociate all robots with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                         [weakSelf sendError:error forCallbackId:callbackId];
                                                         return;
                                                     }
                                                     NSString *message = [result valueForKey:NEATO_RESPONSE_MESSAGE];
                                                     [weakSelf sendSuccessResultAsString:message forCallbackId:callbackId];
                                                 }];
}

- (void)logout:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    //get the callback id
    NSString *callbackId = command.callbackId;
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager logoutUserEmail:[NeatoUserHelper getLoggedInUserEmail]
                              authToken:[NeatoUserHelper getUsersAuthToken]
                             completion:^(NSDictionary *result, NSError *error) {
                                 [weakSelf sendSuccessResultAsString:@"User logged out." forCallbackId:callbackId];
                             }];
}

- (void)turnNotificationOnOff:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    // Get the callback id
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NeatoNotification *notification = [[NeatoNotification alloc] init];
    notification.notificationId = [parameters valueForKey:KEY_NOTFICATION_ID];
    notification.notificationValue = [AppHelper stringFromBool:[[parameters valueForKey:KEY_ON] boolValue]];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager turnNotification:notification
                   onOffForUserWithEmail:[parameters valueForKey:KEY_EMAIL]
                              completion:^(NSDictionary *result, NSError *error) {
                                  if (error) {
                                      debugLog(@"Failed to turn notification on/off with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                      [weakSelf sendError:error forCallbackId:callbackId];
                                      return;
                                  }
                                  [weakSelf sendSuccessResultAsDictionary:result forCallbackId:callbackId];
                              }];
}

- (void)getAssociatedRobots:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSString *email = [parameters objectForKey:@"email"];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager associatedRobotsForUserWithEmail:email
                                               authToken:[NeatoUserHelper getUsersAuthToken]
                                              completion:^(NSDictionary *result, NSError *error) {
                                                  if (error) {
                                                      debugLog(@"Failed to get associated robots with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                      [weakSelf sendError:error forCallbackId:callbackId];
                                                      return;
                                                  }
                                                  NSArray *robots = [result valueForKey:NEATO_RESPONSE_RESULT];
                                                  NSMutableArray *jsonArray = [[NSMutableArray alloc] init];
                                                  for (int i=0 ; i<[robots count] ; i++) {
                                                      NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                                      [data setValue:[[robots objectAtIndex:i] name] forKey:KEY_ROBOT_NAME];
                                                      [data setValue:[[robots objectAtIndex:i] serialNumber] forKey:KEY_ROBOT_ID];
                                                      [jsonArray addObject:data];
                                                  }
                                                  __weak typeof(self) weakSelf = self;
                                                  dispatch_async(dispatch_get_main_queue(), ^{
                                                      CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:jsonArray];
                                                      [weakSelf writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
                                                  });
                                                  
                                              }];
    
}

#pragma mark - Property Getter
- (NeatoServerManager *)serverManager {
    if (!_serverManager) {
        _serverManager = [[NeatoServerManager alloc] init];
    }
    return _serverManager;
}

#pragma mark - Private
- (void)sendError:(NSError *)error forCallbackId:(NSString *)callbackId {
    debugLog(@"Error description = %@, userInfo = %@", [error localizedDescription], [error userInfo]);
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
        [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
        [dictionary setValue:[NSNumber numberWithInt:error.code] forKey:KEY_ERROR_CODE];
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
        [weakSelf writeJavascript:[result toErrorCallbackString:callbackId]];
    });
}

- (void)sendSuccessResultAsString:(NSString *)resultString forCallbackId:(NSString *)callbackId {
    debugLog(@"");
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:resultString];
        [weakSelf writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    });
}

- (void)sendSuccessResultAsDictionary:(NSDictionary *)resultDictionary forCallbackId:(NSString *)callbackId {
    debugLog(@"");
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDictionary];
        [weakSelf writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    });
}

- (void)sendSuccessResultOKWithInt:(NSInteger *)resultInt forCallbackId:(NSString *)callbackId {
    debugLog(@"");
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:resultInt];
        [weakSelf writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    });
}

- (NSArray *)arrayFromNotificationJson:(NSDictionary *)notificationJson {
    debugLog(@"");
    NSArray *notificationJsonArray = [notificationJson objectForKey:KEY_NOTIFICATIONS];
    NSMutableArray *notificationsArray = [[NSMutableArray alloc] init];
    NeatoNotification *globalNotification = [[NeatoNotification alloc] init];
    globalNotification.notificationId = NOTIFICATION_ID_GLOBAL;
    globalNotification.notificationValue = [AppHelper stringFromBool:[[notificationJson valueForKey:NOTIFICATION_ID_GLOBAL] boolValue]];
    [notificationsArray addObject:globalNotification];
    for (int i = 0 ; i < TOTAL_NOTIFICATION_OPTIONS - 1 ; i++) {
        NeatoNotification *notification = [[NeatoNotification alloc] initWithDictionary:[notificationJsonArray objectAtIndex:i]];
        [notificationsArray addObject:notification];
    }
    return notificationsArray;
}

- (void)createUser2:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @"This method should't be called");
}

- (void)createNeatoUser:(NeatoUser *)neatoUser completion:(RequestCompletionBlockDictionary)completion {
    [self.serverManager createUser3:neatoUser
                         completion:^(NSDictionary *result, NSError *error) {
                             if (error) {
                                 completion ? completion(nil, error) : nil;
                                 return;
                             }
                             NSDictionary *responseResultDict = result;
                             NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                             // Get user from dictionary
                             NeatoUser *neatoUser = [[NeatoUser alloc] initWithDictionary:responseResultDict];
                             [pluginDataDict setValue:neatoUser.name forKey:KEY_USER_NAME];
                             [pluginDataDict setValue:neatoUser.userId forKey:KEY_USER_ID];
                             [pluginDataDict setValue:neatoUser.email forKey:KEY_EMAIL];
                             [pluginDataDict setValue:neatoUser.alternateEmail forKey:KEY_ALTERNATE_EMAIL];
                             [pluginDataDict setValue:[neatoUser userValidationStatus] forKey:NEATO_VALIDATION_STATUS];
                             if ([responseResultDict valueForKey:KEY_EXTRA_PARAM]) {
                                 [pluginDataDict setValue:[responseResultDict valueForKey:KEY_EXTRA_PARAM] forKey:KEY_EXTRA_PARAM];
                             }
                             completion ? completion(pluginDataDict, nil) : nil;
                         }];
}

- (void)loginNativeUser:(NSString *)email password:(NSString *)password completion:(RequestCompletionBlockDictionary)completion {
    [self.serverManager loginNativeUser:email
                               password:password
                             completion:^(NSDictionary *result, NSError *error) {
                                 if (error) {
                                     debugLog(@"Login failed with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                     completion ? completion(nil, error) : nil;
                                     return;
                                 }
                                 NSDictionary *responseResultDict = result;
                                 NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                                 NeatoUser *neatoUser = [[NeatoUser alloc] initWithDictionary:responseResultDict];
                                 [pluginDataDict setValue:neatoUser.name forKey:KEY_USER_NAME];
                                 [pluginDataDict setValue:neatoUser.userId forKey:KEY_USER_ID];
                                 [pluginDataDict setValue:neatoUser.email forKey:KEY_EMAIL];
                                 [pluginDataDict setValue:neatoUser.alternateEmail forKey:KEY_ALTERNATE_EMAIL];
                                 [pluginDataDict setValue:[neatoUser userValidationStatus] forKey:NEATO_VALIDATION_STATUS];
                                 
                                 // Params
                                 NSMutableDictionary *extraParams = [[NSMutableDictionary alloc] init];
                                 [extraParams setObject:neatoUser.userCountryCode ? neatoUser.userCountryCode : @"" forKey:KEY_COUNTRY_CODE];
                                 [extraParams setObject:[AppHelper stringFromBool:neatoUser.optIn] forKey:KEY_OPT_IN];
                                 [pluginDataDict setValue:extraParams forKey:KEY_EXTRA_PARAM];
                                 completion ? completion(pluginDataDict, nil) : nil;
                             }];
}

@end
