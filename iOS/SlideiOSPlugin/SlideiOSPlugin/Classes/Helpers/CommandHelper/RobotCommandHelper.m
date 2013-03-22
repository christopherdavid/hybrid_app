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

#define START_ROBOT_COMMAND_TAG                 1001
#define STOP_ROBOT_COMMAND_TAG                  1002
#define SET_TIME_ROBOT_COMMAND_TAG              1003
#define PAUSE_ROBOT_COMMAND_TAG                 1004
#define ENABLE_DISABLE_SCHEDULE_COMMAND_TAG     1005
#define SEND_TO_BASE_COMMAND_TAG                1006

@interface RobotCommandHelper()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) RobotCommandHelper *retainedSelf;

- (void)sendStartCleaningTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate;
- (void)sendStopCleaningTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate;
- (void)sendPauseCleaningTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate;
- (void)sendSetRobotTimeTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate;
- (void)sendEnableDisableScheduleTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate;
- (void)sendToBaseRobotWithId:(NSString *)robotId params:(NSDictionary *)params delegate:(id)delegate;
- (NSMutableData *)tcpCommandHeaderForCommand:(NSData *)command;
- (NSData *)formattedTCPCommandFromCommand:(NSData *)command;
@end

@implementation RobotCommandHelper
@synthesize delegate = _delegate;
@synthesize retainedSelf = _retainedSelf;

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"commandId value is %d",[commandId intValue]);
    self.retainedSelf = self;
    self.delegate = delegate;
    switch ([commandId intValue]) {
        case COMMAND_START_ROBOT:
            [self sendStartCleaningTo:robotId withParams:params delegate:delegate];
            break;
        case COMMAND_STOP_ROBOT:
            [self sendStopCleaningTo:robotId withParams:params delegate:delegate];
            break;
        case COMMAND_PAUSE_CLEANING:
            [self sendPauseCleaningTo:robotId withParams:params delegate:delegate];
            break;
        case COMMAND_SET_ROBOT_TIME:
            [self sendSetRobotTimeTo:robotId withParams:params delegate:delegate];
            break;
        case COMMAND_ENABLE_DISABLE_SCHEDULE:
            [self sendEnableDisableScheduleTo:robotId withParams:params delegate:delegate];
            break;
        case COMMAND_SEND_TO_BASE:
            [self sendToBaseRobotWithId:robotId params:params delegate:delegate];
            break;
        default:
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"Command ID %d not supported!", [commandId intValue]] code:200]];        
            break;
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

- (void)sendStartCleaningTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"");
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] startRobotCommand2WithParams:params andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:START_ROBOT_COMMAND_TAG requestId:requestId delegate:self];
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
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] startRobotCommand2WithParams:params andRequestId:requestId] withTag:START_ROBOT_COMMAND_TAG];
    }
}

- (void)sendStopCleaningTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"");
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] stopRobotCommand2WithParams:params andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:STOP_ROBOT_COMMAND_TAG requestId:requestId delegate:self];
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
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] stopRobotCommand2WithParams:params andRequestId:requestId] withTag:STOP_ROBOT_COMMAND_TAG];
    }
}

- (void)sendPauseCleaningTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"");
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] pauseRobotCommandWithParams:params andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:PAUSE_ROBOT_COMMAND_TAG requestId:requestId delegate:self];
    }
    else {
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
        if (robot == nil)
        {
            debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", robotId);
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"No robot found with id = %@ in the local storage", robotId] code:200]];
            return;
        }
        XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
        xmppHelper.delegate = self;
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] pauseRobotCommandWithParams:params andRequestId:requestId] withTag:PAUSE_ROBOT_COMMAND_TAG];
    }
}

- (void)sendSetRobotTimeTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate {
    debugLog(@"");
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] setRobotTimeCommandWithParams:params andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:SET_TIME_ROBOT_COMMAND_TAG requestId:requestId delegate:self];
    }
    else {
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
        if (robot == nil)
        {
            debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", robotId);
            [self failedToSendCommandOverTCPWithError:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"No robot found with id = %@ in the local storage", robotId] code:200]];
            return;
        }
        XMPPConnectionHelper *xmppHelper = [[XMPPConnectionHelper alloc] init];
        xmppHelper.delegate = self;
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] setRobotTimeCommandWithParams:params andRequestId:requestId] withTag:SET_TIME_ROBOT_COMMAND_TAG];
    }
}

- (void)sendEnableDisableScheduleTo:(NSString *)robotId withParams:(NSDictionary *)params delegate:(id)delegate {
    // Value of "enableSchedule" key is boolean, server needs "true" or "false" string.
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
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] enableDisableScheduleCommandWithParams:updatedParams andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:ENABLE_DISABLE_SCHEDULE_COMMAND_TAG requestId:requestId delegate:self];
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
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] enableDisableScheduleCommandWithParams:updatedParams andRequestId:requestId] withTag:ENABLE_DISABLE_SCHEDULE_COMMAND_TAG];
    }
}

- (void)sendToBaseRobotWithId:(NSString *)robotId params:(NSDictionary *)params delegate:(id)delegate {
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    NSString *requestId = [AppHelper generateUniqueString];
    if ([helper isConnected]) {
        NSData *command = [[[TCPCommandHelper alloc] init] sendToBaseCommandWithParams:params andRequestId:requestId];
        [helper sendCommandToRobot2:[self formattedTCPCommandFromCommand:command] withTag:SEND_TO_BASE_COMMAND_TAG requestId:requestId delegate:self];
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
        [xmppHelper sendCommandToRobot:robot.chatId command:[[[XMPPCommandHelper alloc] init] sendToBaseCommandWithParams:params andRequestId:requestId] withTag:SEND_TO_BASE_COMMAND_TAG];
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

@end
