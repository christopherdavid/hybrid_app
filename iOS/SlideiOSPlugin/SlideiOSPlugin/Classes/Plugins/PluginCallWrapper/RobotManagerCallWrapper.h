#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "XMPPConnectionHelper.h"
#import "Schedule.h"


@protocol RobotManagerProtocol <NSObject>

- (void)connectedOverTCP:(NSString*) host callbackId:(NSString *)callbackId;
- (void)tcpConnectionDisconnected:(NSError *) error callbackId:(NSString *)callbackId;
- (void)commandSentOverTCP:(NSString *)callbackId;
- (void)receivedDataOverTCP:(NSData *)data callbackId:(NSString *)callbackId;
- (void)didConnectOverXMPP:(NSString *)callbackId;
- (void)didDisConnectFromXMPP:(NSString *)callbackId ;
- (void)commandSentOverXMPP:(NSString *)callbackId;
- (void)commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverXMPP:(NSString *)callbackId;
- (void)failedToSendCommandOverTCP:(NSString *)callbackId;
- (void)failedToConnectToTCP2WithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)connectedOverTCP2:(NSString*)host callbackId:(NSString *)callbackId;
- (void)tcpConnectionDisconnected2:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverTCPWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverXMPP2:(NSString *)callbackId;
- (void)commandSentOverTCP2:(NSString *)callbackId;
- (void)commandSentOverXMPP2:(NSString *)callbackId;
- (void)updatedSchedule:(NSString *)scheduleId callbackId:(NSString *)callbackId;
- (void)updateScheduleError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedtoSendCommandWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)commandSentWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId;
- (void)driveRobotFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)stopRobotDriveSuccededForCallbackId:(NSString *)callbackId;
- (void)stopRobotDriveFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
@end

@interface RobotManagerCallWrapper : NSObject

@property(nonatomic, weak) id delegate;

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId;
- (void)updateScheduleForScheduleId:(NSString *)scheduleId callbackId:(NSString *)callbackId;
- (void)driveRobotWithId:(NSString *)robotId navigationControlId:(NSString *)navigationControlId callbackId:(NSString *)callbackId;
- (void)stopRobotDriveForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendCommandOverTCPToRobotWithId:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId;

@end
