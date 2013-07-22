#import "RobotCommandHelper.h"
#import "LogHelper.h"
#import "CommandsHelper.h"
#import "TCPConnectionHelper.h"
#import "TCPHelper.h"
#import "XMPPHelper.h"
#import "NeatoRobotHelper.h"
#import "AppHelper.h"
#import "TCPCommandHelper.h"
#import "XMPPConnectionHelper.h"
#import "XMPPCommandHelper.h"
#import "CleaningArea.h"
#import "NeatoUserHelper.h"
#import "NeatoCommandExpiryHelper.h"

#define START_ROBOT_COMMAND_TAG                 1001
#define STOP_ROBOT_COMMAND_TAG                  1002
#define SET_TIME_ROBOT_COMMAND_TAG              1003
#define PAUSE_ROBOT_COMMAND_TAG                 1004
#define ENABLE_DISABLE_SCHEDULE_COMMAND_TAG     1005
#define SEND_TO_BASE_COMMAND_TAG                1006
#define TURN_VACUUM_ONOFF_COMMAND_TAG           1016
#define RESUME_ROBOT_COMMAND_TAG                1007
#define TURN_WIFI_ONOFF_COMMAND_TAG             1008

@interface RobotCommandHelper()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) RobotCommandHelper *retainedSelf;

- (void)sendCommandWithId:(int)commandId toRobot2:(NSString *)robotId withCommandTag:(long)commandTag withParams:(NSDictionary *)params delegate:(id)delegate;
- (NSMutableData *)tcpCommandHeaderForCommand:(NSData *)command;
- (NSData *)formattedTCPCommandFromCommand:(NSData *)command;
- (BOOL)isTimedModeSupportedForCommand:(NSString *)commandId;
- (void)sendCommandOverTCPXMPPToRobot:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate;
- (void)sendCommandOverServerToRobot:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate;
- (BOOL)isExpirableCommand:(NSInteger)commandId;
@end

@implementation RobotCommandHelper
@synthesize delegate = _delegate;
@synthesize retainedSelf = _retainedSelf;

- (BOOL)isTimedModeSupportedForCommand:(NSString *)commandId {
    switch([commandId intValue]) {
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_SEND_TO_BASE:
        case COMMAND_RESUME_CLEANING:
            return YES;
        default:
            return NO;
    }
}

- (BOOL)isExpirableCommand:(NSInteger)commandId {
    switch(commandId) {
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_RESUME_CLEANING:
            return YES;
        default:
            return NO;
    }
}

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"commandId value is %d",[commandId intValue]);
    self.retainedSelf = self;
    self.delegate = delegate;
    if([commandId intValue] == COMMAND_START_ROBOT) {
        params = [self updatedSpotParametersInParams:params forRobotWithId:robotId];
    }
    
    if(TIMED_MODE_ENABLED && [self isTimedModeSupportedForCommand:commandId]) {
        [self sendCommandOverServerToRobot:robotId commandId:commandId params:params delegate:delegate];
                
    }else {
        [self sendCommandOverTCPXMPPToRobot:robotId commandId:commandId params:params delegate:delegate];
    }
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

- (void)sendCommandWithId:(int)commandId toRobot2:(NSString *)robotId withCommandTag:(long)commandTag withParams:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"");
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] getRobotCommand2WithId:commandId withParams:params andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:commandTag requestId:requestId delegate:self];
    }
    else {
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
        if (robot == nil) {
            debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", robotId);
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"No robot found with id = %@ in the local storage", robotId] code:200]];
            return;
        }
        XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
        xmppHelper.delegate = self;
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

- (void)commandSentOverXMPP {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:@selector(commandSentOverXMPP2)];
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
        [[NeatoCommandExpiryHelper expirableCommandHelper] startCommandTimerForRobotId:command.robotId];
    }
    
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
    [robotCommand.profileDict setValue:robotCommand.xmlCommand forKey:KEY_ROBOT_CLEANING_COMMAND];
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager sendCommand:robotCommand];
}

- (void)sendCommandOverTCPXMPPToRobot:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    switch ([commandId intValue]) {
        case COMMAND_START_ROBOT: {
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:START_ROBOT_COMMAND_TAG withParams:params delegate:delegate];
            break;
        }
        case COMMAND_STOP_ROBOT:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:STOP_ROBOT_COMMAND_TAG withParams:params delegate:delegate];
            break;
        case COMMAND_PAUSE_CLEANING:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:PAUSE_ROBOT_COMMAND_TAG withParams:params delegate:delegate];
            break;
        case COMMAND_SET_ROBOT_TIME:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:SET_TIME_ROBOT_COMMAND_TAG withParams:params delegate:delegate];
            break;
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
            [self sendCommandWithId:[commandId intValue]toRobot2:robotId withCommandTag:ENABLE_DISABLE_SCHEDULE_COMMAND_TAG withParams:updatedParams delegate:delegate];
            break;
        }
        case COMMAND_SEND_TO_BASE:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:SEND_TO_BASE_COMMAND_TAG withParams:params delegate:delegate];
            break;
        case COMMAND_TURN_VACUUM_ONOFF:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:TURN_VACUUM_ONOFF_COMMAND_TAG withParams:params delegate:delegate];
            break;
        case COMMAND_RESUME_CLEANING:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:RESUME_ROBOT_COMMAND_TAG withParams:params delegate:delegate];
            break;
        case COMMAND_TURN_WIFI_ONOFF:
            [self sendCommandWithId:[commandId intValue] toRobot2:robotId withCommandTag:TURN_WIFI_ONOFF_COMMAND_TAG withParams:params delegate:delegate];
            break;
        default:
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"Command ID %d not supported!", [commandId intValue]] code:200]];
            break;
    }

}
@end
