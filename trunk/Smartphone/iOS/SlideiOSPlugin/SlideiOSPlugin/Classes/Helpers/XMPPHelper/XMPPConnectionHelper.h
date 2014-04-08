#import <Foundation/Foundation.h>
#import "XMPP.h"

@protocol XMPPConnectionHelperProtocol <NSObject>

- (void)didConnectOverXMPP;
- (void)didDisConnectFromXMPP;
- (void)commandSentOverXMPP;
- (void)failedToSendCommandOverXMPP;
- (void)commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender;
- (void)xmppLoginfailedWithError:(NSError *)error;

@end

@interface XMPPConnectionHelper : NSObject 

@property (nonatomic, weak) id delegate;

-(BOOL) connectJID:(NSString *) jid password:(NSString *) password host:(NSString *) host;
-(void) disconnectFromRobot;
-(BOOL) isConnected;
-(void) sendCommandToRobot:(NSString *) to command:(NSString *) command withTag:(long) tag;

@end
