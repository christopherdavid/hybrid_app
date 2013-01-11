//
//  UserManagerPlugin.h
//  SlideiOSPlugin
//

#import <Cordova/CDV.h>
#import "UserManagerCallWrapper.h"

@interface UserManagerPlugin : CDVPlugin <UserManagerProtocol>

- (void) login:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) logout:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) createUser:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) isLoggedIn:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getUserDetails:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) associateRobot:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getAssociatedRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) disassociateRobot:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) disassociateAllRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@end
