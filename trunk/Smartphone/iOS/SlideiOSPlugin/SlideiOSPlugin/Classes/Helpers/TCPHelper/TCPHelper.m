#import "TCPHelper.h"
#import "LogHelper.h"
#import "TCPCommandHelper.h"


#define START_ROBOT_COMMAND_TAG 6001
#define STOP_ROBOT_COMMAND_TAG 6002

@implementation TCPHelper

-(void) startCleaning:(id<TCPConnectionHelperProtocol>) delegate
{
    debugLog(@"");
    TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
    [helper sendCommandToRobot:[[[TCPCommandHelper alloc] init] getStartRobotCommand] withTag:START_ROBOT_COMMAND_TAG delegate:delegate];
}


-(void) stopCleaning:(id<TCPConnectionHelperProtocol>) delegate
{
    debugLog(@"");
    TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
    [helper sendCommandToRobot:[[[TCPCommandHelper alloc] init] getStopRobotCommand] withTag:STOP_ROBOT_COMMAND_TAG delegate:delegate];
}
@end
