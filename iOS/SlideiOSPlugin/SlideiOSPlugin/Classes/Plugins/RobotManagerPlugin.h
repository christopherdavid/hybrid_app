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
- (void)createSchedule:(CDVInvokedUrlCommand *)command;
- (void)addScheduleEventData:(CDVInvokedUrlCommand *)command;
- (void)updateScheduleEvent:(CDVInvokedUrlCommand *)command;
- (void)deleteScheduleEvent:(CDVInvokedUrlCommand *)command;
- (void)getScheduleEventData:(CDVInvokedUrlCommand *)command;
- (void)getScheduleData:(CDVInvokedUrlCommand *)command;
- (void)getScheduleEvents:(CDVInvokedUrlCommand *)command;
- (void)updateSchedule:(CDVInvokedUrlCommand *)command;
- (void)robotSetSchedule2:(CDVInvokedUrlCommand *)command;
- (void)getSchedule2:(CDVInvokedUrlCommand *)command;
- (void)deleteScheduleData:(CDVInvokedUrlCommand *)command;
- (void)registerForRobotMessages:(CDVInvokedUrlCommand *)command;
- (void)unregisterForRobotMessages:(CDVInvokedUrlCommand *)command;
- (void)enableSchedule:(CDVInvokedUrlCommand *)command;
- (void)startCleaning:(CDVInvokedUrlCommand *)command;
- (void)stopCleaning:(CDVInvokedUrlCommand *)command;
- (void)pauseCleaning:(CDVInvokedUrlCommand *)command;
- (void)resumeCleaning:(CDVInvokedUrlCommand *)command;
- (void)turnWiFiOnOff:(CDVInvokedUrlCommand *)command;
- (void)turnVacuumOnOff:(CDVInvokedUrlCommand *)command;
@end
