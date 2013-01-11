#import "RobotManagerCallWrapper.h"
#import "LogHelper.h"
#import "XMPPConnectionHelper.h"

@interface RobotManagerCallWrapper()

@property(nonatomic, retain) RobotManagerCallWrapper *retained_self;
@property(nonatomic, retain) NSString *callbackId;
@property(nonatomic, readwrite) bool isForCommandSend;
@end

@implementation RobotManagerCallWrapper
@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;
@synthesize callbackId = _callbackId;

-(void) findRobotsNearBy:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager findRobotsNearBy:self action:@selector(findRobotsNearByHandler:)];
}

-(void) findRobotsNearByHandler:(id) value
{
    debugLog(@"");
    [self.delegate foundRobotsNearby:value callbackId:self.callbackId];
    self.retained_self = nil;
    self.delegate = nil;
}

/*-(void) getRobotInfoBySerialId:(NSString *) serialId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager getRobotInfoBySerialId:serialId delegate:self action:@selector(getRobotInfoBySerialIdHandler:)];
}
*/
/*-(void) getRobotInfoBySerialIdHandler:(id) value
{
    debugLog(@"");
    [self.delegate gotRemoteRobotIP:value callbackId:self.callbackId];
    self.retained_self = nil;
    self.delegate = nil;
}*/

-(void) connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id) delegate callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager connectToRobotOverTCP:robot delegate:self];
}


-(void) diconnectRobotFromTCP:(NSString *) robotId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager diconnectRobotFromTCP:robotId delegate:self];
}


-(void) sendStartCleaningTo:(NSString *) robotId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager sendStartCleaningTo:robotId delegate:self];
}

-(void) sendCommandToRobot:(NSString *) robotId commandId:(NSString *) commandId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.isForCommandSend = NO;
    self.retained_self = self;
    self.callbackId = callbackId;
    
    if (![[[XMPPConnectionHelper alloc] init] isConnected])
    {
        self.isForCommandSend = YES;
    }
    
    [NeatoRobotManager sendCommand:commandId to:robotId delegate:self];
}

-(void) sendStopCleaningTo:(NSString *) robotId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager sendStopCleaningTo:robotId delegate:self];
}

-(void) connectedOverTCP:(NSString*) host
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(connectedOverTCP:callbackId:)])
    {
        [self.delegate connectedOverTCP:host callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) tryDirectConnection:(NSString *) robotId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager getRobotInfoBySerialId:robotId delegate:self action:@selector(getRobotInfoBySerialIdHandler:)];
}

-(void) getRobotInfoBySerialIdHandler:(id) value
{
    debugLog(@"");
    if (value)
    {
        debugLog(@"Got remote device IP. Will try to connect over TCP.");
        // Now we should associate the user with the robot
        NeatoRobot *robot = (NeatoRobot *) value;
        debugLog(@"Robot IP address = %@", robot.ipAddress);
        [NeatoRobotManager connectToRobotOverTCP:robot delegate:self];
    }
    else
    {
        debugLog(@"Failed to get remote device IP. Will not connect over TCP.");
        if ([self.delegate respondsToSelector:@selector(tcpConnectionDisconnected:)])
        {
            [self.delegate tcpConnectionDisconnected:self.callbackId];
        }
        self.retained_self = nil;
        self.delegate = nil;
    }
}


-(void) tcpConnectionDisconnected
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(tcpConnectionDisconnected:)])
    {
        [self.delegate tcpConnectionDisconnected:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) didConnectOverXMPP
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(didConnectOverXMPP:)])
    {
        [self.delegate didConnectOverXMPP:self.callbackId];
    }
    if (!self.isForCommandSend)
    {
        self.isForCommandSend = NO;
        self.retained_self = nil;
        self.delegate = nil;
    }
}


-(void) didDisConnectFromXMPP
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(didDisConnectFromXMPP:)])
    {
        [self.delegate didDisConnectFromXMPP:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}


-(void) commandSentOverXMPP
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(commandSentOverXMPP:)])
    {
        [self.delegate commandSentOverXMPP:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) failedToSendCommandOverXMPP
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToSendCommandOverXMPP:)])
    {
        [self.delegate failedToSendCommandOverXMPP:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(commandReceivedOverXMPP:sender:callbackId:)])
    {
        [self.delegate commandReceivedOverXMPP:message sender:sender callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) commandSentOverTCP
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(commandSentOverTCP:)])
    {
        [self.delegate commandSentOverTCP:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) failedToSendCommandOverTCP
{
    if ([self.delegate respondsToSelector:@selector(failedToSendCommandOverTCP:)])
    {
        [self.delegate failedToSendCommandOverTCP:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) receivedDataOverTCP:(NSData *)data
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(receivedDataOverTCP:callbackId:)])
    {
        [self.delegate receivedDataOverTCP:data callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}



@end
