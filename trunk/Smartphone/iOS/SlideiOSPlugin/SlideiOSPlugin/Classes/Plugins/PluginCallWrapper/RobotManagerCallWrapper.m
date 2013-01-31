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

-(void) getRobotAtlasMetadataForRobotId:(NSString *) robotId callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager getRobotAtlasMetadataForRobotId:robotId delegate:self];
    
}

-(void) updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager updateRobotAtlasData:robotAtlas delegate:self];
}

-(void) atlasMetadataUpdated:(NeatoRobotAtlas *) robotAtlas
{
    if ([self.delegate respondsToSelector:@selector(atlasMetadataUpdated:callbackId:)])
    {
        [self.delegate atlasMetadataUpdated:robotAtlas callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) failedToUpdateAtlasMetadataWithError:(NSError *) error
{
    if ([self.delegate respondsToSelector:@selector(failedToUpdateAtlasMetadataWithError:callbackId:)])
    {
        [self.delegate failedToUpdateAtlasMetadataWithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) getAtlasGridMetadata:(NSString *) robotId gridId:(NSString *) gridId  callbackId:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager getAtlasGridMetadata:robotId gridId:gridId delegate:self];
}

-(void) gotAtlasGridMetadata:(AtlasGridMetadata *) atlasGridMetadata
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotAtlasGridMetadata:callbackId:)])
    {
        [self.delegate gotAtlasGridMetadata:atlasGridMetadata callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) getAtlasGridMetadataFailed:(NSError *) error
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(getAtlasGridMetadataFailed:callbackId:)])
    {
        [self.delegate getAtlasGridMetadataFailed:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) getAtlasDataFailed:(NSError *) error
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(getAtlasDataFailed:callbackId:)])
    {
        [self.delegate getAtlasDataFailed:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) gotAtlasData:(NeatoRobotAtlas *) robotAtlas
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotAtlasData:callbackId:)])
    {
        [self.delegate gotAtlasData:robotAtlas callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
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
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Failed to get remote device IP. Will not connect over TCP." forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:200] integerValue] userInfo:details];
        
        if ([self.delegate respondsToSelector:@selector(tcpConnectionDisconnected:callbackId:)])
        {
            [self.delegate tcpConnectionDisconnected:error callbackId:self.callbackId];
        }
        self.retained_self = nil;
        self.delegate = nil;
    }
}


-(void) tcpConnectionDisconnected:(NSError *) error
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(tcpConnectionDisconnected:callbackId:)])
    {
        [self.delegate tcpConnectionDisconnected:error callbackId:self.callbackId];
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
