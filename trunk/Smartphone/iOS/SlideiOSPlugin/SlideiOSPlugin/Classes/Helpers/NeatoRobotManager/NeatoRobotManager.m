#import "NeatoRobotManager.h"
#import "FindNearByRobotsHelper.h"
#import "GetRobotIPHelper.h"
#import "TCPHelper.h"
#import "LogHelper.h"
#import "XMPPConnectionHelper.h"
#import "NeatoUserHelper.h"
#import "XMPPHelper.h"
#import "NeatoRobotHelper.h"
#import "RobotAtlasManager.h"
#import "AtlasGridManager.h"
#import "DeviceConnectionManager.h"
#import "RobotCommandHelper.h"
#import "CommandsHelper.h"
#import "RobotScheduleManager.h"

@implementation NeatoRobotManager

+(void) findRobotsNearBy:(id) delegate action:(SEL)action
{
    FindNearByRobotsHelper *helper = [[FindNearByRobotsHelper alloc] init];
    [helper findNearbyRobots:delegate action:action];
}

+(void) getRobotInfoBySerialId:(NSString *) serialId delegate:(id) delegate action:(SEL) action
{
    GetRobotIPHelper *ipHelper = [[GetRobotIPHelper alloc] init];
    [ipHelper robotIPAddress:serialId delegate:delegate action:action];
}

+(void) connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id<TCPConnectionHelperProtocol>) delegate
{
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    [helper connectToRobotOverTCP:robot delegate:delegate];
}

+(void) diconnectRobotFromTCP:(NSString*) robotId delegate:(id) delegate;
{
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    [helper disconnectFromRobot:robotId delegate:delegate];
}

+(void) logoutFromXMPP:(id) delegate
{
    XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
    helper.delegate = delegate;
    [helper disconnectFromRobot];
}

+(void) getRobotAtlasMetadataForRobotId:(NSString *) robotId delegate:(id) delegate
{
    RobotAtlasManager *atlasManager = [[RobotAtlasManager alloc] init];
    [atlasManager getAtlasMetadataForRobotWithId:robotId delegate:delegate];
}

+(void) updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas  delegate:(id) delegate
{
    RobotAtlasManager *atlasManager = [[RobotAtlasManager alloc] init];
    [atlasManager updateRobotAtlasData:robotAtlas delegate:delegate];
}

+(void) getAtlasGridMetadata:(NSString *) robotId gridId:(NSString *) gridId delegate:(id) delegate
{
    AtlasGridManager *atlasGridMan = [[AtlasGridManager alloc] init];
    atlasGridMan.delegate = delegate;
    [atlasGridMan getAtlasGridMetadata:robotId gridId:gridId];
}

// AS of now we allow only one TCP connection
// So the device will be connected to a single robot over TCP
// The device can send commands to any number of robots over XMPP
+(void) sendStartCleaningTo:(NSString *) roboId delegate:(id) delegate
{
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
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
     [connectectionHelper connectJID:user.chatId password:user.chatPassword host:NEATO_XMPP_SERVER_ADDRESS];
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
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
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
         [connectectionHelper connectJID:user.chatId password:user.chatPassword host:NEATO_XMPP_SERVER_ADDRESS];
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

+ (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId delegate:(id)delegate {
    NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
    serverManager.delegate = delegate;
    [serverManager setRobotName2:robotName forRobotWithId:robotId];
}

+ (void)getDetailsForRobotWithId:(NSString *)robotId delegate:(id)delegate {
    NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
    serverManager.delegate = delegate;
    [serverManager getRobotDetails:robotId];
}

+ (void)onlineStatusForRobotWithId:(NSString *)robotId delegate:(id)delegate {
    NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
    serverManager.delegate = delegate;
    [serverManager onlineStatusForRobotWithId:robotId];
}

+ (void)tryDirectConnection2:(NSString *)robotId delegate:(id)delegate {
    DeviceConnectionManager *deviceConnectionManager = [[DeviceConnectionManager alloc] init];
    [deviceConnectionManager tryDirectConnection2:robotId delegate:delegate];
}


+ (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate {
    RobotCommandHelper *robotCommandHelper = [[RobotCommandHelper alloc] init];
    [robotCommandHelper sendCommandToRobot2:robotId commandId:commandId params:params delegate:delegate];
}

+ (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    return [scheduleManager createScheduleForRobotId:robotId forScheduleType:scheduleType];
}

+ (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    return [scheduleManager addScheduleEventData:scheduleEventData forScheduleWithScheduleId:scheduleId];
}

+ (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    return [scheduleManager updateScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId withScheduleEventdata:scheduleEventData];
}

+ (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    return [scheduleManager deleteScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
}

+ (id)scheduleEventDataWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    return [scheduleManager scheduleEventDataWithScheduleEventId:scheduleEventId withScheduleId:scheduleId];
}

+ (id)scheduleDataForScheduleId:(NSString *)scheduleId {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    return [scheduleManager scheduleDataForScheduleId:scheduleId];
}

+ (void)scheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType delegate:(id)delegate {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    [scheduleManager scheduleEventsForRobotWithId:robotId ofScheduleType:scheduleType delegate:delegate];
}

+ (void)updateScheduleForScheduleId:(NSString *)scheduleId delegate:(id)delegate {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    [scheduleManager updateScheduleForScheduleId:scheduleId delegate:delegate];
}

+ (void)setRobotSchedule:(NSArray *)schedulesArray forRobotId:(NSString *)robotId ofType:(NSString *)schedule_type delegate:(id)delegate {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    [scheduleManager setRobotSchedule:schedulesArray forRobotId:robotId ofType:schedule_type delegate:delegate];
}

+ (void)getRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type delegate:(id)delegate {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    [scheduleManager getSchedulesForRobotId:robotId OfType:schedule_type delegate:delegate];
}

+(void) deleteRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type delegate:(id)delegate {
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    [scheduleManager deleteScheduleForRobotId:robotId OfType:schedule_type delegate:delegate];
}

@end
