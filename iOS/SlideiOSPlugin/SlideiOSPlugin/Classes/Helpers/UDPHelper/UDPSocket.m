#import "UDPSocket.h"
#import "LogHelper.h"

#define UDP_SMART_APPS_BROADCAST_PORT 12346
static UDPSocket *sharedInstance = nil;
@interface UDPSocket()

@property(nonatomic, retain) GCDAsyncUdpSocket *udpSocket;
@end

@implementation UDPSocket
@synthesize delegate = _delegate;

+(UDPSocket *) getSharedUDPSocket
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
        }
    }
    
    return self;
    
}

-(BOOL) enableBroadcast:(BOOL) enable
{
    if ([self prepareUDPSocket])
    {
        NSError *error = nil;
        [self.udpSocket enableBroadcast:enable error:&error];
        if (!(error == nil))
        {
            debugLog(@"Cound not enable broadcast on UDP socket.");
            return NO;
        }
        return YES;
    }
    else
    {
        return NO;
        debugLog(@"UDP socket is not open or not bound to port!!");
    }
}


-(BOOL) prepareUDPSocket
{
    debugLog(@"");
    if (!self.udpSocket || [self.udpSocket isClosed])
    {
        debugLog(@"Binding socket");
        self.udpSocket = [[GCDAsyncUdpSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)];
        NSError *bindError = nil;
        [self.udpSocket bindToPort:UDP_SMART_APPS_BROADCAST_PORT error:&bindError];
        return (bindError == nil);
    }
    else
    {
        return YES;
    }
}

-(BOOL) receiveOnce
{
    if ([self prepareUDPSocket])
    {
        NSError *error = nil;
        [self.udpSocket receiveOnce:&error];
        if (!(error == nil))
        {
            debugLog(@"Cound not start receive on socket.");
            return NO;
        }
        return YES;
    }
    else
    {
        debugLog(@"UDP socket is not open or not bound to port!!");
        return NO;
    }
}

-(void) sendData:(NSData *)data host:(NSString *) host port:(int) port tag:(long) tag
{
    debugLog(@"");
    if ([self prepareUDPSocket])
    {
        [self.udpSocket sendData:data toHost:host port:port withTimeout:-1 tag:tag];
    }
    else
    {
        debugLog(@"UDP socket is not open or not bound to port!!");
    }
}


-(BOOL) beginReceiving
{
    if ([self prepareUDPSocket])
    {
        NSError *error = nil;
        [self.udpSocket beginReceiving:&error];
        if (!(error == nil))
        {
            debugLog(@"Cound not start receive on socket.");
            return NO;
        }
        return YES;
    }
    else
    {
        debugLog(@"UDP socket is not open or not bound to port!!");
        return NO;
    }
}

-(void) closeSocket
{
    [self.udpSocket closeAfterSending];
    sharedInstance = nil;
}

/**
 * Called when the datagram with the given tag has been sent.
 **/
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didSendDataWithTag:(long)tag
{
    debugLog(@"");
    [self.delegate udpSocket:sock didSendDataWithTag:tag];
}

/**
 * Called if an error occurs while trying to send a datagram.
 * This could be due to a timeout, or something more serious such as the data being too large to fit in a sigle packet.
 **/
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didNotSendDataWithTag:(long)tag dueToError:(NSError *)error
{
    [self.delegate udpSocket:sock didNotSendDataWithTag:tag dueToError:error];
}

/**
 * Called when the socket has received the requested datagram.
 **/
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didReceiveData:(NSData *)data
      fromAddress:(NSData *)address
withFilterContext:(id)filterContext
{
    debugLog(@"");
    [self.delegate udpSocket:sock didReceiveData:data fromAddress:address withFilterContext:filterContext];
}

/**
 * Called when the socket is closed.
 **/
- (void)udpSocketDidClose:(GCDAsyncUdpSocket *)sock withError:(NSError *)error
{
    [self.delegate udpSocketDidClose:sock withError:error];
    sharedInstance = nil;
}

@end
