#import <Foundation/Foundation.h>
#import "NeatoRobot.h"

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
- (BOOL)isXMPPDataChangeCommand:(NSString *)xmlCommand;
- (NSDictionary *)parseXMPPDataChangeNotification:(NSString *)xmlCommand;
- (NSString *)commandIdFromXmlCommand:(NSString *)xmlCommand;
- (BOOL)isCommandOfRequestType:(NSString *)xmlCommand;
@end
