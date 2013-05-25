#import <Foundation/Foundation.h>

@protocol CommandProtocol <NSObject>

@required
- (id)getStartRobotCommand;
- (id)getStopRobotCommand;

- (id)getRobotCommand2WithId:(int)commandId withParams:(NSDictionary *)params andRequestId:(NSString *)requestId;

@end
