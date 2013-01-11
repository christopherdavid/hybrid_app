//
//  RobotManagerPlugin.m
//  SlideiOSPlugin
//

#import "RobotManagerPlugin.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "RobotManagerCallWrapper.h"
#import "PluginConstants.h"

@implementation RobotManagerPlugin

- (void) discoverNearByRobots:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
    NSString *callbackId = [arguments pop];
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call findRobotsNearBy:callbackId];
}

-(void) foundRobotsNearby:(NSArray *) nearbyRobots callbackId:(NSString *) callbackId
{
    debugLog(@"Fount robots = %d", [nearbyRobots count]);
    NSMutableArray *robotArray = [[NSMutableArray alloc] init];
    for(int i = 0;i < [nearbyRobots count];i++)
    {
        NeatoRobot *robot = [nearbyRobots objectAtIndex:i];
        NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
        [dict setObject:robot.name forKey:KEY_ROBOT_NAME];
        [dict setObject:robot.serialNumber forKey:KEY_ROBOT_ID];
        [robotArray addObject:dict];
    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:robotArray];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) sendCommandToRobot:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
    
    NSString *callbackId = [arguments pop];
    
    NSString *parameters = [arguments objectAtIndex:0];
    NSDictionary *data = [AppHelper parseJSON: [parameters dataUsingEncoding:NSUTF8StringEncoding]];
    NSString *commandId = [data objectForKey:KEY_COMMAND_ID];
    NSString *robotId = [data objectForKey:KEY_ROBOT_ID];
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot:robotId commandId:commandId callbackId:callbackId];
}

- (void) tryDirectConnection:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
    NSString *callbackId = [arguments pop];
    
    NSString *parameters = [arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSDictionary *data = [AppHelper parseJSON: [parameters dataUsingEncoding:NSUTF8StringEncoding]];
    debugLog(@"data = %@", data);
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;

    [call tryDirectConnection:[data valueForKey:KEY_ROBOT_ID] callbackId:callbackId];
}

- (void) robotSetSchedule:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}

- (void) getSchedule:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}


- (void) getRobotMap:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}

- (void) setMapOverlayData:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
}

-(void) failedToSendCommandOverXMPP:(NSString *) callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_FAILURE];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) disconnectDirectConnection:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    debugLog(@"");
    NSString *callbackId = [arguments pop];
    
    NSString *parameters = [arguments objectAtIndex:0];
    debugLog(@"parameters = %@", parameters);
    NSDictionary *data = [AppHelper parseJSON: [parameters dataUsingEncoding:NSUTF8StringEncoding]];
    debugLog(@"data = %@", data);
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    
    NSString *robotId = [data valueForKey:KEY_ROBOT_ID];
    
    // TODO: why take robot when we can connect to only one device at a time on TCP
    [call diconnectRobotFromTCP:robotId callbackId:callbackId];

}


-(void) gotRemoteRobotIP:(NeatoRobot *) neatoRobot callbackId:(NSString *) callbackId
{
    debugLog(@"");
    debugLog(@"Robot chat Id : %@", neatoRobot.chatId);
}


-(void) connectedOverTCP:(NSString*) host callbackId:(NSString *) callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:TCP_CONNECTION_STATUS_CONNECTED];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) tcpConnectionDisconnected:(NSString *) callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:TCP_CONNECTION_STATUS_NOT_CONNECTED];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) failedToSendCommandOverTCP:(NSString *)callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_FAILURE];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) commandSentOverTCP :(NSString *) callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_SUCCESS];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) didConnectOverXMPP :(NSString *) callbackId
{
    debugLog(@"");
}

-(void) didDisConnectFromXMPP:(NSString *) callbackId
{
    debugLog(@"");
}

-(void) commandSentOverXMPP:(NSString *) callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_SUCCESS];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender callbackId:(NSString *) callbackId
{
    debugLog(@"");
}

-(void) receivedDataOverTCP:(NSData *)data callbackId:(NSString *) callbackId
{
    debugLog(@"");
}

@end
