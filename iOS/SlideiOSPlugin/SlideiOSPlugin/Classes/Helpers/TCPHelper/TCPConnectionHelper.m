#import "TCPConnectionHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "TCPSocket.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"

@interface TCPConnectionHelper()

@property(nonatomic, retain) TCPConnectionHelper *retained_self;
@property(nonatomic, weak) id<TCPConnectionHelperProtocol> delegate;
@property(nonatomic, retain) NeatoRobot *neatoRobot;

-(void) notifyCallback :(SEL) callbackAction withObject:(id) object;
@end

@implementation TCPConnectionHelper
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize neatoRobot = _neatoRobot;

-(void) disconnectFromRobot:(NSString *) robotId delegate:(id<TCPConnectionHelperProtocol>) delegate
{
    debugLog(@"");
    self.retained_self = self;
    self.delegate = delegate;
    // 'robotId' is not being used right now. Smartphone can connect to only one
    // robot over TCP for now. We just disconnect from the one we are connected to.
    [TCPSocket getSharedTCPSocket].delegate = self;
    [[TCPSocket getSharedTCPSocket] disconnect];
}

-(BOOL) isConnected
{
    debugLog(@"");
    return [[TCPSocket getSharedTCPSocket] isConnected];
}

-(void) connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id<TCPConnectionHelperProtocol>) delegate
{
    debugLog(@"");
    if (robot == nil)
    {
        debugLog(@"NeatoRobot is nil. Quiting!");
        return;
    }
    self.delegate = delegate;
    self.retained_self = self;
    self.neatoRobot = robot;
    
    debugLog(@"Will try to associate the robot with the logged-in user.");
    /*[TCPSocket getSharedTCPSocket].delegate = self;
    
    [[TCPSocket getSharedTCPSocket] connectHost:robot.ipAddress overPort:TCP_ROBOT_SERVER_SOCKET_PORT];*/
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager setRobotUserEmail:[NeatoUserHelper getLoggedInUserEmail] serialNumber:robot.robotId];
    
}

-(void) robotAssociationFailedWithError:(NSError *)error
{
    debugLog(@"Robot association failed with user. Will not connect over TCP.");
    if ([self.delegate respondsToSelector:@selector(tcpConnectionDisconnected:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(tcpConnectionDisconnected:) withObject:error];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId
{
    debugLog(@"");
    debugLog(@"Robot associated with the user. Will try to connect over TCP");
    [TCPSocket getSharedTCPSocket].delegate = self;
    [[TCPSocket getSharedTCPSocket] connectHost:self.neatoRobot.ipAddress overPort:TCP_ROBOT_SERVER_SOCKET_PORT];
}

-(BOOL) sendCommandToRobot:(NSData *)command withTag:(long)tag delegate:(id<TCPConnectionHelperProtocol>) delegate
{
    debugLog(@"");
    self.retained_self = self;
    self.delegate = delegate;
    if([[TCPSocket getSharedTCPSocket] isConnected])
    {
        int commandLength = [command length];
        if ([AppHelper isArchitectureLittleEndian]) {
            debugLog(@"System is Little Endian!");
            commandLength = [AppHelper swapIntoBigEndian:commandLength];
        }
        
        
        NSData *data = [NSData dataWithBytes: &commandLength length: sizeof(commandLength)];
        [TCPSocket getSharedTCPSocket].delegate = self;
        // Send the length of the command first
        [[TCPSocket getSharedTCPSocket] writeData:data withTag:0];
        [TCPSocket getSharedTCPSocket].delegate = self;
        // Send the actual data now
        [[TCPSocket getSharedTCPSocket] writeData:command withTag:tag];
        return YES;
    }
    else
    {
        debugLog(@"Device isn't connected to the Robot.");
        self.retained_self = nil;
        return NO;
    }
}


/**
 * Called when a socket connects and is ready for reading and writing.
 * The host parameter will be an IP address, not a DNS name.
 **/
- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port
{
    debugLog(@"");
    [self notifyCallback:@selector(connectedOverTCP:) withObject:host];
}

/**
 * Called when a socket has completed reading the requested data into memory.
 * Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
    debugLog(@"");
    [self notifyCallback:@selector(receivedDataOverTCP:) withObject:data];
}

/**
 * Called when a socket has completed writing the requested data. Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    debugLog(@"");
    [self notifyCallback:@selector(commandSentOverTCP) withObject:[NSNumber numberWithLong:tag]];
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
    [[TCPSocket getSharedTCPSocket] disconnect];
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
    [self notifyCallback:@selector(tcpConnectionDisconnected:) withObject:err];
}

-(void) notifyCallback :(SEL) callbackAction withObject:(id) object
{
    debugLog(@"");
    @synchronized(self)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:callbackAction])
            {
                [self.delegate performSelector:callbackAction withObject:object];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}


@end
