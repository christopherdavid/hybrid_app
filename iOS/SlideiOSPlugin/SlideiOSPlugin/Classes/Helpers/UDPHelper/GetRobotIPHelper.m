#import "GetRobotIPHelper.h"
#import "CommandsHelper.h"
#import "LogHelper.h"
#import "NetworkUtils.h"
#import "UDPSocket.h"
#import "UDPCommandHelper.h"

#define TAG_GET_ROBOT_IP_COMMAND 9002

@interface GetRobotIPHelper()

@property(nonatomic, weak) id callbackDelegate;
@property(nonatomic, readwrite) SEL callbackAction;
@property(nonatomic, retain) GetRobotIPHelper *retained_self;
@property(nonatomic, retain) NSString *serialId;

-(void) sendGetRobotIPCommand;
-(void) closeSocketAfterTimerExpires;
-(void) parseReceivedCommand:(NSString *)xml fromAddress:(NSString *) address overPort:(uint16_t) port;
-(void) closeSocket:(id) value;

@end

@implementation GetRobotIPHelper
@synthesize callbackAction = _callbackAction, callbackDelegate = _callbackDelegate, retained_self = _retained_self,serialId = _serialId;

-(void) getRobotIPAddress:(NSString *) serialId delegate:(id)delegate action:(SEL)action
{
    if (delegate == nil || action == nil)
    {
        debugLog(@"Delegate or action not set!!");
        return;
    }
    self.retained_self = self;
    self.callbackDelegate = delegate;
    self.callbackAction = action;
    
    self.serialId = serialId;
    
    [UDPSocket getSharedUDPSocket].delegate = self;
    
    [self sendGetRobotIPCommand];
}

-(void) sendGetRobotIPCommand
{
    debugLog(@"");
    
    // Lets first enable multicast on socket
    if (!([[UDPSocket getSharedUDPSocket] enableBroadcast:YES]))
    {
        debugLog(@"Cound not enable broadcast on UDP socket. Would not send 'findNearByRobotsCommand'!!");
        self.retained_self = nil;
        [[UDPSocket getSharedUDPSocket] closeSocket];
        self.callbackDelegate = nil;
        return;
    }

    if (!([[UDPSocket getSharedUDPSocket] beginReceiving]))
    {
        debugLog(@"Could not receive data on UDP socket!!");
        self.retained_self = nil;
        [[UDPSocket getSharedUDPSocket] closeSocket];
        self.callbackDelegate = nil;
        return;
    }
    
    [[UDPSocket getSharedUDPSocket] sendData:[[[UDPCommandHelper alloc] init] getRobotIPAddressCommand:self.serialId] host:[[[NetworkUtils alloc] init] getSubnetIPAddress] port:UDP_SMART_APPS_BROADCAST_PORT tag:TAG_GET_ROBOT_IP_COMMAND];
    
    [self closeSocketAfterTimerExpires];
}

-(void) closeSocketAfterTimerExpires
{
    debugLog(@"");
    [NSTimer scheduledTimerWithTimeInterval:TIME_BEFORE_SOCKET_CLOSES target:self selector:@selector(closeSocket:) userInfo:nil repeats:NO];
}

-(void) closeSocket:(id) value
{
    debugLog(@"");
    [[UDPSocket getSharedUDPSocket] closeSocket];
}

-(void) parseReceivedCommand:(NSString *)xml fromAddress:(NSString *) address overPort:(uint16_t) port
{
    debugLog(@"");
    @synchronized(self)
    {
        if ([[[CommandsHelper alloc] init] isResponseToGetRobotIP:xml])
        {
            debugLog(@"Get robot ip response = %@", xml);
            [self closeSocket:nil];
            NeatoRobot *robot = [[[CommandsHelper alloc] init] getRemoteRobot:xml];
            robot.ipAddress = address;
            robot.port  = port;
           
            [self.callbackDelegate performSelectorOnMainThread:self.callbackAction withObject:robot waitUntilDone:NO];
            self.callbackDelegate = nil;
            self.callbackDelegate = nil;
            self.retained_self = nil;
        }
    }
}



/**
 * Called when the datagram with the given tag has been sent.
 **/
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didSendDataWithTag:(long)tag
{
    debugLog(@"didSendDataWithTag called");
}

/**
 * Called if an error occurs while trying to send a datagram.
 * This could be due to a timeout, or something more serious such as the data being too large to fit in a sigle packet.
 **/
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didNotSendDataWithTag:(long)tag dueToError:(NSError *)error
{
    debugLog(@"didNotSendDataWithTag called");
}

/**
 * Called when the socket has received the requested datagram.
 **/
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didReceiveData:(NSData *)data
      fromAddress:(NSData *)address
withFilterContext:(id)filterContext
{
    
    NSString *host = nil;
    uint16_t port = 0;
    [GCDAsyncUdpSocket getHost:&host port:&port fromAddress:address];
    debugLog(@"didReceiveData called. From Adress = %@ %i",host,port);
    
    if ([[[NetworkUtils alloc] init] isCommandFromRemoteDevice:host])
    {
        debugLog(@"Packet is from remove deivce!");
        NSString *xmlStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        [self parseReceivedCommand:xmlStr fromAddress:host overPort:port];
    }
}


/**
 * Called when the socket is closed.
 **/
- (void)udpSocketDidClose:(GCDAsyncUdpSocket *)sock withError:(NSError *)error
{
    debugLog(@"udpSocketDidClose called");
    @synchronized(self)
    {
        [self.callbackDelegate performSelectorOnMainThread:self.callbackAction withObject:nil waitUntilDone:NO];
        self.callbackDelegate = nil;
        self.callbackDelegate = nil;
        self.retained_self = nil;
    }
}

@end
