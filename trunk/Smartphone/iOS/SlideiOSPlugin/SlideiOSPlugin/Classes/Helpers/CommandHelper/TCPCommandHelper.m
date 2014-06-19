#import "TCPCommandHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "CommandsHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoUser.h"

#define TEMP_TCP_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>1</version><signature>%d</signature></header><payload><command><commandid>%d</commandid><commanddata/></command></payload></packet>"

#define TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><packet><header><version>%d</version><signature>%d</signature></header><payload><request><command>%d</command><timeStamp>%@</timeStamp>%@</request></payload></packet>"



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


- (NSData *)getRobotCommand2WithId:(NSInteger)commandId withParams:(NSDictionary *)params andRequestId:(NSString *)requestId {
    debugLog(@"");
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    
    // Add 'secret key' in command params.
    NSString *secretKey = [AppHelper directConnectionScretKey];
    NSMutableDictionary *newParams = [params mutableCopy];
    if (secretKey) {
        [newParams setObject:secretKey forKey:KEY_SECURE_PASS_KEY];
    }
    
    NSString *command = [NSString stringWithFormat:TEMP_TCP_NEW_ROBOT_COMMAND_FORMAT, [commandHelper versionForCommand], [AppHelper getAppSignature], commandId, [[NSNumber numberWithDouble:[AppHelper currentTimeStamp]] stringValue], [commandHelper generateXMLForParams:newParams]];
    debugLog(@"TCP command = %@", command);
    return [command dataUsingEncoding:NSUTF8StringEncoding];
}

@end
