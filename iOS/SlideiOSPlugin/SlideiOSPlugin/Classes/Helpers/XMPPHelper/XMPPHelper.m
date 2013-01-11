#import "XMPPHelper.h"
#import "XMPPConnectionHelper.h"
#import "LogHelper.h"
#import "CommandsHelper.h"
#import "XMPPCommandHelper.h"

#define START_ROBOT_COMMAND_TAG 7001
#define STOP_ROBOT_COMMAND_TAG 7002

@interface XMPPHelper()

@end

@implementation XMPPHelper


-(void) startCleaning:(NSString *) toJID delegate:(id) delegate;
{
    debugLog(@"");
    XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
    helper.delegate = delegate;
    [helper sendCommandToRobot:toJID command:[[[XMPPCommandHelper alloc] init] getStartRobotCommand] withTag:START_ROBOT_COMMAND_TAG];
}

-(void) stopCleaning:(NSString *) toJID delegate:(id) delegate;
{
    debugLog(@"");
    XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
    helper.delegate = delegate;
    [helper sendCommandToRobot:toJID command:[[[XMPPCommandHelper alloc] init] getStopRobotCommand] withTag:STOP_ROBOT_COMMAND_TAG];
}

@end
