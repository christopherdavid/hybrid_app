#import "RobotManagerCallWrapper.h"
#import "LogHelper.h"
#import "XMPPConnectionHelper.h"
#import "RobotCommandManager.h"

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

- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager setRobotName2:robotName forRobotWithId:robotId delegate:self];
}

- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotName:updatedForRobotWithId:callbackId:)])
    {
        [self.delegate robotName:name updatedForRobotWithId:robotId callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)failedToUpdateRobotNameWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToUpdateRobotNameWithError:callbackId:)])
    {
        [self.delegate failedToUpdateRobotNameWithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)getDetailsForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager getDetailsForRobotWithId:robotId delegate:self];
}

-(void) failedToGetRobotDetailsWihError:(NSError *)error
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToGetRobotDetailsWihError:callbackId:)])
    {
        [self.delegate failedToGetRobotDetailsWihError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

-(void) gotRobotDetails:(NeatoRobot *)neatoRobot
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotRobotDetails:callbackId:)])
    {
        [self.delegate gotRobotDetails:neatoRobot callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)onlineStatusForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager onlineStatusForRobotWithId:robotId delegate:self];
}

- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(onlineStatus:forRobotWithId:callbackId:)])
    {
        [self.delegate onlineStatus:status forRobotWithId:robotId callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToGetRobotOnlineStatusWithError:callbackId:)]) {
        [self.delegate failedToGetRobotOnlineStatusWithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)tryDirectConnection2:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager tryDirectConnection2:robotId delegate:self];
}

- (void)failedToConnectToTCP2WithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:callbackId:)]) {
        [self.delegate failedToConnectToTCP2WithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)connectedOverTCP2:(NSString*) host {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(connectedOverTCP2:callbackId:)]) {
        [self.delegate connectedOverTCP2:host callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    [NeatoRobotManager sendCommandToRobot2:robotId commandId:commandId params:params delegate:self];
}

- (void)failedToSendCommandOverTCPWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToSendCommandOverTCPWithError:callbackId:)]) {
        [self.delegate failedToSendCommandOverTCPWithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)commandSentOverTCP2 {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(commandSentOverTCP2:)])
    {
        [self.delegate commandSentOverTCP2:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)commandSentOverXMPP2 {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(commandSentOverXMPP2:)])
    {
        [self.delegate commandSentOverXMPP2:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)failedToSendCommandOverXMPP2 {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToSendCommandOverXMPP2:)]) {
        [self.delegate failedToSendCommandOverXMPP2:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType {
    debugLog(@"");
    return  [NeatoRobotManager createScheduleForRobotId:robotId ofScheduleType:scheduleType];
}

- (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    return [NeatoRobotManager addScheduleEventData:scheduleEventData forScheduleWithScheduleId:scheduleId];
}

- (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData {
    debugLog(@"");
    return [NeatoRobotManager updateScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId withScheduleEventdata:scheduleEventData];
}

- (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    return [NeatoRobotManager deleteScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
}

- (id)scheduleEventDataWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    return [NeatoRobotManager scheduleEventDataWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
}

- (id)scheduleDataForScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    return [NeatoRobotManager scheduleDataForScheduleId:scheduleId];
}

- (void)scheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType callbackId:(NSString *)callbackId {
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager scheduleEventsForRobotWithId:robotId ofScheduleType:scheduleType delegate:self];
}

- (void)failedToGetScheduleEventsWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToGetScheduleEventsWithError:callbackId:)]) {
        [self.delegate performSelector:@selector(failedToGetScheduleEventsWithError:callbackId:) withObject:error withObject:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)gotScheduleEventsForSchedule:(Schedule *)schedule ofType:(NSInteger)scheduleType forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotScheduleEventsForSchedule:ofType:forRobotWithId:callbackId:)]) {
        [self.delegate gotScheduleEventsForSchedule:schedule ofType:scheduleType forRobotWithId:robotId callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)updateScheduleForScheduleId:(NSString *)scheduleId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager updateScheduleForScheduleId:scheduleId delegate:self];
}

- (void)updatedSchedule:(NSString *)scheduleId {
    if ([self.delegate respondsToSelector:@selector(updatedSchedule:callbackId:)]) {
        [self.delegate updatedSchedule:scheduleId callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)updateScheduleError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(updateScheduleError:callbackId:)]) {
        [self.delegate updateScheduleError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)setRobotSchedule:(NSArray *)schedulesArray forRobotId:(NSString *)robotId ofType:(NSString *)schedule_type callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager setRobotSchedule:schedulesArray forRobotId:robotId ofType:schedule_type delegate:self];
}

- (void)getRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type callbackId:(NSString *) callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager getRobotScheduleForRobotId:robotId ofType:schedule_type delegate:self];
}

- (void)deleteRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    [NeatoRobotManager deleteRobotScheduleForRobotId:robotId ofType:schedule_type delegate:self];
}

- (void)setScheduleSuccess:(NSString *)message {
    if ([self.delegate respondsToSelector:@selector(setScheduleSuccess:callbackId:)]) {
        [self.delegate setScheduleSuccess:message callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)getScheduleSuccess:(NSDictionary *)jsonObject {
    if ([self.delegate respondsToSelector:@selector(getScheduleSuccess:callbackId:)]) {
        [self.delegate getScheduleSuccess:jsonObject callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)setScheduleError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(setScheduleError:callbackId:)]) {
        [self.delegate setScheduleError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)getScheduleError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(getScheduleError:callbackId:)]) {
        [self.delegate getScheduleError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)deleteScheduleSuccess:(NSString *)message {
    if ([self.delegate respondsToSelector:@selector(deleteScheduleSuccess:callbackId:)]) {
        [self.delegate deleteScheduleSuccess:message callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)deleteScheduleError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(deleteScheduleError:callbackId:)]) {
        [self.delegate deleteScheduleError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)enabledDisable:(BOOL)enable schedule:(int)scheduleType forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;

    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager enabledDisable:enable schedule:scheduleType forRobotWithId:robotId withUserEmail:email];
}

- (void)enabledDisabledScheduleWithResult:(NSDictionary *)resultData {
    if ([self.delegate respondsToSelector:@selector(deleteScheduleSuccess:callbackId:)]) {
        [self.delegate enabledDisabledScheduleWithResult:resultData callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)failedToEnableDisableScheduleWithError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(failedToEnableDisableScheduleWithError:callbackId:)]) {
        [self.delegate failedToEnableDisableScheduleWithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)turnVacuumOnOff:(int)on forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email withParams:(NSDictionary *)params commandId:(NSString *)commandId callbackId:(NSString *)callbackId {
    debugLog(@"");
    
    self.retained_self = self;
    self.callbackId = callbackId;
    
    RobotCommandManager *manager = [[RobotCommandManager alloc] init];
    manager.delegate = self;
    [manager turnVacuumOnOff:on forRobotWithId:robotId withUserEmail:email withParams:params commandId:commandId];
}
@end
