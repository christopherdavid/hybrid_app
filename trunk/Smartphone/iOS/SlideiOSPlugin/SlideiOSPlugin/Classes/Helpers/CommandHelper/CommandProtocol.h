#import <Foundation/Foundation.h>

@protocol CommandProtocol <NSObject>

@required
- (id)getStartRobotCommand;
- (id)getStopRobotCommand;

- (id)getRobotCommand2WithId:(NSInteger)commandId withParams:(NSDictionary *)params andRequestId:(NSString *)requestId;

@end
