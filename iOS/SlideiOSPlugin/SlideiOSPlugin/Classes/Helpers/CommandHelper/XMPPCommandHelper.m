#import "XMPPCommandHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "CommandsHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoUser.h"


#define TEMP_XMPP_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><command><commandid>%d</commandid><commanddata/></command></packet>"

#define TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>%d</version><signature>%d</signature></header><payload><request><command>%d</command><requestId>%@</requestId><timeStamp>%@</timeStamp><retryCount>%d</retryCount><responseNeeded>%@</responseNeeded><replyTo>%@</replyTo><distributionMode>%d</distributionMode>%@</request></payload></packet>"


@implementation XMPPCommandHelper


- (NSString *)getStartRobotCommand {
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], COMMAND_START_ROBOT];
    debugLog(@"command = %@", command);
    return command;
}

- (NSString *)getStopRobotCommand {
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], COMMAND_STOP_ROBOT];
    debugLog(@"command = %@", command);
    return command;
}

- (NSString *)startRobotCommand2WithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_START_ROBOT, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"XMPP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}

- (NSString *)stopRobotCommand2WithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_STOP_ROBOT, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"XMPP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}

- (NSString *)pauseRobotCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_PAUSE_CLEANING, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"XMPP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}


- (NSString *)setRobotTimeCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_SET_ROBOT_TIME, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"XMPP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}

- (NSString *)enableDisableScheduleCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_ENABLE_DISABLE_SCHEDULE, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"XMPP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}

- (id)sendToBaseCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
   debugLog(@"");
   CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_SEND_TO_BASE, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"XMPP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}

@end
