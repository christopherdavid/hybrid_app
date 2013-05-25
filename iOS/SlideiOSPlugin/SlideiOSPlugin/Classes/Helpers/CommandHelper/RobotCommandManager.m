#import "RobotCommandManager.h"

@implementation RobotCommandManager

@synthesize delegate = _delegate;

- (void)turnVacuumOnOff:(int)on forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email withParams:(NSDictionary *)params commandId:(NSString *)commandId {
    [NeatoRobotManager sendCommandToRobot2:robotId commandId:commandId params:params delegate:self.delegate];
}

@end
