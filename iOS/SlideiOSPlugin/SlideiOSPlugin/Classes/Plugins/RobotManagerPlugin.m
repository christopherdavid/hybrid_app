//
//  RobotManagerPlugin.m
//  SlideiOSPlugin
//

#import "RobotManagerPlugin.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "RobotManagerCallWrapper.h"
#import "PluginConstants.h"
#import "NSDictionary+StringValueForKey.h"
#import "ScheduleEvent.h"
#import "Schedule.h"
#import "ScheduleUtils.h"
#import "XMPPRobotDataChangeManager.h"
#import "XMPPRobotCleaningStateHelper.h"

// PluginResult Classes
#import "CreateSchedulePluginResult.h"
#import "CreateScheduleEventPluginResult.h"
#import "GetScheduleEventDataPluginResult.h"
#import "GetScheduleDataPluginResult.h"
#import "NeatoUserHelper.h"
#import "CleaningArea.h"
#import "NeatoRobotHelper.h"
#import "NeatoErrorCodes.h"

// Server Manager
#import "NeatoServerManager.h"
#import "RobotScheduleManager.h"

// Helper class
#import "TCPConnectionHelper.h"
#import "RobotDriveManager.h"

@interface RobotManagerPlugin ()
@property (nonatomic, strong) NeatoServerManager *serverManager;
@property (nonatomic, strong) RobotScheduleManager *scheduleManager;
@end

@implementation RobotManagerPlugin

- (void) discoverNearByRobots:(CDVInvokedUrlCommand *)command {
  NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void) sendCommandToRobot:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void) tryDirectConnection:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void) robotSetSchedule:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
}


- (void) getSchedule:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
}
- (void) getRobotMap:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
}

- (void) setMapOverlayData:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
}

- (void)failedToSendCommandOverXMPP:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_FAILURE];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) disconnectDirectConnection:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)connectedOverTCP:(NSString *)host callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:TCP_CONNECTION_STATUS_CONNECTED];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


- (void)tcpConnectionDisconnected:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:TCP_CONNECTION_STATUS_NOT_CONNECTED];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)failedToSendCommandOverTCP:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_FAILURE];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)commandSentOverTCP:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_SUCCESS];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)didConnectOverXMPP :(NSString *)callbackId {
    debugLog(@"");
}

- (void)didDisConnectFromXMPP:(NSString *)callbackId {
    debugLog(@"");
}

- (void)commandSentOverXMPP:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_SUCCESS];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *)sender callbackId:(NSString *)callbackId {
    debugLog(@"");
}

- (void)receivedDataOverTCP:(NSData *)data callbackId:(NSString *)callbackId {
    debugLog(@"");
}

- (void)tryDirectConnection2:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)failedToConnectToTCP2WithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Error = %@", error);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)connectedOverTCP2:(NSString*)host callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)sendCommandToRobot2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *commandId = [parameters objectForKey:KEY_COMMAND_ID];
    if (![commandId isKindOfClass:[NSString class]]) {
        commandId = [NSString stringWithFormat:@"%@", commandId];
    }
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSDictionary *commandParams = [parameters objectForKey:KEY_COMMAND_PARAMETERS];
    NSMutableDictionary *params = [commandParams objectForKey:KEY_PARAMS];
    debugLog(@"params = %@",params);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot2:robotId commandId:commandId params:params callbackId:callbackId];
}

- (void)commandSentOverTCP2:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_SUCCESS];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)commandSentOverXMPP2WithResult:(NSDictionary *)resultDict callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendSuccessResultAsDictionary:resultDict forCallbackId:callbackId];
}

- (void)failedToSendCommandOverTCPWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)failedToSendCommandOverXMPP2:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_FAILURE];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)createSchedule:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    id pluginResult =  [scheduleManager createScheduleForRobotId:robotId forScheduleType:scheduleType];
    if([pluginResult isKindOfClass:[NSError class]]) {
        //Error callback.
        NSError *error = (NSError *)pluginResult;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        //Success callback.
        CreateSchedulePluginResult *successResult = (CreateSchedulePluginResult *)pluginResult;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:successResult.toDictionary];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)addScheduleEventData:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *scheduleId = [parameters objectForKey:KEY_SCHEDULE_ID];
    NSDictionary *scheduleEventData = [parameters objectForKey:KEY_SCHEDULE_EVENT_DATA];
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    id pluginResult =  [scheduleManager addScheduleEventData:scheduleEventData forScheduleWithScheduleId:scheduleId];
    if([pluginResult isKindOfClass:[NSError class]]) {
        NSError *error = (NSError *)pluginResult;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        CreateScheduleEventPluginResult *successResult = (CreateScheduleEventPluginResult *)pluginResult;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:successResult.toDictionary];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)updateScheduleEvent:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *scheduleId = [parameters objectForKey:KEY_SCHEDULE_ID];
    NSString *scheduleEventId = [parameters objectForKey:KEY_SCHEDULE_EVENT_ID];
    NSDictionary *scheduleEventData = [parameters objectForKey:KEY_SCHEDULE_EVENT_DATA];
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
     id pluginResult =  [scheduleManager updateScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId withScheduleEventdata:scheduleEventData];
    if([pluginResult isKindOfClass:[NSError class]]) {
        NSError *error = (NSError *)pluginResult;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        CreateScheduleEventPluginResult *successResult = (CreateScheduleEventPluginResult *)pluginResult;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:successResult.toDictionary];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)deleteScheduleEvent:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *scheduleId = [parameters objectForKey:KEY_SCHEDULE_ID];
    NSString *scheduleEventId = [parameters objectForKey:KEY_SCHEDULE_EVENT_ID];
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    id pluginResult = [scheduleManager deleteScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
    if([pluginResult isKindOfClass:[NSError class]]) {
        NSError *error = (NSError *)pluginResult;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        CreateScheduleEventPluginResult *successResult = (CreateScheduleEventPluginResult *)pluginResult;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:successResult.toDictionary];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)getScheduleEventData:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *scheduleId = [parameters objectForKey:KEY_SCHEDULE_ID];
    NSString *scheduleEventId = [parameters objectForKey:KEY_SCHEDULE_EVENT_ID];
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    id pluginResult = [scheduleManager scheduleEventDataWithScheduleEventId:scheduleEventId withScheduleId:scheduleId];
    if([pluginResult isKindOfClass:[NSError class]]) {
        NSError *error = (NSError *)pluginResult;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        GetScheduleEventDataPluginResult *successResult = (GetScheduleEventDataPluginResult *)pluginResult;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:successResult.toDictionary];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)getScheduleData:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *scheduleId = [parameters objectForKey:KEY_SCHEDULE_ID];
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    id pluginResult = [scheduleManager scheduleDataForScheduleId:scheduleId];
    if([pluginResult isKindOfClass:[NSError class]]) {
        NSError *error = (NSError *)pluginResult;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        GetScheduleDataPluginResult *successResult = (GetScheduleDataPluginResult *)pluginResult;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:successResult.toDictionary];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)failedToGetScheduleEventsWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
}

- (void)gotScheduleEventsForSchedule:(Schedule *)schedule ofType:(NSInteger)scheduleType forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *resultDictionary = [[NSMutableDictionary alloc] init];
    [resultDictionary setValue:schedule.scheduleId forKey:KEY_SCHEDULE_ID];
    [resultDictionary setValue:robotId forKey:KEY_ROBOT_ID];
    [resultDictionary setValue:[NSNumber numberWithInteger:scheduleType] forKey:KEY_SCHEDULE_TYPE];
    [resultDictionary setValue:[schedule arrayOfScheduleEventIdsForType:scheduleType] forKey:KEY_SCHEDULE_EVENTS_LIST];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDictionary];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)robotSetSchedule2:(CDVInvokedUrlCommand *)command {
   NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)getSchedule2:(CDVInvokedUrlCommand *)command {
   NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)deleteScheduleData:(CDVInvokedUrlCommand *)command {
   NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)updateSchedule:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *scheduleId = [parameters objectForKey:KEY_SCHEDULE_ID];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call updateScheduleForScheduleId:scheduleId callbackId:callbackId];
}

- (void)updatedSchedule:(NSString *)scheduleId callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *jsonObject = [[NSMutableDictionary alloc] init];
    [jsonObject setObject:[AppHelper getEmptyStringIfNil:scheduleId] forKey:KEY_SCHEDULE_ID];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonObject];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)updateScheduleError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)failedToEnableDisableScheduleWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:[[NSNumber numberWithInteger:error.code] stringValue] forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)enabledDisabledScheduleWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId {
    debugLog(@"resultData = %@", resultData);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)startCleaning:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *commandId = [NSString stringWithFormat:@"%d", COMMAND_START_ROBOT];
    
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSDictionary *commandParams = [parameters objectForKey:KEY_COMMAND_PARAMETERS];
    NSMutableDictionary *params = [commandParams objectForKey:KEY_PARAMS];
    debugLog(@"params = %@", params);
  
    // If cleaning category is MANUAL,
    // Send command over TCP if TCP connection exists
    // Else send error if not connected over TCP.
    NSNumber *cleaningCategory = [params valueForKey:KEY_CLEANING_CATEGORY];
    if ([cleaningCategory integerValue] == CLEANING_CATEGORY_MANUAL) {
        TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
        if ([helper isRobotConnectedOverTCP:robotId]) {
            RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
            callWrapper.delegate = self;
            [callWrapper sendCommandOverTCPToRobotWithId:robotId commandId:commandId params:params callbackId:callbackId];
            return;
        }
        else {
            debugLog(@"Manual cleaning cannot be started as direct connection does not exist");
            NSError *error = [AppHelper nserrorWithDescription:@"Robot is not connected" code:UI_ROBOT_NOT_CONNECTED];
            [self sendError:error forCallbackId:callbackId];
            return;
        }
    }
    
    // If its not a MANUAL category,
    // then only send command over server.
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot2:robotId commandId:commandId params:params callbackId:callbackId];
}

- (void)stopCleaning:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *commandId = [NSString stringWithFormat:@"%d", COMMAND_STOP_ROBOT];
    
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSDictionary *commandParams = [parameters objectForKey:KEY_COMMAND_PARAMETERS];
    NSMutableDictionary *params = [commandParams objectForKey:KEY_PARAMS];
    debugLog(@"params = %@",params);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot2:robotId commandId:commandId params:params callbackId:callbackId];
}

- (void)pauseCleaning:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *commandId = [NSString stringWithFormat:@"%d", COMMAND_PAUSE_CLEANING];
    
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSDictionary *commandParams = [parameters objectForKey:KEY_COMMAND_PARAMETERS];
    NSMutableDictionary *params = [commandParams objectForKey:KEY_PARAMS];
    debugLog(@"params = %@",params);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot2:robotId commandId:commandId params:params callbackId:callbackId];
}

- (void)resumeCleaning:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *commandId = [NSString stringWithFormat:@"%d", COMMAND_RESUME_CLEANING];
    
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSDictionary *commandParams = [parameters objectForKey:KEY_COMMAND_PARAMETERS];
    NSMutableDictionary *params = [commandParams objectForKey:KEY_PARAMS];
    debugLog(@"params = %@",params);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot2:robotId commandId:commandId params:params callbackId:callbackId];
}

- (void)turnWiFiOnOff:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)sendError:(NSError *)error forCallbackId:(NSString *)callbackId {
    debugLog(@"Error description = %@, userInfo = %@", [error localizedDescription], [error userInfo]);
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
      NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
      [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
      [dictionary setValue:[NSNumber numberWithInteger:error.code] forKey:KEY_ERROR_CODE];
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

- (void)setSpotDefinition:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSInteger cleaningAreaLength = [[parameters objectForKey:KEY_SPOT_CLEANING_AREA_LENGTH] integerValue];
    NSInteger cleaningAreaHeight = [[parameters objectForKey:KEY_SPOT_CLEANING_AREA_HEIGHT] integerValue];
    
    id result = [NeatoRobotHelper setSpotDefinitionForRobotWithId:robotId cleaningAreaLength:cleaningAreaLength cleaningAreaHeight:cleaningAreaHeight];
    if ([result isKindOfClass:[NSError class]]) {
        // Error callback.
        NSError *error = (NSError *)result;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
}

- (void)getSpotDefinition:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    id result = [NeatoRobotHelper spotDefinitionForRobotWithId:robotId];
    if ([result isKindOfClass:[NSError class]]) {
        // Error callback.
        NSError *error = (NSError *)result;
        [self sendError:error forCallbackId:callbackId];
    }
    else {
        NSMutableDictionary *resultData = [[NSMutableDictionary alloc] init];
        CleaningArea *cleaningArea = (CleaningArea *)result;
        if(cleaningArea.height || cleaningArea.length) {
            [resultData setValue:[NSNumber numberWithInteger:cleaningArea.length] forKey:KEY_SPOT_CLEANING_AREA_LENGTH];
            [resultData setValue:[NSNumber numberWithInteger:cleaningArea.height] forKey:KEY_SPOT_CLEANING_AREA_HEIGHT];
        } else {
            [resultData setValue:[NSNumber numberWithInt:DEFAULT_SPOT_CLEANING_LENGTH] forKey:KEY_SPOT_CLEANING_AREA_LENGTH];
            [resultData setValue:[NSNumber numberWithInt:DEFAULT_SPOT_CLEANING_HEIGHT] forKey:KEY_SPOT_CLEANING_AREA_HEIGHT];
        }
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)registerForRobotMessges:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    debugLog(@"callbackId = %@", callbackId);
    [PushNotificationHelper sharedInstance].pushNotificationDelegate = self;
    [[PushNotificationHelper sharedInstance] registerForPushNotificationsForCallbackId:callbackId];
}

- (void)unregisterForRobotMessages:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    [PushNotificationHelper sharedInstance].pushNotificationDelegate = nil;;
    [[PushNotificationHelper sharedInstance] unregisterForPushNotification];
}

- (void)didReceivePushNotification:(NSString *)callbackId withNotification:(NSDictionary*)notification {
    debugLog(@"");
    debugLog(@"callback id = %@", callbackId);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:notification];
    [result setKeepCallbackAsBool:YES];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


- (void)getRobotVirtualOnlineStatus:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)dealloc {
    debugLog(@"");
}

- (void)setRobotName:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSError *error = [AppHelper nserrorWithDescription:@" API 'setRobotName' is deprecated, use 'setRobotName2' instead." code:UI_ERROR_NOT_SUPPORTED];
    [self sendError:error forCallbackId:command.callbackId];
}

- (void)registerRobotNotifications2:(CDVInvokedUrlCommand *)command {
    // Start listening for robot data change xmpp notification.
    XMPPRobotDataChangeManager *dataChangeManager = [XMPPRobotDataChangeManager sharedXmppDataChangeManager];
    [dataChangeManager startListeningRobotDataChangeNotificationsFor:self];
    NSString *callbackId = command.callbackId;
    [NeatoRobotHelper saveXMPPCallbackId:callbackId];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [result setKeepCallbackAsBool:YES];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)unregisterRobotNotifications2:(CDVInvokedUrlCommand *)command {
    // Stop listening for robot data change xmpp notification.
    XMPPRobotDataChangeManager *dataChangeManager = [XMPPRobotDataChangeManager sharedXmppDataChangeManager];
    [dataChangeManager stopListeningRobotDataChangeNotificationsFor:self];
    [NeatoRobotHelper removeXMPPCallbackId];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [result setKeepCallbackAsBool:NO];
    [self writeJavascript:[result toSuccessCallbackString:command.callbackId]];
}

- (void)updateUIForRobotDataChangeNotification:(NSNotification *)notification {
    debugLog(@"");
    NSNumber *successCallback = [notification.userInfo objectForKey:SUCCESS_CALLBACK];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([successCallback boolValue]) {
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[notification.userInfo objectForKey:KEY_UI_UPDATE_DATA]];
            [result setKeepCallbackAsBool:YES];
            [self writeJavascript:[result toSuccessCallbackString:[notification.userInfo objectForKey:KEY_CALLBACK_ID]]];
        }
        else {
            // TODO: Do we need to send error back?
        }
    });
};

- (void)failedtoSendCommandWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Failed to send command with error  = %@", error);
    [self sendError:error forCallbackId:callbackId];
}

- (void)commandSentWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId {
    debugLog(@"commandSentWithResult = %@", resultData);
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:[resultData valueForKeyPath:NEATO_RESPONSE_EXPECTED_TIME] forKey:NEATO_RESPONSE_EXPECTED_TIME];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)getRobotCleaningState:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)intendToDrive:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)driveRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *navigationControlId = [parameters objectForKey:KEY_NAVIGATION_CONTROL_ID];
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper driveRobotWithId:robotId navigationControlId:navigationControlId callbackId:callbackId];
}

- (void)driveRobotFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
}

- (void)cancelIntendToDrive:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)stopRobotDrive:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper stopRobotDriveForRobotWithId:robotId callbackId:callbackId];
}

- (void)stopRobotDriveSuccededForCallbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)stopRobotDriveFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
}

- (void)isRobotPeerConnected:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotDriveManager *driveManager = [[RobotDriveManager alloc] init];
    id data = [driveManager isConnectedOverTCPWithRobotId:robotId];
    if ([data isKindOfClass:[NSMutableDictionary class]]) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:(NSDictionary *)data];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)turnMotorOnOff2:(CDVInvokedUrlCommand *)command {
    NSAssert(NO, @" %s this method should't be called",__PRETTY_FUNCTION__);
}

- (void)turnMotorOnOff:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSError *error = [AppHelper nserrorWithDescription:@" API 'turnMotorOnOff' is deprecated, use 'turnMotorOnOff2' instead." code:UI_ERROR_NOT_SUPPORTED];
    [self sendError:error forCallbackId:command.callbackId];
}

- (void)getRobotCleaningCategory:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    // Save robotId of robot, selected by the user(JS layer).
    [AppHelper saveLastUsedRobotId:robotId];
    __weak typeof(self) weakSelf = self;
    NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
    [serverManager cleaningCategoryForRobot:robotId
                                 completion:^(NSDictionary *result, NSError *error) {
                                     dispatch_async(dispatch_get_main_queue(), ^{
                                         if (error) {
                                             [weakSelf sendError:error forCallbackId:command.callbackId];
                                             return;
                                         }
                                         
                                         NSNumber *cleaningCategory = [result objectForKey:NEATO_RESPONSE_CLEANING_CATEGORY];
                                         if (result && cleaningCategory) {
                                             NSMutableDictionary *resultData = [[NSMutableDictionary alloc] init];
                                             [resultData setObject:cleaningCategory forKey:KEY_CLEANING_CATEGORY];
                                             [resultData setObject:robotId forKey:KEY_ROBOT_ID];
                                             [weakSelf sendSuccessResultAsDictionary:resultData forCallbackId:callbackId];
                                         } else {
                                             // If the response does not contain cleaning category, send error to UI.
                                             NSError *uiError = [AppHelper nserrorWithDescription:@"No Current Cleaning State set by the robot"
                                                                                             code:UI_ERROR_TYPE_NO_CLEANING_STATE_SET];
                                             [weakSelf sendError:uiError forCallbackId:command.callbackId];
                                         }
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

- (RobotScheduleManager *)scheduleManager {
  if (!_scheduleManager) {
    _scheduleManager = [[RobotScheduleManager alloc] init];
  }
  return _scheduleManager;
}

- (void)getRobotCurrentStateDetails:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@", parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];

  // Save robotId of robot, selected by the user(JS layer).
  [AppHelper saveLastUsedRobotId:robotId];
  __weak typeof(self) weakSelf = self;
  [self.serverManager profileDetailsForRobot:robotId
                                  completion:^(NSDictionary *result, NSError *error) {
                                      if (error) {
                                          debugLog(@"Failed to get robot current state details with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                          [weakSelf sendError:error forCallbackId:callbackId];
                                          return;
                                      }
                                      NSDictionary *profileDetails = result;
                                      // Update the timestamp in DB.
                                      XMPPRobotDataChangeManager *xmppDataChangeManager = [XMPPRobotDataChangeManager sharedXmppDataChangeManager];
                                      [xmppDataChangeManager updateDataTimestampIfChangedForKey:KEY_ROBOT_CURRENT_STATE_DETAILS withProfile:profileDetails];

                                      // Send the data back to UI layer.
                                      NSString *robotCurrentStateDetailsJsonString = [[profileDetails valueForKey:KEY_ROBOT_CURRENT_STATE_DETAILS] valueForKey:KEY_VALUE];
                                      NSData *jsonData = [robotCurrentStateDetailsJsonString dataUsingEncoding:NSUTF8StringEncoding];
                                      NSDictionary *robotCurrentStateDetailsDict = [AppHelper parseJSON:jsonData];
                                      
                                      // If there's no current state details set on server, send empty dict to UI.
                                      if (!robotCurrentStateDetailsDict) {
                                          robotCurrentStateDetailsDict = [[NSDictionary alloc] init];
                                      }
                                      NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                      [data setValue:robotCurrentStateDetailsDict forKey:NEATO_RESPONSE_CURRENT_STATE_DETAILS];
                                      [data setValue:robotId forKey:KEY_ROBOT_ID];
                                      
                                      [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                  }];
}

- (void)getRobotCurrentState:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager profileDetailsForRobot:robotId
                                    completion:^(NSDictionary *result, NSError *error) {
                                        if (error) {
                                            debugLog(@"Failed to get robot current state with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                            [weakSelf sendError:error forCallbackId:callbackId];
                                            return;
                                        }
                                        NSDictionary *profileDetails = result;
                                        // Update the timestamp in DB.
                                        XMPPRobotDataChangeManager *xmppDataChangeManager = [XMPPRobotDataChangeManager sharedXmppDataChangeManager];
                                        [xmppDataChangeManager updateDataTimestampIfChangedForKey:KEY_ROBOT_CURRENT_STATE withProfile:profileDetails];
                                        
                                        // Send the data back to UI layer.
                                        NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                        NSInteger robotCurrentState = [XMPPRobotCleaningStateHelper robotCurrentStateFromRobotProfile:profileDetails];
                                        NSInteger robotActualState = [XMPPRobotCleaningStateHelper robotActualStateFromRobotProfile:profileDetails];
                                        [data setValue:[NSNumber numberWithInteger:robotActualState] forKey:KEY_ROBOT_NEW_VIRTUAL_STATE];
                                        [data setValue:[NSNumber numberWithInteger:robotCurrentState] forKey:KEY_ROBOT_CURRENT_STATE];
                                        [data setValue:robotId forKey:KEY_ROBOT_ID];
                                        
                                        [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                    }];
  
}

- (void)getRobotData:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@", parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  __block NSArray *profileKeys = [parameters objectForKey:KEY_ROBOT_PROFILE_KEYS];

  // Save robotId of robot, selected by the user(JS layer).
  [AppHelper saveLastUsedRobotId:robotId];
  __weak typeof(self) weakSelf = self;
  [self.serverManager profileDetailsForRobot:robotId
                                  completion:^(NSDictionary *result, NSError *error) {
                                    if (error) {
                                      debugLog(@"Failed to get robot profile details with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                      [weakSelf sendError:error forCallbackId:callbackId];
                                      return;
                                    }
                                    NSDictionary *profileDetails = result;
                                    
                                    // If profileKeys have some keys, send data for only those keys,
                                    // Else send data for all keys of 'robot profile'.
                                    if (!profileKeys || (profileKeys.count == 0)) {
                                        profileKeys = [AppHelper removeInternalKeysFromRobotProfileKeys:profileDetails.allKeys];
                                    }
                                    
                                    NSMutableDictionary *robotDataDict = [[NSMutableDictionary alloc] init];
                                    XMPPRobotDataChangeManager *xmppDataChangeManager = [XMPPRobotDataChangeManager sharedXmppDataChangeManager];

                                    for (NSString *key in profileKeys) {
                                      id valueForKey = [[profileDetails valueForKey:key] valueForKey:KEY_VALUE];
                                      if (valueForKey) {
                                        // Update the timestamp in DB.
                                        [xmppDataChangeManager updateDataTimestampIfChangedForKey:key withProfile:profileDetails];
                                        
                                        // As the value could be JSON string or any other value,
                                        // So parse to JSON object(Dict) if it is valid JSON string,
                                        // Else use the original un-parsed value.
                                        if ([AppHelper isValidJSONString:valueForKey]) {
                                          NSString *jsonString = (NSString *)valueForKey;
                                          NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
                                          [robotDataDict setValue:[AppHelper parseJSON:jsonData] forKey:key];
                                        }
                                        else {
                                          [robotDataDict setValue:valueForKey forKey:key];
                                        }
                                      }
                                    }

                                    // Send the data back to UI layer.
                                    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                    [data setValue:robotDataDict forKey:KEY_ROBOT_PROFILE_DATA];
                                    [data setValue:robotId forKey:KEY_ROBOT_ID];
                                    [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                  }];
}

- (void)directConnectToRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    // Send error if robot is already connected to same robot or some other robot.
    id canRequestDirectConnection = [RobotDriveManager canRequestDirectConnectionWithRobotId:robotId];
    if ([canRequestDirectConnection isKindOfClass:[NSError class]]) {
        NSError *connectionError = (NSError *)canRequestDirectConnection;
        [self sendError:connectionError forCallbackId:callbackId];
        return;
    }
    
    __weak typeof(self) weakSelf = self;
    [self.serverManager profileDetailsForRobot:robotId
                                    completion:^(NSDictionary *result, NSError *error) {
                                        if (error) {
                                            debugLog(@"Failed to make direct connection with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                            [weakSelf sendError:error forCallbackId:callbackId];
                                            return;
                                        }
                                        NSDictionary *profileDetails = result;
                                        NSString *netInfoJsonString = [[profileDetails objectForKey:KEY_NET_INFO] objectForKey:KEY_VALUE];
                                        NSDictionary *netInfoDict = [AppHelper parseJSON:[netInfoJsonString dataUsingEncoding:NSUTF8StringEncoding]];
                                        NSString *ipAddress = [netInfoDict objectForKey:KEY_ROBOT_IP_ADDRESS];
                                        NSString *secretKey = [netInfoDict objectForKey:KEY_ROBOT_DIRECT_CONNECT_SCRET];
                                        
                                        if (ipAddress && secretKey) {
                                            // Save secret key to make direct connection(TCP).
                                            [AppHelper saveDirectConnectionScretKey:secretKey];
                                            
                                            // Robot is ready to drive.
                                            [[[RobotDriveManager alloc] init] connectOverTCPWithRobotId:robotId ipAddress:ipAddress];
                                            
                                            // Send the data back to UI layer.
                                            NSMutableDictionary *robotDataDict = [[NSMutableDictionary alloc] init];
                                            NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
                                            [data setValue:robotDataDict forKey:KEY_ROBOT_DATA];
                                            [data setValue:robotId forKey:KEY_ROBOT_ID];
                                            [weakSelf sendSuccessResultAsDictionary:data forCallbackId:callbackId];
                                        }
                                        else {
                                            NSError *error = [AppHelper nserrorWithDescription:@"Failed to get Network info of robot." code:UI_ERROR_TYPE_NETWORK_INFO_NOT_SET];
                                            [weakSelf sendError:error forCallbackId:callbackId];
                                            return;
                                        }
                                    }];
}

- (void)getRobotDetail:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@", parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];

  __weak typeof(self) weakSelf = self;
  [self.serverManager robotDetailForRobot:robotId
                                  completion:^(NSDictionary *result, NSError *error) {
                                    if (error) {
                                      debugLog(@"Failed to get robot detail with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                      [weakSelf sendError:error forCallbackId:callbackId];
                                      return;
                                    }
                                    
                                    // Save robot detail in local DB.
                                    NSDictionary *robotDetailsDict = result;
                                    NeatoRobot *neatoRobot = [[NeatoRobot alloc] initWithDictionary:robotDetailsDict];
                                    [NeatoRobotHelper saveNeatoRobot:neatoRobot];

                                    // Send the data back to UI layer.
                                    NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                                    [pluginDataDict setObject:neatoRobot.serialNumber forKey:KEY_ROBOT_ID];
                                    [pluginDataDict setObject:neatoRobot.name forKey:KEY_ROBOT_NAME];
                                    [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                                  }];
}

- (void)clearRobotData:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"parameters received : %@", parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  NSString *email = [parameters objectForKey:KEY_EMAIL];

  __weak typeof(self) weakSelf = self;
  [self.serverManager clearRobotAssociationWithRobotId:robotId
                                                  email:email
                                             completion:^(NSDictionary *result, NSError *error) {
                                               if (error) {
                                                 debugLog(@"Failed to clear robot association with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                                 [weakSelf sendError:error forCallbackId:callbackId];
                                                 return;
                                               }

                                               NSString *message = [result valueForKey:NEATO_RESPONSE_MESSAGE];

                                               // Send the data back to UI layer.
                                               NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                                               [pluginDataDict setValue:message forKey:KEY_MESSAGE];
                                               [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                                             }];
}

- (void)getScheduleEvents:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@",parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
  
  // Save last used Robot Id, selected by user at JS layer.
  [AppHelper saveLastUsedRobotId:robotId];
  
  __weak typeof(self) weakSelf = self;
  [self.scheduleManager scheduleEventsForRobotWithId:robotId
                                      ofScheduleType:scheduleType
                                          completion:^(NSDictionary *result, NSError *error) {
                                            if (error) {
                                              debugLog(@"Failed to get schedule events with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                              [weakSelf sendError:error forCallbackId:callbackId];
                                              return;
                                            }
                                            
                                            // Send the data back to UI layer.
                                            NSDictionary *pluginDataDict = result;
                                            [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                                          }];
}

- (void)getRobotOnlineStatus:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@", parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  
  __weak typeof(self) weakSelf = self;
  [self.serverManager onlineStatusForRobotWithId:robotId
                                      completion:^(NSDictionary *result, NSError *error) {
                                        if (error) {
                                          debugLog(@"Failed to get Robot's online status with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                          [weakSelf sendError:error forCallbackId:callbackId];
                                          return;
                                        }
                                        
                                        NSString *onlineStatus = [result valueForKey:NEATO_ROBOT_ONLINE_STATUS];
                                        
                                        // Send the data back to UI layer.
                                        NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                                        [pluginDataDict setValue:robotId forKey:KEY_ROBOT_ID];
                                        [pluginDataDict setValue:[NSNumber numberWithBool:[onlineStatus boolValue]] forKey:KEY_ROBOT_ONLINE_STATUS];
                                        [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                                      }];
}

- (void)isScheduleEnabled:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@", parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  
  __weak typeof(self) weakSelf = self;
  [self.serverManager profileDetailsForRobot:robotId
                                  completion:^(NSDictionary *result, NSError *error) {
                                    if (error) {
                                      debugLog(@"Failed to get schedule's enabled status with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                                      [weakSelf sendError:error forCallbackId:callbackId];
                                      return;
                                    }
                                    NSDictionary *profileDetails = result;
                                    
                                    // Get required data.
                                    NSDictionary *scheduleDict = [profileDetails valueForKey:NEATO_KEY_SCHEDULE_ENABLED] ? [profileDetails valueForKey:NEATO_KEY_SCHEDULE_ENABLED] : [profileDetails valueForKey:NEATO_KEY_SCHEDULE_ENABLED_2];
                                    NSString *scheduleEnableStatus = [scheduleDict valueForKey:KEY_VALUE];
                                    
                                    // Send the data back to UI layer.
                                    NSMutableDictionary *pluginDataDict = [[NSMutableDictionary alloc] init];
                                    [pluginDataDict setValue:[NSNumber numberWithBool:[scheduleEnableStatus boolValue]] forKey:KEY_SCHEDULE_IS_ENABLED];
                                    [pluginDataDict setValue:NEATO_SCHEDULE_BASIC forKey:KEY_SCHEDULE_TYPE];
                                    [pluginDataDict setValue:robotId forKey:KEY_ROBOT_ID];
                                    [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                                  }];
}

- (void)enableSchedule:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@",parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  NSString *email = [NeatoUserHelper getLoggedInUserEmail];
  BOOL enable = [[parameters objectForKey:KEY_ENABLE_DISABLE_SCHEDULE ] boolValue];
  NSString *scheduleType = [parameters objectForKey:KEY_SCHEDULE_TYPE];
  
  __weak typeof(self) weakSelf = self;
  [self.serverManager setEnableStatus:enable
                          withRobotId:robotId
                         scheduleType:scheduleType
                            userEmail:email
                           completion:^(NSDictionary *result, NSError *error) {
                             if (error) {
                               debugLog(@"Failed to set enable status of robot with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                               [weakSelf sendError:error forCallbackId:callbackId];
                               return;
                             }
                             
                             NSDictionary *pluginDataDict = result;
                             [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                           }];
}

- (void)setRobotName2:(CDVInvokedUrlCommand *)command {
  debugLog(@"");
  NSString *callbackId = command.callbackId;
  NSDictionary *parameters = [command.arguments objectAtIndex:0];
  debugLog(@"received parameters : %@",parameters);
  NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
  NSString *robotName = [parameters objectForKey:KEY_ROBOT_NAME];
  
  __weak typeof(self) weakSelf = self;
  [self.serverManager setRobotName2:robotName
                     forRobotWithId:robotId
                         completion:^(NSDictionary *result, NSError *error) {
                           if (error) {
                             debugLog(@"Failed to set robot name with error = %@, info = %@", [error localizedDescription], [error userInfo]);
                             [weakSelf sendError:error forCallbackId:callbackId];
                             return;
                           }
                           
                           NSDictionary *pluginDataDict = result;
                           [weakSelf sendSuccessResultAsDictionary:pluginDataDict forCallbackId:callbackId];
                         }];
}

@end
