#import <Foundation/Foundation.h>

@protocol CommandProtocol <NSObject>

@required
-(id) getStartRobotCommand;
-(id) getStopRobotCommand;

@end
