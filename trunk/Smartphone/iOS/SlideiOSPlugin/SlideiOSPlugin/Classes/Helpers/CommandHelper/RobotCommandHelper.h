#import <Foundation/Foundation.h>

@protocol RobotCommandHelperProtocol <NSObject>

- (void)failedToSendCommandOverTCPWithError:(NSError *)error;
- (void)commandSentOverTCP2;
- (void)commandSentOverXMPP2;
- (void)failedToSendCommandOverXMPP2;
- (void)cleaningCommandSentWithResult:(NSDictionary *)result;
- (void)failedtoSendCleaningCommandWithError:(NSError *)error;

@end

@interface RobotCommandHelper : NSObject

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate;
- (void)sendCommandOverTCPToRobotWithId:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate;

@end
