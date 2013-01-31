//
//  UserManagerPlugin.h
//  SlideiOSPlugin
//

#import <Cordova/CDV.h>
#import "UserManagerCallWrapper.h"

@interface UserManagerPlugin : CDVPlugin <UserManagerProtocol>

- (void) login:(CDVInvokedUrlCommand *)command;
- (void) logout:(CDVInvokedUrlCommand *)command;
- (void) createUser:(CDVInvokedUrlCommand *)command;
- (void) isLoggedIn:(CDVInvokedUrlCommand *)command;
- (void) getUserDetails:(CDVInvokedUrlCommand *)command;
- (void) associateRobot:(CDVInvokedUrlCommand *)command;
- (void) getAssociatedRobots:(CDVInvokedUrlCommand *)command;
- (void) disassociateRobot:(CDVInvokedUrlCommand *)command;
- (void) disassociateAllRobots:(CDVInvokedUrlCommand *)command;
- (void) debugGetConfigDetails:(CDVInvokedUrlCommand *)command;

@end
