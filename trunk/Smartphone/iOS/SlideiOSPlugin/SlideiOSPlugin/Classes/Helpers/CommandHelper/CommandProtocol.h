#import <Foundation/Foundation.h>

@protocol CommandProtocol <NSObject>

@required
- (id)getStartRobotCommand;
- (id)getStopRobotCommand;

- (id)startRobotCommand2WithParams:(NSDictionary *)params andRequestId:(NSString *)requestId;
- (id)stopRobotCommand2WithParams:(NSDictionary *)params andRequestId:(NSString *)requestId;
- (id)pauseRobotCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId;
- (id)setRobotTimeCommandWithParams:(NSDictionary *)params andRequestId:(NSString *)requestId;

@end
