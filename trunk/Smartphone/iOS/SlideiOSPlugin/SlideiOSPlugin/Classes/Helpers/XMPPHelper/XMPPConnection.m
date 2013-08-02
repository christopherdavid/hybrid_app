#import "XMPPConnection.h"
#import "LogHelper.h"
#import "CommandsHelper.h"

static XMPPConnection *sharedInstance;

@interface XMPPConnection()
{

}
@property(nonatomic, retain) NSString *jid;
@property(nonatomic, retain) NSString *password;
@property(nonatomic, retain) XMPPStream *xmppStream;

-(void) initializeConnection;
@end

@implementation XMPPConnection
@synthesize xmppStream = _xmppStream, jid = _jid, password = _password, delegate= _delegate;

+(XMPPConnection *) getSharedXMPPConnection
{
    @synchronized(self)
    {
        if(sharedInstance == nil) {
            sharedInstance =  [[self alloc] init];
        }
    }
    return sharedInstance;
}

-(id) init
{
    @synchronized(self)
    {
        if(self = [super init])
        {
            // Initialization code here.
            [self initializeConnection];
        }
    }
    return self;
}

// create xmpp stream and set yourself as delegate
-(void) initializeConnection
{
    debugLog(@"");
    self.xmppStream = [[XMPPStream alloc] init];
	
    #if !TARGET_IPHONE_SIMULATOR
	{
		self.xmppStream.enableBackgroundingOnSocket = YES;
	}
    #endif
	
	// Add ourself as a delegate to anything we may be interested in
	[self.xmppStream addDelegate:self delegateQueue:dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)];
}

// sends data to xmpp server, after enclosing it in XMPP message element
-(void)sendData:(NSString *) command to:(NSString *) to
{
    debugLog(@"command = %@, sending to  = %@", command, to);
    
    
    NSXMLElement *body = [NSXMLElement elementWithName:@"body"];
    [body setStringValue:command];
    
    NSXMLElement *messageElement = [NSXMLElement elementWithName:@"message"];
    [messageElement addAttributeWithName:@"type" stringValue:@"chat"];
    [messageElement addAttributeWithName:@"to" stringValue:to];
    [messageElement addChild:body];
    
    // Send data to XMPP server
    [self.xmppStream sendElement:messageElement];
}


// Connects or logs in a user
-(BOOL) connectJID:(NSString *)jid password:(NSString *) password withHost:(NSString *) host;
{
    if (![self.xmppStream isDisconnected]) {
        return YES;
    }
    
    self.jid = jid;
    self.password = password;
    
    if (jid == nil || [jid length] == 0 || password == nil || [password length] == 0 || host == nil || [host length] == 0) {
        return NO;
    }
    
    [self.xmppStream setMyJID:[XMPPJID jidWithString:jid]];
    [self.xmppStream setHostName:host];
    
    NSError *error = nil;
    if (![self.xmppStream connect:&error])
    {
        return NO;
    }
    
    return error == nil;
}

- (void)xmppStream:(XMPPStream *)sender socketDidConnect:(GCDAsyncSocket *)socket
{
    debugLog(@"socketDidConnect called");
    [self.delegate xmppStream:sender socketDidConnect:socket];
}

- (void)xmppStream:(XMPPStream *)sender willSecureWithSettings:(NSMutableDictionary *)settings
{
	debugLog(@"willSecureWithSettings called");
}

- (void)xmppStream:(XMPPStream *)sender didSendElementWithTag:(id)tag
{
    debugLog(@"didSendElementWithTag called. IM sent!");
    //[self.delegate xmppStream:sender didSendElementWithTag:tag];
}

-(void) disconnect
{
    [self.xmppStream disconnect];
}

-(BOOL) isConnected
{
    return [self.xmppStream isConnected];
}


- (void)xmppStream:(XMPPStream *)sender didSendMessage:(XMPPMessage *)message
{
    debugLog(@"");
    [self.delegate xmppStream:sender didSendMessage:message];
}

- (void)xmppStreamDidSecure:(XMPPStream *)sender
{
	debugLog(@"xmppStreamDidSecure called");
}

- (void)xmppStreamDidConnect:(XMPPStream *)sender
{
	debugLog(@"xmppStreamDidConnect called");
	
	NSError *error = nil;
	
    // Authenticate the user
	if (![[self xmppStream] authenticateWithPassword:self.password error:&error])
	{
        NSLog(@"Error authenticating: %@", error);
	}
}

- (void)xmppStreamDidAuthenticate:(XMPPStream *)sender
{
    debugLog(@"xmppStreamDidAuthenticate called");
    [self.delegate xmppStreamDidAuthenticate:sender];
}

- (void)xmppStream:(XMPPStream *)sender didNotAuthenticate:(NSXMLElement *)error
{
	debugLog(@"didNotAuthenticate called");
    [self.delegate xmppStream:sender didNotAuthenticate:error];
}

- (BOOL)xmppStream:(XMPPStream *)sender didReceiveIQ:(XMPPIQ *)iq
{
	debugLog(@"didReceiveIQ called. iq = %@", iq);
	
	return NO;
}

- (void)xmppStream:(XMPPStream *)sender didReceiveMessage:(XMPPMessage *)message {
    debugLog(@"Sender : %@", sender);
    debugLog(@"didReceiveMessage called. message = %@", [message stringValue]);
    [self.delegate xmppStream:sender didReceiveMessage:message];
    // Notify if we have recevied 'data changed' from remote.
    [self performSelectorOnMainThread:@selector(postNotificationIfRobotDataChangedWithMessage:) withObject:message waitUntilDone:NO];
}

- (void)xmppStream:(XMPPStream *)sender didReceivePresence:(XMPPPresence *)presence
{
	debugLog(@"didReceivePresence called. presence = %@", presence);
    
}

- (void)xmppStream:(XMPPStream *)sender didReceiveError:(id)error
{
	debugLog(@"didReceiveError called. error = %@", error);
    [self.delegate xmppStream:sender didReceiveError:error];
}

- (void)xmppStreamDidDisconnect:(XMPPStream *)sender withError:(NSError *)error
{
	debugLog(@"xmppStreamDidDisconnect called. error = %@", error);
    [self.delegate xmppStreamDidDisconnect:sender withError:error];
}

- (void)postNotificationIfRobotDataChangedWithMessage:(XMPPMessage *)message {
    CommandsHelper *commandHelper = [[CommandsHelper alloc] init];
    NSString *messageBody = [[message elementForName:@"body"] stringValue];
    // Check if command is of request type.
    if ([commandHelper isCommandOfRequestType:messageBody]) {
        if ([commandHelper isXMPPDataChangeCommand:messageBody]) {
            NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
            [userInfo setObject:messageBody forKey:KEY_XMPP_MESSAGE];
            [userInfo setObject:[message fromStr] forKey:KEY_CHAT_ID];
            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_XMPP_DATA_CHANGE object:nil userInfo:userInfo];
        }
    }
}

@end
