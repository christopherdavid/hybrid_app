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

// Helper class
#import "TCPConnectionHelper.h"

@interface RobotManagerPlugin ()

@property (nonatomic, strong) NeatoServerManager *serverManager;

@end

@implementation RobotManagerPlugin


- (void) discoverNearByRobots:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call findRobotsNearBy:callbackId];
}

- (void)foundRobotsNearby:(NSArray *)nearbyRobots callbackId:(NSString *)callbackId {
    debugLog(@"Fount robots = %d", [nearbyRobots count]);
    NSMutableArray *robotArray = [[NSMutableArray alloc] init];
    for(int i = 0;i < [nearbyRobots count];i++)
    {
        NeatoRobot *robot = [nearbyRobots objectAtIndex:i];
        NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
        [dict setObject:robot.name forKey:KEY_ROBOT_NAME];
        [dict setObject:robot.robotId forKey:KEY_ROBOT_ID];
        [robotArray addObject:dict];
    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:robotArray];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) sendCommandToRobot:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    
    NSString *callbackId = command.callbackId; //use this to get command.callbackId
    
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are : %@",parameters);
    NSString *commandId = [parameters objectForKey:KEY_COMMAND_ID];
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot:robotId commandId:commandId callbackId:callbackId];
}

- (void) tryDirectConnection:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSDictionary *parameters =  [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are : %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    
    [call tryDirectConnection:[parameters valueForKey:KEY_ROBOT_ID] callbackId:callbackId];
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
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    
    NSString *robotId = [parameters valueForKey:KEY_ROBOT_ID];
    
    // TODO: why take robot when we can connect to only one device at a time on TCP
    [call diconnectRobotFromTCP:robotId callbackId:callbackId];
    
}

- (void)gotRemoteRobotIP:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId {
    debugLog(@"");
    debugLog(@"Robot chat Id : %@", neatoRobot.chatId);
    
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

- (void)setRobotName2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *robotName = [parameters objectForKey:KEY_ROBOT_NAME];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call setRobotName2:robotName forRobotWithId:robotId callbackId:callbackId];
}

- (void)failedToUpdateRobotNameWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:[[NSNumber numberWithInt:error.code] stringValue] forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *jsonRobot = [[NSMutableDictionary alloc] init];
    [jsonRobot setObject:robotId forKey:KEY_ROBOT_ID];
    [jsonRobot setObject:name forKey:KEY_ROBOT_NAME];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonRobot];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)getRobotDetail:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    //call callWrapper method
    [call getDetailsForRobotWithId:robotId callbackId:callbackId];
}

- (void)gotRobotDetails:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *jsonRobot = [[NSMutableDictionary alloc] init];
    [jsonRobot setObject:neatoRobot.serialNumber forKey:KEY_ROBOT_ID];
    [jsonRobot setObject:neatoRobot.name forKey:KEY_ROBOT_NAME];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonRobot];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)failedToGetRobotDetailsWihError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)getRobotOnlineStatus:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    //call callWrapper method
    [call onlineStatusForRobotWithId:robotId callbackId:callbackId];
}

- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *jsonRobot = [[NSMutableDictionary alloc] init];
    [jsonRobot setObject:robotId forKey:KEY_ROBOT_ID];
    [jsonRobot setObject:[NSNumber numberWithBool:[status boolValue]] forKey:KEY_ROBOT_ONLINE_STATUS];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonRobot];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)tryDirectConnection2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters =  [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call tryDirectConnection2:[parameters valueForKey:KEY_ROBOT_ID] callbackId:callbackId];
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

- (void)commandSentOverXMPP2:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_SUCCESS];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
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
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    id pluginResult = [callWrapper createScheduleForRobotId:robotId ofScheduleType:scheduleType];
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
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    id pluginResult = [call addScheduleEventData:scheduleEventData forScheduleWithScheduleId:scheduleId];
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
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    id pluginResult = [call updateScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId withScheduleEventdata:scheduleEventData];
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
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    id pluginResult = [call deleteScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
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
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    id pluginResult = [callWrapper scheduleEventDataWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
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
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    id pluginResult = [callWrapper scheduleDataForScheduleId:scheduleId];
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

- (void)getScheduleEvents:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
  
    [AppHelper saveLastUsedRobotId:robotId];
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call scheduleEventsForRobotWithId:robotId ofScheduleType:scheduleType callbackId:callbackId];
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


- (void) robotSetSchedule2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters received %@",parameters);
    NSString *robotID = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
    NSArray *schedulesArray = [parameters objectForKey:KEY_SCHEDULE];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call setRobotSchedule:schedulesArray forRobotId:robotID ofType:scheduleType callbackId:callbackId];
}

- (void) getSchedule2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters received %@",parameters);
    NSString *robotID = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call getRobotScheduleForRobotId:robotID ofType:scheduleType callbackId:callbackId];
}

- (void)deleteScheduleData:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
    debugLog(@"received parameters %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call deleteRobotScheduleForRobotId:[parameters valueForKey:KEY_ROBOT_ID] ofType:scheduleType callbackId:callbackId];
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

- (void)setScheduleSuccess:(NSString *)message callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)getScheduleSuccess:(NSDictionary *)jsonObject callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonObject];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)setScheduleError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)getScheduleError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void)deleteScheduleSuccess:(NSString *)message callbackId:(NSString *)callbackId {
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)deleteScheduleError:(NSError *)error callbackId:(NSString *)callbackId {
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
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
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call enabledDisable:enable schedule:[scheduleType intValue] forRobotWithId:robotId withUserEmail:email callbackId:callbackId];
}

- (void)failedToEnableDisableScheduleWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:[[NSNumber numberWithInt:error.code] stringValue] forKey:KEY_ERROR_CODE];
    
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
  
    // Do not send command if cleaning category is MANUAL and not directly connected (i.e over TCP)
    NSNumber *cleaningCategory = [params valueForKey:KEY_CLEANING_CATEGORY];
    if ([cleaningCategory integerValue] == CLEANING_CATEGORY_MANUAL) {
        TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
        if (![helper isConnected]) {
            debugLog(@"Manual cleaning cannot be started as direct connection does not exist");
            NSError *error = [AppHelper nserrorWithDescription:@"Robot is not connected" code:UI_ROBOT_NOT_CONNECTED];
            [self sendError:error forCallbackId:callbackId];
            return;
        }
    }
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
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    
    NSString *commandId = [NSString stringWithFormat:@"%d", COMMAND_TURN_WIFI_ONOFF];
    
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSDictionary *commandParams = [parameters objectForKey:KEY_COMMAND_PARAMETERS];
    NSMutableDictionary *params = [commandParams objectForKey:KEY_PARAMS];
    debugLog(@"params = %@",params);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot2:robotId commandId:commandId params:params callbackId:callbackId];
}

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

- (void)setSpotDefinition:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSInteger cleaningAreaLength = [[parameters objectForKey:KEY_SPOT_CLEANING_AREA_LENGTH] intValue];
    NSInteger cleaningAreaHeight = [[parameters objectForKey:KEY_SPOT_CLEANING_AREA_HEIGHT] intValue];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
        id result = [callWrapper setSpotDefinitionForRobotWithId:robotId cleaningAreaLength:cleaningAreaLength cleaningAreaHeight:cleaningAreaHeight];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([result isKindOfClass:[NSError class]]) {
                // Error callback.
                NSError *error = (NSError *)result;
                [self sendError:error forCallbackId:callbackId];
            }
            else {
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
                [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
            }
        });
    });
}

- (void)getSpotDefinition:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters %@",parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
        id result = [callWrapper spotDefinitionForRobotWithId:robotId];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([result isKindOfClass:[NSError class]]) {
                // Error callback.
                NSError *error = (NSError *)result;
                [self sendError:error forCallbackId:callbackId];
            }
            else {
                NSMutableDictionary *resultData = [[NSMutableDictionary alloc] init];
                CleaningArea *cleaningArea = (CleaningArea *)result;
                if(cleaningArea.height || cleaningArea.length) {
                    [resultData setValue:[NSNumber numberWithInt:cleaningArea.length] forKey:KEY_SPOT_CLEANING_AREA_LENGTH];
                    [resultData setValue:[NSNumber numberWithInt:cleaningArea.height] forKey:KEY_SPOT_CLEANING_AREA_HEIGHT];
                } else {
                    [resultData setValue:[NSNumber numberWithInt:DEFAULT_SPOT_CLEANING_LENGTH] forKey:KEY_SPOT_CLEANING_AREA_LENGTH];
                    [resultData setValue:[NSNumber numberWithInt:DEFAULT_SPOT_CLEANING_HEIGHT] forKey:KEY_SPOT_CLEANING_AREA_HEIGHT];
                }
                CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];
                [self writeJavascript:[result toSuccessCallbackString:callbackId]];
            }
        });
    });
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
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call virtualOnlineStatusForRobotWithId:robotId callbackId:callbackId];
}

- (void)virtualOnlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *jsonRobot = [[NSMutableDictionary alloc] init];
    [jsonRobot setObject:robotId forKey:KEY_ROBOT_ID];
    [jsonRobot setObject:[NSNumber numberWithBool:[status boolValue]] forKey:KEY_ROBOT_ONLINE_STATUS];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonRobot];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)failedToGetRobotVirtualOnlineStatusWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
}

- (void)dealloc {
    debugLog(@"");
}

- (void)isScheduleEnabled:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    NSString *scheduleType = [parameters stringForKey:KEY_SCHEDULE_TYPE];
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper isScheduleType:scheduleType enabledForRobotWithId:robotId callbackId:callbackId];
}

- (void)gotScheduleStatus:(NSDictionary *)status callbackId:(NSString *)callbackId{
    debugLog(@"Status = %@", status);
    NSString *scheduleStatus = [status valueForKey:NEATO_KEY_SCHEDULE_ENABLED] ? [status valueForKey:NEATO_KEY_SCHEDULE_ENABLED] : [status valueForKey:NEATO_KEY_SCHEDULE_ENABLED_2];
    NSMutableDictionary *message = [[NSMutableDictionary alloc] init];
    [message setValue:[scheduleStatus boolValue] ? [NSNumber numberWithBool:YES] : [NSNumber numberWithBool:NO] forKey:KEY_SCHEDULE_IS_ENABLED];
    [message setValue:NEATO_SCHEDULE_BASIC forKey:KEY_SCHEDULE_TYPE];
    [message setValue:[status valueForKey:KEY_SERIAL_NUMBER] forKey:KEY_ROBOT_ID];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    
}

- (void)failedToGetScheduleStatusWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
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
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper getCleaningStateForRobotWithId:robotId callbackId:callbackId];
}

- (void)gotCleaningStateWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId {
    NSDictionary *neatoResult = [resultData valueForKeyPath:NEATO_RESPONSE_RESULT];
    NSDictionary *robotProfileDetails = [neatoResult valueForKeyPath:NEATO_PROFILE_DETAILS];
    debugLog(@"RobotProfileDetails received from server : %@", robotProfileDetails);
    XMPPRobotDataChangeManager *xmppDataChangeManager = [XMPPRobotDataChangeManager sharedXmppDataChangeManager];
    // Update the timestamp in DB.
    [xmppDataChangeManager updateDataTimestampIfChangedForKey:KEY_ROBOT_CURRENT_STATE withProfile:robotProfileDetails];
    
    // Send the data back to UI layer.
    NSString *robotId = [[robotProfileDetails valueForKey:KEY_SERIAL_NUMBER] valueForKey:KEY_VALUE];
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    NSInteger robotActualState = [XMPPRobotCleaningStateHelper robotActualStateFromRobotProfile:robotProfileDetails];
    NSInteger robotCurrentState = [XMPPRobotCleaningStateHelper robotCurrentStateFromRobotProfile:robotProfileDetails];
    [data setValue:[NSNumber numberWithInteger:robotCurrentState] forKey:KEY_ROBOT_CURRENT_STATE];
    [data setValue:[NSNumber numberWithInteger:robotActualState] forKey:KEY_ROBOT_NEW_VIRTUAL_STATE];
    [data setValue:robotId forKey:KEY_ROBOT_ID];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    
}

- (void)failedToGetCleaningStateWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"Failed to send command with error  = %@", error);
    [self sendError:error forCallbackId:callbackId];
}

- (void)intendToDrive:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper requestIntentToDriveForRobotWithId:robotId callbackId:callbackId];
}

- (void)intentToDriveRequestSuccededWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:[resultData valueForKeyPath:NEATO_RESPONSE_EXPECTED_TIME] forKey:NEATO_RESPONSE_EXPECTED_TIME];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)intentToDriveRequestFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
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

- (void)driveRobotSentforCallBackId:(NSString *)callbackId {
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)driveRobotFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
}

- (void)cancelIntendToDrive:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@", parameters);
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    [callWrapper cancelIntendToDriveForRobotWithId:robotId callbackId:callbackId];
}

- (void)cancelIntendToDriveSuccededForCallbackId:(NSString *)callbackId {
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)cancelIntendToDriveFailedWithError:(NSError *)error callbackId:(NSString *)callbackId {
    [self sendError:error forCallbackId:callbackId];
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
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    id data = [callWrapper isConnectedOverTCPWithRobotId:robotId callbackId:callbackId];
    if ([data isKindOfClass:[NSMutableDictionary class]]) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:(NSDictionary *)data];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}

- (void)turnMotorOnOff2:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters : %@",parameters);
    NSString *commandId = [NSString stringWithFormat:@"%d", COMMAND_TURN_MOTOR_ONOFF];
    NSString *onOff = [[parameters objectForKey:KEY_ON] stringValue];
    NSString *motorType = [parameters objectForKey:KEY_MOTOR_TYPE];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] initWithCapacity:2];
    [params setValue:onOff forKey:KEY_FLAG_ON_OFF];
    [params setValue:motorType forKey:KEY_MOTOR_TYPE];
    
    RobotManagerCallWrapper *callWrapper = [[RobotManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    // As we can connect to only one robot over TCP, robotId can be anything. Command is sent to connected robot.
    [callWrapper sendCommandOverTCPToRobotWithId:@"" commandId:commandId params:params callbackId:callbackId];
}

- (void)clearRobotData:(CDVInvokedUrlCommand *)command {
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    NSString *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters received : %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call clearDataForRobotId:[parameters valueForKey:KEY_ROBOT_ID] email:[parameters valueForKey:KEY_EMAIL] callbackId:callbackId];
}

- (void)clearRobotDataSucceededWithMessage:(NSString *)message callbackId:(NSString *)callbackId {
    debugLog(@"");
    NSMutableDictionary *messageInfo = [[NSMutableDictionary alloc] init];
    [messageInfo setValue:message forKey:KEY_MESSAGE];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:messageInfo];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)failedToClearRobotDataWithError:(NSError *)error callbackId:(NSString *)callbackId {
    debugLog(@"");
    [self sendError:error forCallbackId:callbackId];
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

@end
