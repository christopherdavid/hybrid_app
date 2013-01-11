#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "NeatoRobotManager.h"
#import "XMPPConnectionHelper.h"

@protocol RobotManagerProtocol <NSObject>

-(void) foundRobotsNearby:(id) value callbackId:(NSString *) callbackId;
-(void) gotRemoteRobotIP:(id) value callbackId:(NSString *) callbackId;
-(void) connectedOverTCP:(NSString*) host callbackId:(NSString *) callbackId;
-(void) tcpConnectionDisconnected:(NSString *) callbackId;
-(void) commandSentOverTCP :(NSString *) callbackId;
-(void) receivedDataOverTCP:(NSData *)data callbackId:(NSString *) callbackId;
-(void) didConnectOverXMPP :(NSString *) callbackId;
-(void) didDisConnectFromXMPP:(NSString *) callbackId ;
-(void) commandSentOverXMPP:(NSString *) callbackId;
-(void) commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender callbackId:(NSString *) callbackId;
-(void) failedToSendCommandOverXMPP:(NSString *) callbackId;
-(void) failedToSendCommandOverTCP:(NSString *)callbackId;
@end

@interface RobotManagerCallWrapper : NSObject <TCPConnectionHelperProtocol, XMPPConnectionHelperProtocol>

@property(nonatomic, weak) id<RobotManagerProtocol> delegate;

-(void) findRobotsNearBy:(NSString *) callbackId;
//-(void) getRobotInfoBySerialId:(NSString *) serialId callbackId:(NSString *) callbackId;
-(void) connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id) delegate callbackId:(NSString *) callbackId;
-(void) diconnectRobotFromTCP:(NSString *) robotId callbackId:(NSString *) callbackId;
-(void) sendStartCleaningTo:(NSString *) robotId callbackId:(NSString *) callbackId;
-(void) sendStopCleaningTo:(NSString *) robotId callbackId:(NSString *) callbackId;
-(void) sendCommandToRobot:(NSString *) robotId commandId:(NSString *) commandId callbackId:(NSString *) callbackId;
-(void) tryDirectConnection:(NSString *) robotId callbackId:(NSString *) callbackId;
@end
