#import <Foundation/Foundation.h>
#import "CommandProtocol.h"

@interface TCPCommandHelper : NSObject <CommandProtocol>

-(NSData *) getStartRobotCommand;
-(NSData *) getStopRobotCommand;

@end
