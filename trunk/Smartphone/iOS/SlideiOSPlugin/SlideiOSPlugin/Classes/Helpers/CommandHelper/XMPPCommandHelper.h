#import <Foundation/Foundation.h>
#import "CommandProtocol.h"
#import "XMPP.h"

@interface XMPPCommandHelper : NSObject <CommandProtocol>

-(NSString *) getStartRobotCommand;
-(NSString *) getStopRobotCommand;

@end
