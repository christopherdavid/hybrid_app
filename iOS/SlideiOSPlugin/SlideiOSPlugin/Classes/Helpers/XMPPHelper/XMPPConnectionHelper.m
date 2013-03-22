#import "XMPPConnectionHelper.h"
#import "LogHelper.h"
#import "XMPPConnection.h"
#import "NeatoUserHelper.h"
#import "NeatoUser.h"

@interface XMPPConnectionHelper()

@property(nonatomic, retain) XMPPConnectionHelper *retained_self;
@property(nonatomic, readwrite) bool needToSendACommand;
@property(nonatomic, retain) NSString *commandToSend;
@property(nonatomic, retain) NSString *sendTo;

-(void) notifyCaller:(SEL) selector;
-(void) notifyCaller:(SEL) selector withObject:(id) object withObject:(id) object1;
@end

@implementation XMPPConnectionHelper
@synthesize retained_self = _retained_self;
@synthesize delegate  = _delegate;
@synthesize needToSendACommand = _needToSendACommand;
@synthesize commandToSend = _commandToSend;
@synthesize sendTo = _sendTo;

// Wrapper to login to XMPP server
-(BOOL) connectJID:(NSString *) jid password:(NSString *) password host:(NSString *) host
{
    debugLog(@"");
    self.retained_self = self;
    [XMPPConnection getSharedXMPPConnection].delegate = self;
    return [[XMPPConnection getSharedXMPPConnection] connectJID:jid password:password withHost:host];
}

-(void) disconnectFromRobot
{
    self.retained_self = self;
    [XMPPConnection getSharedXMPPConnection].delegate = self;
    [[XMPPConnection getSharedXMPPConnection] disconnect];
}

-(BOOL) isConnected
{
    return [[XMPPConnection getSharedXMPPConnection] isConnected];
}

-(void) sendCommandToRobot:(NSString *) to command:(NSString *) command withTag:(long) tag
{
    debugLog(@"");
    self.retained_self = self;
    [XMPPConnection getSharedXMPPConnection].delegate = self;
    if ([self isConnected])
    {
        [[XMPPConnection getSharedXMPPConnection] sendData:command to:to];
    }
    else
    {
        NeatoUser *user = [NeatoUserHelper getNeatoUser];
        if (!user)
        {
            self.needToSendACommand = NO;
            self.retained_self = nil;
            debugLog(@"User not logged in. Will not connect over XMPP!");
            [self notifyCaller:@selector(failedToSendCommandOverXMPP)];
            return;
        }
        self.needToSendACommand = YES;
        self.commandToSend = command;
        self.sendTo = to;
     
        debugLog(@"User not connected over XMPP. Will first connect over XMPP and then try to send the command");
        
        [self connectJID:user.chatId password:user.chatPassword host:NEATO_XMPP_SERVER_ADDRESS];
    }
}

-(void)xmppStream:(XMPPStream *)sender didSendMessage:(XMPPMessage *)message
{
    debugLog(@"");
    [self notifyCaller:@selector(commandSentOverXMPP)];
    if(self.needToSendACommand)
    {
        debugLog(@"Pending XMPP command sent to server.");
    }
    self.needToSendACommand = NO;
    self.retained_self = nil;
}

- (void)xmppStream:(XMPPStream *)sender socketDidConnect:(GCDAsyncSocket *)socket
{
    debugLog(@"");
    
}

- (void)xmppStream:(XMPPStream *)sender willSecureWithSettings:(NSMutableDictionary *)settings
{
	debugLog(@"willSecureWithSettings called");
}

- (void)xmppStream:(XMPPStream *)sender didSendElementWithTag:(id)tag
{
    debugLog(@"didSendElementWithTag called. IM sent!");
    [self notifyCaller:@selector(commandSentOverXMPP)];
    if(self.needToSendACommand)
    {
        debugLog(@"Pending XMPP command sent to server.");
    }
    self.needToSendACommand = NO;
    self.retained_self = nil;
}



- (void)xmppStreamDidSecure:(XMPPStream *)sender
{
	debugLog(@"xmppStreamDidSecure called");
}

- (void)xmppStreamDidConnect:(XMPPStream *)sender
{
	debugLog(@"xmppStreamDidConnect called");

}

- (void)xmppStreamDidAuthenticate:(XMPPStream *)sender
{
    debugLog(@"xmppStreamDidAuthenticate called");
    [self notifyCaller:@selector(didConnectOverXMPP)];
    if (self.needToSendACommand)
    {
        debugLog(@"Connected over XMPP. Now sending pending command.");
        [XMPPConnection getSharedXMPPConnection].delegate = self;
        [[XMPPConnection getSharedXMPPConnection] sendData:self.commandToSend to:self.sendTo];
        self.needToSendACommand = NO;
    }
    else
    {
        self.retained_self = nil;
    }
}

-(void) notifyCaller:(SEL) selector
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector])
        {
            [self.delegate performSelector:selector];
        }
    });
}

-(void) notifyCaller:(SEL) selector withObject:(id) object withObject:(id) object1
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector])
        {
            [self.delegate performSelector:selector withObject:object withObject:object1];
        }
    });
}

- (void)xmppStream:(XMPPStream *)sender didNotAuthenticate:(NSXMLElement *)error
{
	debugLog(@"didNotAuthenticate called");
    [self notifyCaller:@selector(didDisConnectFromXMPP)];
    if (self.needToSendACommand)
    {
        debugLog(@"Failed to connect to XMPP server.Command could not be sent.");
        [self notifyCaller:@selector(failedToSendCommandOverXMPP)];
    }
    self.needToSendACommand = NO;
    self.retained_self = nil;
}

- (BOOL)xmppStream:(XMPPStream *)sender didReceiveIQ:(XMPPIQ *)iq
{
	debugLog(@"didReceiveIQ called. iq = %@", iq);
	
	return NO;
}

- (void)xmppStream:(XMPPStream *)sender didReceiveMessage:(XMPPMessage *)message
{
	debugLog(@"didReceiveMessage called. message = %@", message);
    [self notifyCaller:@selector(commandReceivedOverXMPP:sender:) withObject:message withObject:sender];
    self.retained_self = nil;
}

- (void)xmppStream:(XMPPStream *)sender didReceivePresence:(XMPPPresence *)presence
{
	debugLog(@"didReceivePresence called. presence = %@", presence);
    
}

- (void)xmppStream:(XMPPStream *)sender didReceiveError:(id)error
{
	debugLog(@"didReceiveError called. error = %@", error);
    
}

- (void)xmppStreamDidDisconnect:(XMPPStream *)sender withError:(NSError *)error
{
	debugLog(@"xmppStreamDidDisconnect called. error = %@", error);
    [self notifyCaller:@selector(didDisConnectFromXMPP)];
    if (self.needToSendACommand)
    {
        debugLog(@"XMPP connection closed before a command could be sent.");
        [self notifyCaller:@selector(failedToSendCommandOverXMPP)];
    }
    self.needToSendACommand = NO;
    self.retained_self = nil;
}

@end
