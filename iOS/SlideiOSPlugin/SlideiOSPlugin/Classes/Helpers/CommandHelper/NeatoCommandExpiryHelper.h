#import <Foundation/Foundation.h>
#import "NeatoRobotCommand.h"

@interface NeatoCommandExpiryHelper : NSObject

+ (NeatoCommandExpiryHelper *)expirableCommandHelper;
- (void)startCommandTimerForRobotId:(NSString *)robotId;
- (void)stopCommandTimerForRobotId:(NSString *)robotId;
- (BOOL)isTimerRunningForRobotId:(NSString *)robotId;

@end
