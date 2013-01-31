#import <Foundation/Foundation.h>
#import "NeatoRobot.h"

@interface CommandsHelper : NSObject

-(BOOL) isResponseToFindRobots:(NSString *) xmlCommand;
-(NeatoRobot *) getRemoteRobot:(NSString *) xmlCommand;
-(BOOL) isResponseToGetRobotIP:(NSString *) xml;
-(void) removeCommandFromTracker:(NSString *) xmlCommand;

@end
