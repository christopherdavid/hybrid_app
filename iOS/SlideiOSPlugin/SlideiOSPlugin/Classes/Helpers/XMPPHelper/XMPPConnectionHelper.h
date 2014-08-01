#import <Foundation/Foundation.h>
#import "XMPP.h"

@class NeatoRobotCommand;

@protocol XMPPConnectionHelperProtocol <NSObject>

- (void)didConnectOverXMPP;
- (void)didDisConnectFromXMPP;
- (void)commandSentOverXMPP:(NeatoRobotCommand *)robotCommand;
- (void)failedToSendCommandOverXMPP;
- (void)commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender;
- (void)xmppLoginfailedWithError:(NSError *)error;

@end

@interface XMPPConnectionHelper : NSObject
@property (nonatomic, weak) id delegate;
@property (nonatomic, strong) NeatoRobotCommand *robotCommand;

-(BOOL) connectJID:(NSString *) jid password:(NSString *) password host:(NSString *) host;
-(void) disconnectFromRobot;
-(BOOL) isConnected;
-(void) sendCommandToRobot:(NSString *) to command:(NSString *) command withTag:(long) tag;

@end
