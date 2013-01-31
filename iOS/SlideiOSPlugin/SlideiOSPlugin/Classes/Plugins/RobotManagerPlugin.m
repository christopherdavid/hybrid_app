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

- (void) discoverNearByRobots:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
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
        [dict setObject:robot.robotId forKey:KEY_ROBOT_ID];
        [robotArray addObject:dict];
    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:robotArray];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) sendCommandToRobot:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    
    NSString *callbackId = command.callbackId; //use this to get command.callbackId
    
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are : %@",parameters);
    NSString *commandId = [parameters objectForKey:KEY_COMMAND_ID];
    NSString *robotId = [parameters objectForKey:KEY_ROBOT_ID];
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call sendCommandToRobot:robotId commandId:commandId callbackId:callbackId];
}

- (void) tryDirectConnection:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSDictionary *parameters =  [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are : %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    
    [call tryDirectConnection:[parameters valueForKey:KEY_ROBOT_ID] callbackId:callbackId];
}

- (void) robotSetSchedule:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}


- (void) getSchedule:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}
- (void) getRobotMap:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}

- (void) setMapOverlayData:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
}

-(void) failedToSendCommandOverXMPP:(NSString *) callbackId
{
    debugLog(@"");
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:COMMAND_SENT_FAILURE];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) disconnectDirectConnection:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSDictionary *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    
    NSString *robotId = [parameters valueForKey:KEY_ROBOT_ID];
    
    // TODO: why take robot when we can connect to only one device at a time on TCP
    [call diconnectRobotFromTCP:robotId callbackId:callbackId];
    
}

-(void) getRobotAtlasMetadata:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSString *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"received parameters are %@",parameters);
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call getRobotAtlasMetadataForRobotId:[parameters valueForKey:KEY_ROBOT_ID] callbackId:callbackId];
}

-(void) getAtlasDataFailed:(NSError *) error callbackId:(NSString *) callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

-(void) gotAtlasData:(NeatoRobotAtlas *) robotAtlas  callbackId:(NSString *) callbackId
{
    debugLog(@"Atlas Id = %@, Atlas metadata = %@", robotAtlas.atlasId, robotAtlas.atlasMetadata);
    // TODO: We have to decide upon the return values for all API's
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:robotAtlas.atlasId forKey:KEY_ATLAS_ID];
    [dictionary setValue:robotAtlas.atlasMetadata forKey:KEY_ATLAS_METADATA];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void) getAtlasGridData:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSString *parameters = [command.arguments objectAtIndex:0];
     debugLog(@"parameters received are %@",parameters);
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call getAtlasGridMetadata:[parameters valueForKey:KEY_ROBOT_ID] gridId:[parameters valueForKey:KEY_ATLAS_GRID_ID]  callbackId:callbackId];
}

-(void) gotAtlasGridMetadata:(AtlasGridMetadata *) atlasGridMetadata callbackId:(NSString *) callbackId
{
    debugLog(@"callbackId = %@", callbackId);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:atlasGridMetadata.atlasId forKey:KEY_ATLAS_ID];
    [dictionary setValue:atlasGridMetadata.gridId forKey:KEY_ATLAS_GRID_ID];
    [dictionary setValue:atlasGridMetadata.gridCachePath forKey:KEY_ATLAS_GRID_DATA];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) getAtlasGridMetadataFailed:(NSError *) error callbackId:(NSString *) callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
}

- (void) updateRobotAtlasMetadata:(CDVInvokedUrlCommand *)command
{
    debugLog(@"");
    NSString *callbackId = command.callbackId;
    
    NSString *parameters = [command.arguments objectAtIndex:0];
    debugLog(@"parameters received are %@",parameters);
    
    NeatoRobotAtlas *robotAtlas = [[NeatoRobotAtlas alloc] init];
    robotAtlas.robotId = [parameters valueForKey:KEY_ROBOT_ID];
    robotAtlas.atlasMetadata = [parameters valueForKey:KEY_ATLAS_METADATA];
    
    RobotManagerCallWrapper *call = [[RobotManagerCallWrapper alloc] init];
    call.delegate = self;
    [call updateRobotAtlasData:robotAtlas callbackId:callbackId];
}

-(void) atlasMetadataUpdated:(NeatoRobotAtlas *) robotAtlas callbackId:(NSString *) callbackId
{
    debugLog(@"Atlas Id = %@. Xml version = %@", robotAtlas.atlasId, robotAtlas.version);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    // TODO: Do we need to get the robot_atlas_id?
    [dictionary setValue:robotAtlas.atlasId forKey:KEY_ATLAS_ID];
    [dictionary setValue:robotAtlas.version forKey:KEY_ATLAS_VERSION];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}


-(void) failedToUpdateAtlasMetadataWithError:(NSError *) error callbackId:(NSString *) callbackId
{
    debugLog(@"Error = %@", error);
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[error localizedDescription] forKey:KEY_ERROR_MESSAGE];
    [dictionary setValue:ERROR_TYPE_UNKNOWN forKey:KEY_ERROR_CODE];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
    [self writeJavascript:[result toErrorCallbackString:callbackId]];
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


-(void) tcpConnectionDisconnected:(NSError *) error callbackId:(NSString *) callbackId
{
    debugLog(@"Error = %@", error);
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
