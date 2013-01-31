#import "UDPCommandHelper.h"
#import "AppHelper.h"
#import "NetworkUtils.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "CommandTracker.h"


#define TEMP_FIND_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata><key_robot_discovery_device_id>%@</key_robot_discovery_device_id></commanddata></command></payload></packet>"

#define TEMP_FIND_ROBOT_COMMAND_NEW_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><discovery_packet><header><version>1</version><signature>%d</signature></header><payload><request><command>%d</command><requestId>%@</requestId><userId>%@</userId><params/></request></payload></discovery_packet>"


#define TEMP_GET_ROBOT_IP_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata><serial_id>%@</serial_id></commanddata></command></payload></packet>"

#define TEMP_GET_ROBOT_IP_COMMAND_NEW_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><discovery_packet><header><version>1</version><signature>%d</signature></header><payload><request><command>%d</command><requestId>%@</requestId><userId>%@</userId><robotId>%@</robotId><params/></request></payload></discovery_packet>"


@implementation UDPCommandHelper

-(NSString*) getFindRobotsCommand:(NSString *) requestId
{
    debugLog(@"");
    //NSString *uniqueString = [AppHelper generateUniqueString];
    NSString *command = [NSString stringWithFormat:TEMP_FIND_ROBOT_COMMAND_NEW_FORMAT, [AppHelper getAppSignature], FIND_ROBOTS_COMMAND, requestId, [NeatoUserHelper getNeatoUser].userId];
    debugLog(@"command = %@", command);
    return command;
}

-(NSString *) getRobotIPAddressCommandRequestId:(NSString *)requestId robotId:(NSString *)robotId
{
    debugLog(@"");
    //NSString *uniqueString = [AppHelper generateUniqueString];
    NSString *command = [NSString stringWithFormat:TEMP_GET_ROBOT_IP_COMMAND_NEW_FORMAT, [AppHelper getAppSignature], GET_ROBOT_IP_COMMAND, requestId, [NeatoUserHelper getNeatoUser].userId, robotId];

    
    debugLog(@"command = %@", command);
    return command;
}

@end
