#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "GCDAsyncSocket.h"
#import "NeatoServerManager.h"

@protocol TCPConnectionHelperProtocol <NSObject>

- (void)connectedOverTCP:(NSString*)host;
- (void)tcpConnectionDisconnected:(NSError *)error;
- (void)commandSentOverTCP;
- (void)receivedDataOverTCP:(NSData *)data;
- (void)failedToSendCommandOverTCP;


@end

@interface TCPConnectionHelper : NSObject <GCDAsyncSocketDelegate>

- (void)connectToRobotOverTCP:(NeatoRobot *)robot delegate:(id<TCPConnectionHelperProtocol>)delegate;
- (BOOL)sendCommandToRobot:(NSData *)command withTag:(long)tag delegate:(id<TCPConnectionHelperProtocol>)delegate;
- (void)disconnectFromRobot:(NSString *)robotId delegate:(id)delegate;
- (BOOL)isConnected;
- (void)connectToRobotOverTCP2:(NeatoRobot *)robot delegate:(id)delegate;
- (void)sendCommandToRobot2:(NSData *)command withTag:(long)tag requestId:(NSString *)requestId delegate:(id)delegate;

@end
