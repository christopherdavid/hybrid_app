#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "GCDAsyncSocket.h"
#import "NeatoServerManager.h"

@protocol TCPConnectionHelperProtocol <NSObject>

- (void)connectedOverTCP:(NSString*)host toRobotWithId:(NSString *)robotId;
- (void)tcpConnectionDisconnectedWithError:(NSError *)error forRobot:(NeatoRobot *)neatoRobot forcedDisconnected:(BOOL)forcedDisconneted;
- (void)commandSentOverTCP;
- (void)receivedDataOverTCP:(NSData *)data;
- (void)failedToSendCommandOverTCP;
- (void)failedToFormTCPConnectionForRobotId:(NSString *)robotId;


@end

@interface TCPConnectionHelper : NSObject <GCDAsyncSocketDelegate>

+ (id)sharedTCPConnectionHelper;

- (BOOL)sendCommandToRobot:(NSData *)command withTag:(long)tag delegate:(id<TCPConnectionHelperProtocol>)delegate;
- (void)disconnectFromRobot:(NSString *)robotId delegate:(id)delegate;
- (BOOL)isConnected;
- (void)connectToRobotOverTCP2:(NeatoRobot *)robot delegate:(id)delegate;
- (void)sendCommandToRobot2:(NSData *)command withTag:(long)tag requestId:(NSString *)requestId delegate:(id)delegate;
- (BOOL)isRobotConnectedOverTCP:(NSString *)robotId;
@end
