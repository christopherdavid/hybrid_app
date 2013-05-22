//
//  UserManagerPlugin.h
//  SlideiOSPlugin
//

#import <Cordova/CDV.h>
#import "UserManagerCallWrapper.h"

@interface UserManagerPlugin : CDVPlugin <UserManagerProtocol>

- (void)login:(CDVInvokedUrlCommand *)command;
- (void)logout:(CDVInvokedUrlCommand *)command;
- (void)createUser:(CDVInvokedUrlCommand *)command;
- (void)isLoggedIn:(CDVInvokedUrlCommand *)command;
- (void)getUserDetails:(CDVInvokedUrlCommand *)command;
- (void)associateRobot:(CDVInvokedUrlCommand *)command;
- (void)associateRobot2:(CDVInvokedUrlCommand *)command;
- (void)getAssociatedRobots:(CDVInvokedUrlCommand *)command;
- (void)disassociateRobot:(CDVInvokedUrlCommand *)command;
- (void)disassociateAllRobots:(CDVInvokedUrlCommand *)command;
- (void)debugGetConfigDetails:(CDVInvokedUrlCommand *)command;
- (void)isUserValidated:(CDVInvokedUrlCommand *)command;
- (void)resendValidationMail:(CDVInvokedUrlCommand *)command;
- (void)forgetPassword:(CDVInvokedUrlCommand *)command;
- (void)changePassword:(CDVInvokedUrlCommand *)command;
- (void)createUser2:(CDVInvokedUrlCommand *)command;
@end
