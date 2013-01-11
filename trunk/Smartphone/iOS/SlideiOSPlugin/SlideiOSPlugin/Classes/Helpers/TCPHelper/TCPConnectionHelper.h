#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "GCDAsyncSocket.h"
#import "NeatoServerManager.h"

@protocol TCPConnectionHelperProtocol <NSObject>

-(void) connectedOverTCP:(NSString*) host;
-(void) tcpConnectionDisconnected;
-(void) commandSentOverTCP;
-(void) receivedDataOverTCP:(NSData *)data;
-(void) failedToSendCommandOverTCP;

@end

@interface TCPConnectionHelper : NSObject <GCDAsyncSocketDelegate, NeatoServerManagerProtocol>

-(void) connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id<TCPConnectionHelperProtocol>) delegate;
-(BOOL) sendCommandToRobot:(NSData *) command withTag:(long) tag delegate:(id<TCPConnectionHelperProtocol>) delegate;
-(void) disconnectFromRobot:(NSString *) robotId delegate:(id<TCPConnectionHelperProtocol>) delegate;
-(BOOL) isConnected;


@end
