//
//  RobotManagerPlugin.h
//  SlideiOSPlugin
//

#import <Cordova/CDV.h>
#import "RobotManagerCallWrapper.h"

@interface RobotManagerPlugin : CDVPlugin 

- (void)discoverNearByRobots:(CDVInvokedUrlCommand *)command;
- (void)sendCommandToRobot:(CDVInvokedUrlCommand *)command;
- (void)tryDirectConnection:(CDVInvokedUrlCommand *)command;
- (void)robotSetSchedule:(CDVInvokedUrlCommand *)command;
- (void)getSchedule:(CDVInvokedUrlCommand *)command;
- (void)getRobotMap:(CDVInvokedUrlCommand *)command;
- (void)setMapOverlayData:(CDVInvokedUrlCommand *)command;
- (void)disconnectDirectConnection:(CDVInvokedUrlCommand *)command;
- (void)getRobotAtlasMetadata:(CDVInvokedUrlCommand *)command;
- (void)getAtlasGridData:(CDVInvokedUrlCommand *)command;
- (void)updateRobotAtlasMetadata:(CDVInvokedUrlCommand *)command;
- (void)getRobotDetail:(CDVInvokedUrlCommand *)command;
- (void)setRobotName2:(CDVInvokedUrlCommand *)command;
- (void)getRobotOnlineStatus:(CDVInvokedUrlCommand *)command;
- (void)tryDirectConnection2:(CDVInvokedUrlCommand *)command;
- (void)sendCommandToRobot2:(CDVInvokedUrlCommand *)command;
@end
