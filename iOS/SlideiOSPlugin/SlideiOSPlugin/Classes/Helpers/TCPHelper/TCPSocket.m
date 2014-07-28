#import "TCPSocket.h"
#import "LogHelper.h"

#define TCP_SOCKET_CONNECTION_TIMEOUT  15

static TCPSocket *sharedInstance = nil;

@interface TCPSocket()

@property(nonatomic, retain) GCDAsyncSocket *tcpScoket;
@property(nonatomic, strong) NSString *connectedRobotId;

-(void) initializeTCPSocket;
@end

@implementation TCPSocket
@synthesize tcpScoket = _tcpScoket, delegate = _delegate, connectedRobotId = _connectedRobotId;

+ (TCPSocket *)getSharedTCPSocket {
    @synchronized(self) {
        if(sharedInstance == nil) {
            sharedInstance =  [[self alloc] init];
        }
    }
    return sharedInstance;
}

- (id)init {
    @synchronized(self) {
        if(self = [super init]) {
            // Initialization code here.
            [self initializeTCPSocket];
        }
    }
    return self;
}

- (void)initializeTCPSocket {
    debugLog(@"");
    self.tcpScoket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)];
}

- (void)beginReadingData {
    [self.tcpScoket readDataWithTimeout:-1 tag:0];
}

- (BOOL)connectToRobotWithId:(NSString *)robotId host:(NSString *)host overPort:(int)port {
    debugLog(@"");
    if (self.tcpScoket && [self.tcpScoket isConnected]) {
        debugLog(@"Device is already connected over TCP!");
        // TODO: check if the two devices are same
        // If the IP of both current connected and new request are same
        // we should return connection success straightway
        [self.tcpScoket setDelegate:nil];
        [self.tcpScoket disconnect];
        self.tcpScoket = nil;
        [self initializeTCPSocket];
    }
    debugLog(@"Connecting to IP : %@, over Port : %d forRobotId: %@", host, port, robotId);
    NSError *error = nil;
    bool didConnect = [self.tcpScoket connectToHost:host onPort:port withTimeout:TCP_SOCKET_CONNECTION_TIMEOUT error:&error];
    if (didConnect && error == nil) {
        // If TCP is successfully connected save robotId.
         self.connectedRobotId = robotId;
        return YES;
    }
    return NO;
}



/**
 * Called when a socket connects and is ready for reading and writing.
 * The host parameter will be an IP address, not a DNS name.
 **/
- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate socket:sock didConnectToHost:host port:port];
    });
    // Always keep a read in the queue.
    [self beginReadingData];
}

/**
 * Called when a socket has completed reading the requested data into memory.
 * Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate socket:sock didReadData:data withTag:tag];
    });
    // Always keep a read in the queue.
    [self beginReadingData];
}

/**
 * Called when a socket has completed writing the requested data. Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate socket:sock didWriteDataWithTag:tag];
    });
}

/**
 * Conditionally called if the read stream closes, but the write stream may still be writeable.
 *
 * This delegate method is only called if autoDisconnectOnClosedReadStream has been set to NO.
 * See the discussion on the autoDisconnectOnClosedReadStream method for more information.
 **/
- (void)socketDidCloseReadStream:(GCDAsyncSocket *)sock {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate socketDidCloseReadStream:sock];
        [self.tcpScoket disconnect];
        sharedInstance = nil;
    });
}

-(BOOL) isConnected {
    return [self.tcpScoket isConnected];
}

-(void) disconnect {
    debugLog(@"");
    if ([self.tcpScoket isConnected])
    {
        [self.tcpScoket disconnect];
    }
}

-(void) writeData:(NSData *) data withTag:(long) tag {
    if ([self.tcpScoket isConnected]) {
        [self.tcpScoket writeData:data withTimeout:-1 tag:tag];
    }
}

/**
 * Called when a socket disconnects with or without error.
 *
 * If you call the disconnect method, and the socket wasn't already disconnected,
 * this delegate method will be called before the disconnect method returns.
 **/
- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate socketDidDisconnect:sock withError:err];
        sharedInstance = nil;
    });
}

- (BOOL)isConnectedToRobotWithId:(NSString *)robotId {
    if ([self isConnected]) {
        if ([self.connectedRobotId caseInsensitiveCompare:robotId] == NSOrderedSame) {
            return YES;
        }
    }
    return NO;
}

@end
