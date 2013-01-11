#import "CommandsHelper.h"
#import "LogHelper.h"
#import "NetworkUtils.h"
#import "XMPP.h"
#import "AppHelper.h"

#define FIND_ROBOTS_COMMAND_RESPONSE 2
#define GET_ROBOT_IP_COMMAND_RESPONSE 4


@interface CommandsHelper()

@end

@implementation CommandsHelper


-(BOOL) isResponseToGetRobotIP:(NSString *) xml
{
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xml options:0 error:&error];
    
    
    //NSLog(@"document.rootElement = %@", document.rootElement);
    
    NSArray *elements = [document.rootElement nodesForXPath:@"//commandid" error:&error];
    //debugLog(@"elements count = %d", [elements count]);
    
    
    for (DDXMLElement *child in elements) {
        if ([child.stringValue intValue] == GET_ROBOT_IP_COMMAND_RESPONSE)
        {
            return YES;
        }
    }
    return NO;
}


-(BOOL) isResponseToFindRobots:(NSString *) xmlCommand
{
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlCommand options:0 error:&error];
    
    
    //NSLog(@"document.rootElement = %@", document.rootElement);
    
    NSArray *elements = [document.rootElement nodesForXPath:@"//commandid" error:&error];
    //debugLog(@"elements count = %d", [elements count]);
    
    
    for (DDXMLElement *child in elements) {
        if ([child.stringValue intValue] == FIND_ROBOTS_COMMAND_RESPONSE)
        {
            return YES;
        }
    }
    return NO;
}

-(NeatoRobot *) getRemoteRobot:(NSString *) xmlCommand
{
    NSError *error = nil;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlCommand options:0 error:&error];
    
    NeatoRobot *robot = [[NeatoRobot alloc] init];
    //NSLog(@"document.rootElement = %@", document.rootElement);
   
    NSArray *robotNameArr = [document.rootElement nodesForXPath:@"//robot_name" error:&error];
    if ([robotNameArr count] != 0)
    {
        robot.name = [[robotNameArr objectAtIndex:0] stringValue];
    }
    NSArray *robotIdArr = [document.rootElement nodesForXPath:@"//robot_id" error:&error];
    if ([robotIdArr count] != 0)
    {
        robot.robotId = [[robotIdArr objectAtIndex:0] stringValue];
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

@end
