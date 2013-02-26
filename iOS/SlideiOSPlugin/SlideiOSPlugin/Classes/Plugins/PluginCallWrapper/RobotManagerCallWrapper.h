#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "NeatoRobotManager.h"
#import "XMPPConnectionHelper.h"

@protocol RobotManagerProtocol <NSObject>

- (void)foundRobotsNearby:(id)value callbackId:(NSString *)callbackId;
- (void)gotRemoteRobotIP:(id)value callbackId:(NSString *)callbackId;
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
- (void)getAtlasDataFailed:(NSError *)error callbackId:(NSString *)callbackId;
- (void)gotAtlasData:(NeatoRobotAtlas *)robotAtlas  callbackId:(NSString *)callbackId;
- (void)gotAtlasGridMetadata:(AtlasGridMetadata *) atlasGridMetadata callbackId:(NSString *) callbackId;
- (void)getAtlasGridMetadataFailed:(NSError *) error callbackId:(NSString *)callbackId;
- (void)atlasMetadataUpdated:(NeatoRobotAtlas *) robotAtlas callbackId:(NSString *)callbackId;
- (void)failedToUpdateAtlasMetadataWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToUpdateRobotNameWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)gotRobotDetails:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId;
- (void)failedToGetRobotDetailsWihError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)failedToConnectToTCP2WithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)connectedOverTCP2:(NSString*)host callbackId:(NSString *)callbackId;
- (void)tcpConnectionDisconnected2:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverTCPWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverXMPP2:(NSString *)callbackId;
- (void)commandSentOverTCP2:(NSString *)callbackId;
- (void)commandSentOverXMPP2:(NSString *)callbackId;
@end

@interface RobotManagerCallWrapper : NSObject <TCPConnectionHelperProtocol, XMPPConnectionHelperProtocol>

@property(nonatomic, weak) id delegate;

- (void)findRobotsNearBy:(NSString *)callbackId;
- (void)connectToRobotOverTCP:(NeatoRobot *)robot delegate:(id)delegate callbackId:(NSString *) callbackId;
- (void)diconnectRobotFromTCP:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendStartCleaningTo:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendStopCleaningTo:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendCommandToRobot:(NSString *)robotId commandId:(NSString *)commandId callbackId:(NSString *)callbackId;
- (void)tryDirectConnection:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getRobotAtlasMetadataForRobotId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getAtlasGridMetadata:(NSString *)robotId gridId:(NSString *)gridId  callbackId:(NSString *)callbackId;
- (void)updateRobotAtlasData:(NeatoRobotAtlas *)robotAtlas callbackId:(NSString *)callbackId;
- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getDetailsForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)onlineStatusForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)tryDirectConnection2:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId;
@end
