#import <Foundation/Foundation.h>
#import "NeatoRobot.h"

#define COMMAND_START_ROBOT     		101
#define COMMAND_STOP_ROBOT      		102
#define COMMAND_PAUSE_CLEANING  		107
#define COMMAND_SET_ROBOT_TIME  		110
#define COMMAND_ENABLE_DISABLE_SCHEDULE 108
#define COMMAND_SEND_TO_BASE 			104

@interface CommandsHelper : NSObject

- (BOOL)isResponseToFindRobots:(NSString *) xmlCommand;
- (NeatoRobot *)getRemoteRobot:(NSString *) xmlCommand;
- (BOOL)isResponseToGetRobotIP:(NSString *) xml;
- (void)removeCommandFromTracker:(NSString *) xmlCommand;
- (NSInteger)versionForCommand;
- (NSInteger)commandRetryCount;
- (NSString *)commandResponseNeeded;
- (NSInteger)distributionModeForCommandType:(NSString *)connectionType;
- (NSString *)generateXMLForParams:(NSDictionary *)params;
@end
