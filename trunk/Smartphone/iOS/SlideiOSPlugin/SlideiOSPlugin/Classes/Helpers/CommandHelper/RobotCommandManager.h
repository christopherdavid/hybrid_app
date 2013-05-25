#import <Foundation/Foundation.h>
#import "NeatoRobotManager.h"

@interface RobotCommandManager : NSObject

@property(nonatomic, weak) id delegate;

- (void)turnVacuumOnOff:(int)on forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email withParams:(NSDictionary *)params commandId:(NSString *)commandId;

@end
