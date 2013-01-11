#import "FindNearByRobotsHelper.h"
#import "NetworkUtils.h"
#import "LogHelper.h"
#import "NeatoRobot.h"
#import "UDPSocket.h"
#import "CommandsHelper.h"
#import "UDPCommandHelper.h"


#define TAG_FIND_ROBOT_COMMAND 9001

@interface FindNearByRobotsHelper()

@property(nonatomic, weak) id callbackDelegate;
@property(nonatomic, readwrite) SEL callbackAction;
@property(nonatomic, retain) FindNearByRobotsHelper *retained_self;
@property(nonatomic, retain) NSMutableArray *receivedCommands;
@property(nonatomic, retain) NSMutableArray *foundRobots;


-(void) sendDiscoverRobotsCommand;
-(void) closeSocketAfterTimerExpires;
-(void) parseReceivedCommands;

@end

@implementation FindNearByRobotsHelper
@synthesize callbackAction = _callbackAction, callbackDelegate = _callbackDelegate, retained_self = _retained_self, receivedCommands = _receivedCommands,foundRobots = _foundRobots;

-(void) findNearbyRobots:(id)delegate action:(SEL)action
{
    if (delegate == nil || action == nil)
    {
        debugLog(@"Delegate or action not set!!");
        return;
    }
    self.retained_self = self;
    self.callbackDelegate = delegate;
    self.callbackAction = action;

    [UDPSocket getSharedUDPSocket].delegate = self;
    
    if (![[UDPSocket getSharedUDPSocket] prepareUDPSocket])
    {
        debugLog(@"Could not start UDP socket!!");
        self.retained_self = nil;
        [[UDPSocket getSharedUDPSocket] closeSocket];
        self.callbackDelegate = nil;
        return;
    }
    self.receivedCommands = [[NSMutableArray alloc] init];
    [self sendDiscoverRobotsCommand];
}

-(void) sendDiscoverRobotsCommand
{
    debugLog(@"sendDiscoverRobotsCommand called");
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
    
    [[UDPSocket getSharedUDPSocket] sendData:[[[UDPCommandHelper alloc] init] getFindRobotsCommand] host:[[[NetworkUtils alloc] init] getSubnetIPAddress] port:UDP_SMART_APPS_BROADCAST_PORT tag:TAG_FIND_ROBOT_COMMAND];
    
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
    
    if ([[NetworkUtils alloc] isCommandFromRemoteDevice:host])
    {
        debugLog(@"Packet is from remove deivce!");
        NSString *xmlStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        [self.receivedCommands addObject:xmlStr];
    }
}

-(void) parseReceivedCommands
{
    debugLog(@"");
    if (!self.receivedCommands || [self.receivedCommands count] == 0)
    {
        debugLog(@"No replies received. Robots not found!");
        self.retained_self = nil;
        [[UDPSocket getSharedUDPSocket] closeSocket];
        self.callbackDelegate = nil;
        return;
    }
    
    NSLog(@"self.receivedCommands count = %d", [self.receivedCommands count]);
    for (NSString *xmlCommand in self.receivedCommands) {
        if ([[[CommandsHelper alloc] init] isResponseToFindRobots:xmlCommand])
        {
            debugLog(@"Find robots response = %@", xmlCommand);
            [self remoteRobotFound:xmlCommand];
        }
    }
    
    @synchronized(self)
    {
        [self.callbackDelegate performSelectorOnMainThread:self.callbackAction withObject:self.foundRobots waitUntilDone:NO];
        self.callbackDelegate = nil;
        self.retained_self = nil;
    }
}

-(void) remoteRobotFound:(NSString *) xmlCommand
{
    NeatoRobot *robot = [[[CommandsHelper alloc] init] getRemoteRobot:xmlCommand];
    if (self.foundRobots == nil)
    {
        self.foundRobots = [[NSMutableArray alloc] init];
    }
    [self.foundRobots addObject:robot];
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
 * Called when the socket is closed.
 **/
- (void)udpSocketDidClose:(GCDAsyncUdpSocket *)sock withError:(NSError *)error
{
    debugLog(@"udpSocketDidClose called");
    [self parseReceivedCommands];
}


@end
