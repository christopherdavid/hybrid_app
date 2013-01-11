//
//  RobotManagerPlugin.h
//  SlideiOSPlugin
//

#import <Cordova/CDV.h>
#import "RobotManagerCallWrapper.h"

@interface RobotManagerPlugin : CDVPlugin <RobotManagerProtocol>

- (void) discoverNearByRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) sendCommandToRobot:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) tryDirectConnection:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) robotSetSchedule:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getSchedule:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getRobotMap:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) setMapOverlayData:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) disconnectDirectConnection:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@end
