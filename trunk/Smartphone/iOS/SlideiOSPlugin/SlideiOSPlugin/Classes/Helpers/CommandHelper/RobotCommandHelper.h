#import <Foundation/Foundation.h>

@protocol RobotCommandHelperProtocol <NSObject>

- (void)failedToSendCommandOverTCPWithError:(NSError *)error;
- (void)commandSentOverTCP2;
- (void)commandSentOverXMPP2;
- (void)failedToSendCommandOverXMPP2;

@end

@interface RobotCommandHelper : NSObject

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate;

@end
