#import "UDPSocket.h"
#import "LogHelper.h"


// Internal class to wrap packet data
@interface UDPPacket : NSObject

@property(nonatomic, retain) NSData *commandData;
@property(nonatomic, retain) NSString *remoteIPAddress;
@property(nonatomic, readwrite) int remotePort;
@property(nonatomic, readwrite) long tag;

@end

@implementation UDPPacket
@synthesize commandData = _commandData;
@synthesize remoteIPAddress = _remoteIPAddress;
@synthesize remotePort = _remotePort;
@synthesize tag = _tag;

@end


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
    // || [self.udpSocket isClosed]
    if (!self.udpSocket)
    {
        debugLog(@"Binding socket");
        self.udpSocket = [[GCDAsyncUdpSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)];
        
        if(self.udpSocket)
        {
            return YES;
        }
        else
        {
            return NO;
        }
    }
    else
    {
        return YES;
    }
}
-(BOOL) bindOnPort:(int)bindPort
{
    debugLog(@"");
    if ([self prepareUDPSocket])
    {
        NSError *bindError = nil;
        [self.udpSocket bindToPort:bindPort error:&bindError];
        return (bindError == nil);
    }
    else
    {
        debugLog(@"UDP socket is not prepared");
        return NO;
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

// Sends the data over UDP socket
// To make up for loss of UDP packets over the network, we send every command three
// times, at a inteval of time
-(void) sendData:(NSData *)data host:(NSString *) host port:(int) port tag:(long) tag
{
    debugLog(@"");
    if ([self prepareUDPSocket])
    {
        UDPPacket *packet = [[UDPPacket alloc] init];
        packet.commandData = data;
        packet.remoteIPAddress = host;
        packet.remotePort = port;
        packet.tag = tag;
        
        debugLog(@"Will schedule timer");
        // Schedule timer to send the data three times at a interval of 5 mili seconds
        for (int count = 0; count < 3; count++) {
            float trigger = ((count + 1) * 0.005);
            debugLog(@"Scheduling a timer for : %f", trigger);
            [NSTimer scheduledTimerWithTimeInterval:trigger target:self selector:@selector(sendUDPPacket:) userInfo:packet repeats:NO];
        }
       
    }
    else
    {
        debugLog(@"UDP socket is not open or not bound to port!!");
    }
}


-(void) sendUDPPacket:(NSTimer *) timer
{
    debugLog(@"");
    UDPPacket *packet = timer.userInfo;
    [self.udpSocket sendData:packet.commandData toHost:packet.remoteIPAddress port:packet.remotePort withTimeout:-1 tag:packet.tag];
}

-(BOOL) beginReceiving
{
    debugLog(@"");
    if ([self prepareUDPSocket])
    {
        NSError *error = nil;
        [self.udpSocket beginReceiving:&error];
        if ((error != nil))
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
