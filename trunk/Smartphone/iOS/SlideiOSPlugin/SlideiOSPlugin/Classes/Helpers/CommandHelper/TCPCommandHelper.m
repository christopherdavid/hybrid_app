#import "TCPCommandHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "CommandsHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoUser.h"

#define TEMP_TCP_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata/></command></payload></packet>"

#define TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>%d</version><signature>%d</signature></header><payload><request><command>%d</command><requestId>%@</requestId><timeStamp>%@</timeStamp><retryCount>%d</retryCount><responseNeeded>%@</responseNeeded><replyTo>%@</replyTo><distributionMode>%d</distributionMode>%@</request></payload></packet>"



@interface TCPCommandHelper()

@end

@implementation TCPCommandHelper

- (NSData *)getStartRobotCommand {
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_TCP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], COMMAND_START_ROBOT];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}


- (NSData *)getStopRobotCommand {
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_TCP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], COMMAND_STOP_ROBOT];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

- (NSData *)startRobotCommand2WithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_START_ROBOT, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"TCP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}


- (NSData *)stopRobotCommand2WithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_STOP_ROBOT, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"TCP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

- (NSData *)pauseRobotCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_PAUSE_CLEANING, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"TCP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}



- (NSData *)setRobotTimeCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_SET_ROBOT_TIME, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"TCP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

- (NSData *)enableDisableScheduleCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_ENABLE_DISABLE_SCHEDULE, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"TCP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

- (NSData *)sendToBaseCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], COMMAND_SEND_TO_BASE, requestId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper commandRetryCount], [commandHelper commandResponseNeeded], [NeatoUserHelper getNeatoUser].userId, [commandHelper distributionModeForCommandType:@"TCP"], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

@end