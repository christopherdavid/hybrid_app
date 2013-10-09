#import "NeatoRobotManager.h"
#import "TCPHelper.h"
#import "LogHelper.h"
#import "XMPPConnectionHelper.h"
#import "NeatoUserHelper.h"
#import "XMPPHelper.h"
#import "NeatoRobotHelper.h"
#import "RobotCommandHelper.h"

@implementation NeatoRobotManager

// AS of now we allow only one TCP connection
// So the device will be connected to a single robot over TCP
// The device can send commands to any number of robots over XMPP
+(void) sendStartCleaningTo:(NSString *) roboId delegate:(id) delegate
{
    TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
    // TODO: check if connected devices robot ID is same as to which the
    // user wnats to send TCP command
    // If it is not, then we should send the command over XMPP
    if ([helper isConnected])
    {
        TCPHelper *helper = [[TCPHelper alloc] init];
        [helper startCleaning:delegate];
    }
    else
    {
        XMPPHelper *helper = [[XMPPHelper alloc] init];
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:roboId];
        if (robot == nil)
        {
            debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", roboId);
            [delegate failedToSendCommandOverXMPP];
            return;
        }
        [helper startCleaning:robot.chatId delegate:delegate];
    }
    
    /*            NeatoUser *user = [NeatoUserHelper getNeatoUser];
     if (user)
     {
     [connectectionHelper connectJID:user.chatId password:user.chatPassword host:XMPP_SERVER_ADDRESS];
     NeatoRobot *robot = [NeatoRobotHelper getRobotForId:roboId];
     if (robot == nil)
     {
     debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", roboId);
     return;
     }
     [helper startCleaning:robot.chatId];
     }
     else
     {
     debugLog(@"User not logged-in. Will not connect over XMPP!");
     }*/
}

// AS of now we allow only one TCP connection
// So the device will be connected to a single robot over TCP
// The device can send commands to any number of robots over XMPP
+(void) sendStopCleaningTo:(NSString *) roboId delegate:(id) delegate
{
    TCPConnectionHelper *helper = [TCPConnectionHelper sharedTCPConnectionHelper];
    // TODO: check if connected devices robot ID is same as to which the
    // user wnats to send TCP command
    // If it is not, then we should send the command over XMPP
    if ([helper isConnected])
    {
        TCPHelper *helper = [[TCPHelper alloc] init];
        [helper stopCleaning:delegate];
    }
    else
    {
        //XMPPConnectionHelper *connectectionHelper = [[XMPPConnectionHelper alloc] init];
        XMPPHelper *helper = [[XMPPHelper alloc] init];
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:roboId];
        if (robot == nil)
        {
            debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", roboId);
            [delegate failedToSendCommandOverXMPP];
            return;
        }
        [helper stopCleaning:robot.chatId delegate:delegate];
        /*}
         else
         {
         debugLog(@"Not connected over XMPP. Will connect and send command.");
         NeatoUser *user = [NeatoUserHelper getNeatoUser];
         if (user)
         {
         [connectectionHelper connectJID:user.chatId password:user.chatPassword host:XMPP_SERVER_ADDRESS];
         NeatoRobot *robot = [NeatoRobotHelper getRobotForId:roboId];
         if (robot == nil)
         {
         debugLog(@"WHOA! No robot found with id = %@ in the local storage. Maybe the robot is not associated with the user. Will not send XMPP command to the robot.", roboId);
         return;
         }
         [helper stopCleaning:robot.chatId];
         }
         else
         {
         debugLog(@"User not logged-in. Will not connect over XMPP!");
         }
         }*/
    }
}

+(void) sendCommand:(NSString *) commandId to:(NSString*) robotId delegate:(id) delegate
{
    switch ([commandId intValue]) {
        case COMMAND_START_ROBOT:
            [self sendStartCleaningTo:robotId delegate:delegate];
            break;
        case COMMAND_STOP_ROBOT:
            [self sendStopCleaningTo:robotId delegate:delegate];
            break;
        default:
            break;
    }
}

@end
