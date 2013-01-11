#import "UDPCommandHelper.h"
#import "AppHelper.h"
#import "NetworkUtils.h"
#import "LogHelper.h"

#define FIND_ROBOTS_COMMAND 1
#define GET_ROBOT_IP_COMMAND 3

#define TEMP_FIND_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata><key_robot_discovery_device_id>%@</key_robot_discovery_device_id></commanddata></command></payload></packet>"

#define TEMP_GET_ROBOT_IP_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata><serial_id>%@</serial_id></commanddata></command></payload></packet>"

@implementation UDPCommandHelper

-(NSData*) getFindRobotsCommand
{
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_FIND_ROBOT_COMMAND_FORMAT, [AppHelper getAppSignature], FIND_ROBOTS_COMMAND, [[[NetworkUtils alloc] init] getMacAddress]];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

-(NSData *) getRobotIPAddressCommand:(NSString *) serialId
{
    debugLog(@"");
    NSString *command = [NSString stringWithFormat:TEMP_GET_ROBOT_IP_COMMAND_FORMAT, [AppHelper getAppSignature], GET_ROBOT_IP_COMMAND, serialId];
    debugLog(@"command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

@end
