#import "CommandsHelper.h"
#import "LogHelper.h"
#import "CommandTracker.h"
#import "XMPP.h"
#import "AppHelper.h"

#define FIND_ROBOTS_COMMAND_RESPONSE 2
#define GET_ROBOT_IP_COMMAND_RESPONSE 4

#define RESPONSE_TYPE_UNKNOWN 9001
#define RESPONSE_TYPE_FIND_ROBOTS 9002
#define RESPONSE_TYPE_GET_IP 9003

#define TEMP_PARAMS_XML_FORMAT @"<%@>%@</%@>"

// Internal class defination
@interface UDPCommandFormat : NSObject
{
    
}
@property(nonatomic, retain) NSString *command;
@property(nonatomic, retain) NSString *robotId;

@end

// Internal class implementation
@implementation UDPCommandFormat
@synthesize command = _command;
@synthesize robotId = _robotId;
@end

@interface CommandsHelper()
{

}
-(NSString *) getRequestIdFromCommand:(NSString *) xmlString;
-(int) getResponseType:(NSString *) xml withRequestId:(NSString *) requestId;

@end

// main class implementation
@implementation CommandsHelper


-(UDPCommandFormat *) getUDPCommandFromXml:(NSString *) xml
{
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xml options:0 error:&error];
    
    UDPCommandFormat *udpCommand = [[UDPCommandFormat alloc] init];
    NSArray *robotIdArr = [document.rootElement nodesForXPath:@"//robotId" error:&error];
    if (error || [robotIdArr count] == 0)
    {
        debugLog(@"Command does not contain robotID tag!!");
    }
    else
    {
        DDXMLElement *child = [robotIdArr objectAtIndex:0];
        NSString *robotId = child.stringValue;
        udpCommand.robotId = robotId;
    }
    
    NSArray *commadArr = [document.rootElement nodesForXPath:@"//command" error:&error];
    if (error || [commadArr count] == 0)
    {
        // This is not normal. The reply must contain a command tag.
        debugLog(@"Request does not containt <command> tag!!");
    }
    else
    {
        DDXMLElement *child = [commadArr objectAtIndex:0];
        NSString *commandId = child.stringValue;
        udpCommand.command = commandId;
    }
    return udpCommand;
}



-(NSString *) getRequestIdFromCommand:(NSString *) xmlString
{
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlString options:0 error:&error];
    
    NSArray *requestIdArr = [document.rootElement nodesForXPath:@"//requestId" error:&error];
    if (error || [requestIdArr count] == 0)
    {
        // This is not normal. The reply must contain a request Id.
        debugLog(@"Response does not contain RequestID!!");
        return nil;
    }
    DDXMLElement *child = [requestIdArr objectAtIndex:0];
    
    NSString *requestId = child.stringValue;
    debugLog(@"Received request ID = %@", requestId);
    return requestId;
}

-(int) getResponseType:(NSString *) xml withRequestId:(NSString *) requestId
{
    // Check if the request id is empty or nil
    if ([AppHelper isStringNilOrEmpty:requestId])
    {
        debugLog(@"requestId is nil in response. Check remote implementation!");
        return RESPONSE_TYPE_UNKNOWN;
    }
    
    // Get the original command sent for this request Id
    CommandTracker *commandTracker = [[CommandTracker alloc] init];
    NSString *originalCommand = [commandTracker getCommandForRequestId:requestId];
    
    debugLog(@"originalCommand = %@", originalCommand);
    
    if ([AppHelper isStringNilOrEmpty:originalCommand]) {
        debugLog(@"No command sent for request ID = %@", requestId);
        return RESPONSE_TYPE_UNKNOWN;
    }
    
    // Get the command info
    UDPCommandFormat *command = [self getUDPCommandFromXml:originalCommand];
    
    debugLog(@"sent Command = %@", command.command);
    
    if ([AppHelper isStringNilOrEmpty:command.command]) {
        debugLog(@"Original command does not containt command value!");
        return RESPONSE_TYPE_UNKNOWN;
    }
    
    if ([command.command intValue] == GET_ROBOT_IP_COMMAND || [command.command intValue] == FIND_ROBOTS_COMMAND)
    {
        // Check if the original command contains 'robotId' tag
        // GetRobotIP command should contain <robotId> tag
        if (![AppHelper isStringNilOrEmpty:command.robotId])
        {
            debugLog(@"Robot ID = %@. Got response for Get robot IP!", command.robotId);
            // As the command contains robotId tag, response is of type 'GetIP'
            return RESPONSE_TYPE_GET_IP;
        }
        else
        {
            // Command does not contain robotId tag, it is of type 'FindRobots'
            debugLog(@"Command does not contain robotId tag. Got response of Find near by robots!");
            return RESPONSE_TYPE_FIND_ROBOTS;
        }
    }
    else
    {
        // command does not match GET_ROBOT_IP_COMMAND or RESPONSE_TYPE_FIND_ROBOTS
        debugLog(@"Command does not match! command = %@", command.command);
        return RESPONSE_TYPE_UNKNOWN;
    }
}

-(void) removeCommandFromTracker:(NSString *) xmlCommand
{
    debugLog(@"");
    NSString *requestId = [self getRequestIdFromCommand:xmlCommand];
    [[[CommandTracker alloc] init] removeCommandForRequestId:requestId];
}

-(BOOL) isResponseToGetRobotIP:(NSString *) xml
{
    debugLog(@"");
    NSString *requestId = [self getRequestIdFromCommand:xml];
    if ([self getResponseType:xml withRequestId:requestId] == RESPONSE_TYPE_GET_IP)
    {
        return YES;
    }
    return NO;
    //return YES;
}

-(BOOL) isResponseToFindRobots:(NSString *) xmlCommand
{
    debugLog(@"");
    NSString *requestId = [self getRequestIdFromCommand:xmlCommand];
    if ([self getResponseType:xmlCommand withRequestId:requestId] == RESPONSE_TYPE_FIND_ROBOTS)
    {
        return YES;
    }
    return NO;
    //return YES;
}

-(NeatoRobot *) getRemoteRobot:(NSString *) xmlCommand
{
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlCommand options:0 error:&error];
    
    NeatoRobot *robot = [[NeatoRobot alloc] init];
    //NSLog(@"document.rootElement = %@", document.rootElement);
    
    NSArray *robotNameArr = [document.rootElement nodesForXPath:@"//name" error:&error];
    if ([robotNameArr count] != 0)
    {
        robot.name = [[robotNameArr objectAtIndex:0] stringValue];
    }
    NSArray *robotIdArr = [document.rootElement nodesForXPath:@"//robotId" error:&error];
    if ([robotIdArr count] != 0)
    {
        robot.robotId = [[robotIdArr objectAtIndex:0] stringValue];
    }
    NSArray *robotChatIdArr = [document.rootElement nodesForXPath:@"//chatId" error:&error];
    if ([robotChatIdArr count] != 0)
    {
        robot.chatId = [[robotChatIdArr objectAtIndex:0] stringValue];
    }
    
    NSArray *robotSerialIdArr = [document.rootElement nodesForXPath:@"//serial_id" error:&error];
    if ([robotSerialIdArr count] != 0)
    {
        robot.serialNumber = [[robotSerialIdArr objectAtIndex:0] stringValue];
    }
    NSArray *robotPortArr = [document.rootElement nodesForXPath:@"//robot_port" error:&error];
    if ([robotPortArr count] != 0)
    {
        robot.port = [[[robotPortArr objectAtIndex:0] stringValue] intValue];
    }
    NSArray *robotIPAddArr = [document.rootElement nodesForXPath:@"//robot_ip_address" error:&error];
    if ([robotIPAddArr count] != 0)
    {
        robot.ipAddress = [[robotIPAddArr objectAtIndex:0] stringValue];
    }
    return robot;
}

- (NSInteger)versionForCommand {
    return 1;
}

- (NSInteger)commandRetryCount {
    return 0;
}

- (NSString *)commandResponseNeeded
{
    //Sending the default value as of now.It may Change later.
    return @"false";
}

- (NSInteger)distributionModeForCommandType:(NSString *)connectionType
{
    if([connectionType isEqualToString:@"XMPP"])
    {
        return 0;
    }
    else
    {
        return 1;
    }
}

- (NSString *)generateXMLForParams:(NSDictionary *)params {
    debugLog(@"");
    NSMutableString *paramsXML = [[NSMutableString alloc] init];
    if([params count] != 0)
    {
        [paramsXML appendString:@"<params>"];
        NSArray *keys = [params allKeys];
        for(int i=0 ;i <[keys count]; i++)
        {
            [paramsXML appendString:[NSString stringWithFormat:TEMP_PARAMS_XML_FORMAT, keys[i], [params objectForKey:keys[i]], keys[i]]];
        }
        [paramsXML appendString:@"</params>"];
        return paramsXML;
    }
    else
    {
        [paramsXML appendString:@"<params/>"];
        return paramsXML;
    }
}

- (NSString *)commandIdFromXmlCommand:(NSString *)xmlCommand {
    debugLog(@"");
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlCommand options:0 error:&error];
    NSArray *commadArr = [document.rootElement nodesForXPath:@"//command" error:&error];
    if (error || [commadArr count] == 0) {
        // This is not normal. The reply must contain a command tag.
        debugLog(@"Request does not contain <command> tag!!");
        return nil;
    }
    else {
        DDXMLElement *child = [commadArr objectAtIndex:0];
        NSString *commandId = child.stringValue;
        debugLog(@"CommandId retreived from xmlCommand is : %@",commandId);
        return commandId;
    }
}

- (BOOL)isXMPPDataChangeCommand:(NSString *)xmlCommand {
    debugLog(@"");
    if ([self commandIdFromXmlCommand:xmlCommand]) {
        if ([[self commandIdFromXmlCommand:xmlCommand] isEqualToString:[NSString stringWithFormat:@"%d", COMMAND_ROBOT_PROFILE_DATA_CHANGED]]) {
            return YES;
        }
        return NO;
    }
    return NO;
}

- (NSDictionary *)parseXMPPDataChangeNotification:(NSString *)xmlCommand {
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlCommand options:0 error:&error];
    NSMutableDictionary *notificationData = [[NSMutableDictionary alloc] init];
    NSArray *robotIdArray = [document.rootElement nodesForXPath:@"//robotId" error:&error];
    if ([robotIdArray count] != 0)
    {
        [notificationData setValue:[[robotIdArray objectAtIndex:0] stringValue] forKey:@"robotId"];
    }
    NSArray *causeAgentIdArray = [document.rootElement nodesForXPath:@"//causeAgentId" error:&error];
    if ([robotIdArray count] != 0)
    {
        [notificationData setValue:[[causeAgentIdArray objectAtIndex:0] stringValue] forKey:@"causeAgentId"];
    }
    return notificationData;
}

- (BOOL)isCommandOfRequestType:(NSString *)xmlCommand {
    debugLog(@"");
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlCommand options:0 error:&error];
     NSArray *requestTag = [document.rootElement nodesForXPath:@"//request" error:&error];
    if ([requestTag count] != 0) {
        debugLog(@"Command is of request type.");
        return YES;
    }
    return NO;
}

@end
