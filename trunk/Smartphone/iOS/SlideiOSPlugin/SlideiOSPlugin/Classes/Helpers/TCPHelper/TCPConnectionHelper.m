#import "TCPConnectionHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "TCPSocket.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"

static TCPConnectionHelper *sharedInstance = nil;

@interface TCPConnectionHelper()

@property(nonatomic, weak) id<TCPConnectionHelperProtocol> delegate;
@property(nonatomic, retain) NeatoRobot *neatoRobot;
// If caller initated disconnection of TCP then it is YES otherwise NO.
@property(nonatomic) BOOL isForcedDisconnecting;

@end

@implementation TCPConnectionHelper
@synthesize delegate = _delegate;
@synthesize neatoRobot = _neatoRobot;
@synthesize isForcedDisconnecting = _isForcedDisconnecting;

+ (id)sharedTCPConnectionHelper {
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[TCPConnectionHelper alloc] init];
    });
    return sharedInstance;
}

-(void) disconnectFromRobot:(NSString *) robotId delegate:(id) delegate
{
    debugLog(@"");
    self.delegate = delegate;
    self.isForcedDisconnecting = YES;
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

-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId
{
    debugLog(@"");
    debugLog(@"Robot associated with the user. Will try to connect over TCP");
    [TCPSocket getSharedTCPSocket].delegate = self;
    [[TCPSocket getSharedTCPSocket] connectToRobotWithId:robotId host:self.neatoRobot.ipAddress overPort:TCP_ROBOT_SERVER_SOCKET_PORT];
}

-(BOOL) sendCommandToRobot:(NSData *)command withTag:(long)tag delegate:(id<TCPConnectionHelperProtocol>) delegate
{
    debugLog(@"");
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
        return NO;
    }
}


/**
 * Called when a socket connects and is ready for reading and writing.
 * The host parameter will be an IP address, not a DNS name.
 **/
- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port {
    debugLog(@"");
    @synchronized(self) {
        if ([self.delegate respondsToSelector:@selector(connectedOverTCP:toRobotWithId:)]) {
            [self.delegate performSelector:@selector(connectedOverTCP:toRobotWithId:) withObject:host withObject:[[TCPSocket getSharedTCPSocket] connectedRobotId]];
        }
    }
}

/**
 * Called when a socket has completed reading the requested data into memory.
 * Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
    debugLog(@"");
    @synchronized(self) {
        if ([self.delegate respondsToSelector:@selector(receivedDataOverTCP:toRobotWithId:)]) {
            [self.delegate performSelector:@selector(receivedDataOverTCP:) withObject:data];
        }
    }
}

/**
 * Called when a socket has completed writing the requested data. Not called if there is an error.
 **/
- (void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    debugLog(@"");
    @synchronized(self) {
        if ([self.delegate respondsToSelector:@selector(commandSentOverTCP)]) {
            [self.delegate performSelector:@selector(commandSentOverTCP)];
        }
    }
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
    if (err && err.code == GCDAsyncSocketConnectTimeoutError) {
        [self failedToConnectToSocket:sock withTimeoutError:err];
        return;
    }
    @synchronized(self) {
        NeatoRobot *connectedRobot = [NeatoRobotHelper getRobotForId:[[TCPSocket getSharedTCPSocket] connectedRobotId]];
        connectedRobot.robotId = [[TCPSocket getSharedTCPSocket] connectedRobotId];
        debugLog(@"Post robot disconnection notification.");
        NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
        [userInfo setObject:connectedRobot.robotId forKey:KEY_ROBOT_ID];
        if (err) {
            [userInfo setObject:err forKey:KEY_DISCONNECTION_ERROR];
        }
        [userInfo setObject:[NSNumber numberWithBool:self.isForcedDisconnecting] forKey:KEY_TCP_FORCED_DISCONNECTED];
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_TCP_DISCONNECTION object:nil userInfo:userInfo];
        self.delegate = nil;
        self.isForcedDisconnecting = NO;
    }
}

- (void)connectToRobotOverTCP2:(NeatoRobot *)robot delegate:(id)delegate {
    debugLog(@"");
    if (robot == nil) {
        debugLog(@"NeatoRobot is nil. Quiting!");
        return;
    }
    self.delegate = delegate;
    self.neatoRobot = robot;
    
    debugLog(@"connecting to TCP...");
    [TCPSocket getSharedTCPSocket].delegate = self;
    [[TCPSocket getSharedTCPSocket] connectToRobotWithId:robot.robotId host:robot.ipAddress overPort:TCP_ROBOT_SERVER_SOCKET_PORT2];
}

- (void)sendCommandToRobot2:(NSData *)command withTag:(long)tag requestId:(NSString *)requestId delegate:(id)delegate {
    debugLog(@"");
    self.delegate = delegate;
    if([[TCPSocket getSharedTCPSocket] isConnected]) {
        [TCPSocket getSharedTCPSocket].delegate = self;
        [[TCPSocket getSharedTCPSocket] writeData:command withTag:tag];
    }
    else {
        debugLog(@"Device isn't connected to the Robot.");
        if ([self.delegate respondsToSelector:@selector(failedToSendCommandOverTCPWithError:)]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.delegate performSelector:@selector(failedToSendCommandOverTCPWithError:) withObject:[AppHelper nserrorWithDescription:@"Device not connected over TCP" code:200]];
                self.delegate = nil;
            });
        }
    }
}

- (BOOL)isRobotConnectedOverTCP:(NSString *)robotId {
    return [[TCPSocket getSharedTCPSocket] isConnectedToRobotWithId:robotId];
}

- (void)failedToConnectToSocket:(GCDAsyncSocket *)sock withTimeoutError:(NSError *)err {
    debugLog(@"Failed to connect to TCP with error = %@", err);
    @synchronized(self) {
        NeatoRobot *connectedRobot = [NeatoRobotHelper getRobotForId:[[TCPSocket getSharedTCPSocket] connectedRobotId]];
        connectedRobot.robotId = [[TCPSocket getSharedTCPSocket] connectedRobotId];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(failedToFormTCPConnectionForRobotId:)]) {
                [self.delegate failedToFormTCPConnectionForRobotId:connectedRobot.robotId];
            }
            self.delegate = nil;
        });
    }
}

@end
