#import "TCPCommandHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"

#define TEMP_TCP_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata/></command></payload></packet>"

#define TCP_START_ROBOT_COMMAND 101
#define TCP_STOP_ROBOT_COMMAND 102

@implementation TCPCommandHelper

-(NSData *) getStartRobotCommand
{
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_TCP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], TCP_START_ROBOT_COMMAND];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}


-(NSData *) getStopRobotCommand
{
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_TCP_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], TCP_STOP_ROBOT_COMMAND];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}
@end
