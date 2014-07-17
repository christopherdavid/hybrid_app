//
//  RobotManagerPlugin.h
//  SlideiOSPlugin
//

#import <Cordova/CDV.h>
#import "RobotManagerCallWrapper.h"
#import "PushNotificationHelper.h"

@interface RobotManagerPlugin : CDVPlugin <PushNotificationDelegate>

// Not server calls
- (void)registerRobotNotifications2:(CDVInvokedUrlCommand *)command;
- (void)unregisterRobotNotifications2:(CDVInvokedUrlCommand *)command;
- (void)unregisterForRobotMessages:(CDVInvokedUrlCommand *)command;
- (void)tryDirectConnection:(CDVInvokedUrlCommand *)command;
- (void)tryDirectConnection2:(CDVInvokedUrlCommand *)command;
- (void)disconnectDirectConnection:(CDVInvokedUrlCommand *)command;
- (void)registerForRobotMessges:(CDVInvokedUrlCommand *)command;
- (void)setSpotDefinition:(CDVInvokedUrlCommand *)command;
- (void)getSpotDefinition:(CDVInvokedUrlCommand *)command;
- (void)isRobotPeerConnected:(CDVInvokedUrlCommand *)command;
- (void)driveRobot:(CDVInvokedUrlCommand *)command;
- (void)stopRobotDrive:(CDVInvokedUrlCommand *)command;
- (void)createSchedule:(CDVInvokedUrlCommand *)command;
- (void)addScheduleEventData:(CDVInvokedUrlCommand *)command;
- (void)updateScheduleEvent:(CDVInvokedUrlCommand *)command;
- (void)deleteScheduleEvent:(CDVInvokedUrlCommand *)command;
- (void)getScheduleEventData:(CDVInvokedUrlCommand *)command;
- (void)getScheduleData:(CDVInvokedUrlCommand *)command;
- (void)sendCommandToRobot2:(CDVInvokedUrlCommand *)command;
- (void)startCleaning:(CDVInvokedUrlCommand *)command;
- (void)stopCleaning:(CDVInvokedUrlCommand *)command;
- (void)pauseCleaning:(CDVInvokedUrlCommand *)command;
- (void)resumeCleaning:(CDVInvokedUrlCommand *)command;
- (void)turnWiFiOnOff:(CDVInvokedUrlCommand *)command;
- (void)turnMotorOnOff2:(CDVInvokedUrlCommand *)command;

// Block based APIs
- (void)getRobotCleaningCategory:(CDVInvokedUrlCommand *)command;
- (void)getRobotCurrentStateDetails:(CDVInvokedUrlCommand *)command;
- (void)getRobotCurrentState:(CDVInvokedUrlCommand *)command;
- (void)getRobotData:(CDVInvokedUrlCommand *)command;
- (void)directConnectToRobot:(CDVInvokedUrlCommand *)command;
- (void)clearRobotData:(CDVInvokedUrlCommand *)command;
- (void)getRobotDetail:(CDVInvokedUrlCommand *)command;
- (void)getScheduleEvents:(CDVInvokedUrlCommand *)command;
- (void)getRobotOnlineStatus:(CDVInvokedUrlCommand *)command;
- (void)isScheduleEnabled:(CDVInvokedUrlCommand *)command;
- (void)enableSchedule:(CDVInvokedUrlCommand *)command;

- (void)updateSchedule:(CDVInvokedUrlCommand *)command;

// Deprecated
- (void)robotSetSchedule2:(CDVInvokedUrlCommand *)command;
- (void)getSchedule2:(CDVInvokedUrlCommand *)command;
- (void)deleteScheduleData:(CDVInvokedUrlCommand *)command;
- (void)robotSetSchedule:(CDVInvokedUrlCommand *)command;
- (void)getSchedule:(CDVInvokedUrlCommand *)command;
- (void)getRobotMap:(CDVInvokedUrlCommand *)command;
- (void)setMapOverlayData:(CDVInvokedUrlCommand *)command;
- (void)turnMotorOnOff:(CDVInvokedUrlCommand *)command;
- (void)setRobotName:(CDVInvokedUrlCommand *)command;
- (void)intendToDrive:(CDVInvokedUrlCommand *)command;
- (void)cancelIntendToDrive:(CDVInvokedUrlCommand *)command;
- (void)getRobotVirtualOnlineStatus:(CDVInvokedUrlCommand *)command;
- (void)discoverNearByRobots:(CDVInvokedUrlCommand *)command;
- (void)sendCommandToRobot:(CDVInvokedUrlCommand *)command;
- (void)getRobotCleaningState:(CDVInvokedUrlCommand *)command;
@end
