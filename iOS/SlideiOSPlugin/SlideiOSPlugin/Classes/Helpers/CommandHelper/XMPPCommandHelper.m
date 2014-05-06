#import "XMPPCommandHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "CommandsHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoUser.h"


#define TEMP_XMPP_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><command><commandid>%d</commandid><commanddata/></command></packet>"

#define TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>%d</version><signature>%d</signature></header><payload><request><command>%d</command><timeStamp>%@</timeStamp>%@</request></payload></packet>"


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

- (NSString *)getRobotCommand2WithId:(NSInteger)commandId withParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], commandId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper generateXMLForParams:params]];
    debugLog(@"command = %@", command);
    return command;
}



@end
