#import "TCPSocket.h"
#import "LogHelper.h"

static TCPSocket *sharedInstance = nil;

@interface TCPSocket()

@property(nonatomic, retain) GCDAsyncSocket *tcpScoket;

-(void) initializeTCPSocket;
@end

@implementation TCPSocket
@synthesize tcpScoket = _tcpScoket, delegate = _delegate;

+(TCPSocket *) getSharedTCPSocket
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
            [self initializeTCPSocket];
        }
    }
    return self;
}

-(void) initializeTCPSocket
{
    debugLog(@"");
    self.tcpScoket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)];
}

-(void) beginReadingData
{
    [self.tcpScoket readDataWithTimeout:-1 tag:0];
}

-(BOOL) connectHost:(NSString *) host overPort:(int) port
{
    if (self.tcpScoket && [self.tcpScoket isConnected])
    {
        debugLog(@"Device is already connected over TCP!");
        //[self.tcpScoket disconnect];
        return NO;
    }
    NSError *error = nil;
    bool didConnect = [self.tcpScoket connectToHost:host onPort:port error:&error];
    if (didConnect && error == nil)
    {
        return YES;
    }
    return NO;
}



/**
 * Called when a socket connects and is ready for reading and writing.
 * The host parameter will be an IP address, not a DNS name.
 **/
- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port
{
    debugLog(@"");
    [self.delegate socket:sock didConnectToHost:host port:port];
}

/**
 * Called when a socket has completed reading the requested data into memory.
 * Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
    debugLog(@"");
    [self.delegate socket:sock didReadData:data withTag:tag];
}

/**
 * Called when a socket has completed writing the requested data. Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    debugLog(@"");
    [self.delegate socket:sock didWriteDataWithTag:tag];
}

/**
 * Conditionally called if the read stream closes, but the write stream may still be writeable.
 *
 * This delegate method is only called if autoDisconnectOnClosedReadStream has been set to NO.
 * See the discussion on the autoDisconnectOnClosedReadStream method for more information.
 **/
- (void)socketDidCloseReadStream:(GCDAsyncSocket *)sock
{
    debugLog(@"");
    [self.delegate socketDidCloseReadStream:sock];
    [self.tcpScoket disconnect];
    sharedInstance = nil;
}

-(BOOL) isConnected
{
    return [self.tcpScoket isConnected];
}

-(void) disconnect
{
    debugLog(@"");
    if ([self.tcpScoket isConnected])
    {
        [self.tcpScoket disconnect];
    }
}

-(void) writeData:(NSData *) data withTag:(long) tag
{
    if ([self.tcpScoket isConnected])
    {
        [self.tcpScoket writeData:data withTimeout:-1 tag:tag];
    }
}

/**
 * Called when a socket disconnects with or without error.
 *
 * If you call the disconnect method, and the socket wasn't already disconnected,
 * this delegate method will be called before the disconnect method returns.
 **/
- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err
{
    debugLog(@"");
    [self.delegate socketDidDisconnect:sock withError:err];
    sharedInstance = nil;
}

@end
