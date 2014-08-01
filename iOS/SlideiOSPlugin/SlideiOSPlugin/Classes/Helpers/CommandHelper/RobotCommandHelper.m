#import "RobotCommandHelper.h"
#import "LogHelper.h"
#import "CommandsHelper.h"
#import "TCPConnectionHelper.h"
#import "NeatoRobotHelper.h"
#import "AppHelper.h"
#import "TCPCommandHelper.h"
#import "XMPPConnectionHelper.h"
#import "XMPPCommandHelper.h"
#import "CleaningArea.h"
#import "NeatoUserHelper.h"
#import "NeatoCommandExpiryHelper.h"
#import "ProfileDetail.h"

@interface RobotCommandHelper()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) RobotCommandHelper *retainedSelf;
@end

@implementation RobotCommandHelper

- (BOOL)isExpirableCommand:(NSInteger)commandId {
    switch(commandId) {
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_RESUME_CLEANING:
        case COMMAND_SEND_TO_BASE:
            return YES;
        default:
            return NO;
    }
}

- (NSString *)profileKeyForCommandId:(NSInteger)commandId {
    switch (commandId) {
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_RESUME_CLEANING:
        case COMMAND_SEND_TO_BASE:
            return KEY_ROBOT_CLEANING_COMMAND;
        case COMMAND_TURN_WIFI_ONOFF:
            return KEY_TURN_WIFI_ONOFF;
        default:
            debugLog(@"No profile key for command id = %d returning nil.", commandId);
            return nil;
    }
}

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"commandId value is %d",[commandId intValue]);
    self.retainedSelf = self;
    self.delegate = delegate;
    if([commandId intValue] == COMMAND_START_ROBOT) {
        params = [self updatedSpotParametersInParams:params forRobotWithId:robotId];
    }
    
    // For now we send start/stop/resume/pause/sendToBase commands via XMPP.
    if([self shouldSendCommandDirectlyViaXMPP:commandId]) {
        [self sendCommandOverXMPPToRobotWithId:robotId commandId:commandId params:params delegate:delegate];
    }
    else {
        [self sendCommandOverServerToRobot:robotId commandId:commandId params:params delegate:delegate];
    }
}

- (BOOL)shouldSendCommandDirectlyViaXMPP:(NSString *)commandId {
    
    // Check if following commands are valid to be sent directly via XMPP.
    // As these commands should be sent via XMPP, if command category is not manual.
    BOOL isCommandValid = NO;
    switch ([commandId integerValue]) {
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_RESUME_CLEANING:
        case COMMAND_SEND_TO_BASE:
            isCommandValid = YES;
            break;
        default:
            isCommandValid = NO;
            break;
    }
    return (isCommandValid && SHOULD_SEND_COMMAND_DIRECTLY_VIA_XMPP);
}

- (NSMutableData *)tcpCommandHeaderForCommand:(NSData *)command {
    int commandLength = [command length];
    int signature = [AppHelper getAppSignature];
    int appVersion = [[[CommandsHelper alloc] init] versionForCommand];
    if ([AppHelper isArchitectureLittleEndian]) {
        debugLog(@"System is Little Endian!");
        commandLength = [AppHelper swapIntoBigEndian:commandLength];
        signature = [AppHelper swapIntoBigEndian:signature];
        appVersion = [AppHelper swapIntoBigEndian:appVersion];
    }
    
    NSData *dataSignature = [NSData dataWithBytes:&signature length:sizeof(signature)];
    NSData *dataAppversion = [NSData dataWithBytes:&appVersion length:sizeof(appVersion)];
    NSData *dataCommandLength = [NSData dataWithBytes: &commandLength length: sizeof(commandLength)];
    
    NSMutableData *commandHeader = [[NSMutableData alloc] init];
    [commandHeader appendData:dataSignature];
    [commandHeader appendData:dataAppversion];
    [commandHeader  appendData:dataCommandLength];
    return commandHeader;
}

- (NSData *)formattedTCPCommandFromCommand:(NSData *)command {
    NSMutableData *commandHeader = [self tcpCommandHeaderForCommand:command];
    NSMutableData *finalCommand = [[NSMutableData alloc] init];
    [finalCommand appendData:commandHeader];
    [finalCommand appendData:command];
    return finalCommand;
}

- (void)sendCommandWithId:(NSInteger)commandId toRobot2:(NSString *)robotId withCommandTag:(long)commandTag withParams:(NSDictionary *)params delegate:(id)delegate overTCP:(BOOL)overTCP {
    debugLog(@"");
    NSString *requestId = [AppHelper generateUniqueString];
    if (overTCP) {
        TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
        if ([helper isConnected]) {
            NSData *command = [[[TCPCommandHelper alloc] init] getRobotCommand2WithId:commandId withParams:params andRequestId:requestId];
            [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:commandTag requestId:requestId delegate:self];
        }
        else {
            debugLog(@"Device not connected over TCP.");
            if ([self.delegate respondsToSelector:@selector(failedToSendCommandOverTCPWithError:)]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate performSelector:@selector(failedToSendCommandOverTCPWithError:) withObject:[AppHelper nserrorWithDescription:@"Device not connected over TCP" code:200]];
                    self.delegate = nil;
                });
            }
        }
    }
    else {
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
        if (robot == nil) {
            debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", robotId);
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"No robot found with id = %@ in the local storage", robotId] code:200]];
            return;
        }
        
        // Set command
        NSString *requestId = [AppHelper generateUniqueString];
        NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
        robotCommand.xmlCommand = [[[XMPPCommandHelper alloc] init] getRobotCommand2WithId:commandId withParams:params andRequestId:requestId];
        robotCommand.commandId = [NSString stringWithFormat:@"%ld", (long)commandId];
        robotCommand.robotId = robotId;
        robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
        [robotCommand.profileDict setValue:robotCommand.xmlCommand forKey:[self profileKeyForCommandId:commandId]];
        
        XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
        xmppHelper.delegate = self;
        xmppHelper.robotCommand = robotCommand;
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] getRobotCommand2WithId:commandId withParams:params andRequestId:requestId] withTag:commandTag];
    }
}

- (void)failedToSendCommandOverTCPWithError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(failedToSendCommandOverTCPWithError:) withObject:error];
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)commandSentOverTCP {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(commandSentOverTCP2)];
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)commandSentOverXMPP:(NeatoRobotCommand *)robotCommand {
    debugLog(@"");
    // Start a timer if the command is expirable and if a timer is not already in progress
    if ([self isExpirableCommand:[robotCommand.commandId integerValue]] && ![[NeatoCommandExpiryHelper expirableCommandHelper] isTimerRunningForRobotId:robotCommand.robotId]) {
 		[[NeatoCommandExpiryHelper expirableCommandHelper] startCommandTimerForRobotId:robotCommand.robotId withCommandId:robotCommand.commandId];
    }

    // Set expected time of success for XMPP command as 1 sec.
    NSMutableDictionary *resultDict = [[NSMutableDictionary alloc] init];
    [resultDict setValue:@(EXPECTED_SUCCESS_TIME_FOR_XMPP_COMMAND) forKey:NEATO_RESPONSE_EXPECTED_TIME];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(commandSentOverXMPP2WithResult:) withObject:resultDict];
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)failedToSendCommandOverXMPP {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(failedToSendCommandOverXMPP2)];
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (NSDictionary *)updatedSpotParametersInParams:(NSDictionary *)params forRobotWithId:(NSString *)robotId {
    NSNumber *cleaningCategory = [params valueForKey:KEY_CLEANING_CATEGORY];
    if ([cleaningCategory integerValue] == CLEANING_CATEGORY_SPOT) {
        NSMutableDictionary *updatedParams = [params mutableCopy];
        id dbResult = [NeatoRobotHelper spotDefinitionForRobotWithId:robotId];
        if ([dbResult isKindOfClass:[NSError class]]) {
            return params;
        }
        CleaningArea *cleaningArea = (CleaningArea *)dbResult;
        if (cleaningArea.height || cleaningArea.length) {
            [updatedParams setObject:[NSNumber numberWithInteger:cleaningArea.height] forKey:KEY_SPOT_CLEANING_AREA_HEIGHT];
            [updatedParams setObject:[NSNumber numberWithInteger:cleaningArea.length] forKey:KEY_SPOT_CLEANING_AREA_LENGTH];
        }
        else {
            [updatedParams setObject:[NSNumber numberWithInteger:DEFAULT_SPOT_CLEANING_HEIGHT] forKey:KEY_SPOT_CLEANING_AREA_HEIGHT];
            [updatedParams setObject:[NSNumber numberWithInteger:DEFAULT_SPOT_CLEANING_LENGTH] forKey:KEY_SPOT_CLEANING_AREA_LENGTH];
        }
        return updatedParams;
    }
    return params;
}

- (void)failedtoSendCommandWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedtoSendCommandWithError:) withObject:error];
    self.delegate = nil;
    self.retainedSelf = nil;
}

- (void)command:(NeatoRobotCommand *)command sentWithResult:(NSDictionary *)result {
    debugLog(@"");
    // Start a timer if the command is expirable and if a timer is not already in progress
    if ([self isExpirableCommand:[command.commandId integerValue]] && ![[NeatoCommandExpiryHelper expirableCommandHelper] isTimerRunningForRobotId:command.robotId]) {
 		[[NeatoCommandExpiryHelper expirableCommandHelper] startCommandTimerForRobotId:command.robotId withCommandId:command.commandId];
    }
    // Save timestamp returned from server in DB.
    ProfileDetail *profileDetail = [[ProfileDetail alloc] init];
    profileDetail.key = [self profileKeyForCommandId:[command.commandId integerValue]];
    profileDetail.timestamp = [result objectForKey:KEY_TIMESTAMP];
    [NeatoRobotHelper updateProfileDetail:profileDetail forRobotWithId:command.robotId];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(commandSentWithResult:) withObject:result];
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)sendCommandOverServerToRobot:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"");
    NSString *requestId = [AppHelper generateUniqueString];
    
    NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
    robotCommand.xmlCommand = [[[XMPPCommandHelper alloc] init] getRobotCommand2WithId:[commandId integerValue] withParams:params andRequestId:requestId];
    robotCommand.commandId = commandId;
    robotCommand.robotId = robotId;
    robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
    [robotCommand.profileDict setValue:robotCommand.xmlCommand forKey:[self profileKeyForCommandId:[commandId integerValue]]];
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager sendCommand:robotCommand];
}

- (void)sendCommandOverTCPXMPPToRobot:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate overTCP:(BOOL)overTCP {
    switch ([commandId intValue]) {
        case COMMAND_ENABLE_DISABLE_SCHEDULE: {
            NSMutableDictionary *updatedParams = [params mutableCopy];
            if([updatedParams objectForKey:@"enableSchedule"]) {
                NSNumber *value = [updatedParams objectForKey:@"enableSchedule"];
                if([value boolValue] == YES) {
                    [updatedParams setObject:@"true" forKey:@"enableSchedule"];
                }
                else {
                    [updatedParams setObject:@"false" forKey:@"enableSchedule"];
                }
            }
            [self sendCommandWithId:[commandId intValue]toRobot2:robotId withCommandTag:[commandId longLongValue] withParams:updatedParams delegate:delegate overTCP:overTCP];
            break;
        }
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_SET_ROBOT_TIME:
        case COMMAND_SEND_TO_BASE:
        case COMMAND_RESUME_CLEANING:
        case COMMAND_TURN_WIFI_ONOFF:
        case COMMAND_DRIVE_ROBOT:
        case COMMAND_TURN_MOTOR_ONOFF:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:[commandId longLongValue] withParams:params delegate:delegate overTCP:overTCP];
            break;
        default:
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"Command ID %d not supported!", [commandId intValue]] code:200]];
            break;
    }

}

- (void)sendCommandOverXMPPToRobotWithId:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    [self sendCommandOverTCPXMPPToRobot:robotId commandId:commandId params:params delegate:delegate overTCP:NO];
}

- (void)sendCommandOverTCPToRobotWithId:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"commandId value is %d",[commandId intValue]);
    self.retainedSelf = self;
    self.delegate = delegate;
    [self sendCommandOverTCPXMPPToRobot:robotId commandId:commandId params:params delegate:delegate overTCP:YES];
}

@end
