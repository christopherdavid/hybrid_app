#import "XMPPCommandHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"

#define XMPP_START_ROBOT_COMMAND 101
#define XMPP_STOP_ROBOT_COMMAND 102

#define TEMP_XMPP_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><command><commandid>%d</commandid><commanddata/></command></packet>"

@implementation XMPPCommandHelper


-(NSString *) getStartRobotCommand
{
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], XMPP_START_ROBOT_COMMAND];
    debugLog(@"command = %@", command);
    return command;
}

-(NSString *) getStopRobotCommand
{
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_XMPP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], XMPP_STOP_ROBOT_COMMAND];
    debugLog(@"command = %@", command);
    return command;
}
@end
