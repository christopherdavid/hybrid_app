#import "RobotManagerCallWrapper.h"
#import "LogHelper.h"
#import "RobotCommandHelper.h"
#import "RobotScheduleManager.h"
#import "RobotDriveManager.h"

@interface RobotManagerCallWrapper() <RobotScheduleManagerProtocol, RobotDriveManagerProtocol>
@property(nonatomic, retain) RobotManagerCallWrapper *retained_self;
@property(nonatomic, retain) NSString *callbackId;
@property(nonatomic, readwrite) bool isForCommandSend;
@end

@implementation RobotManagerCallWrapper

- (void)connectedOverTCP:(NSString*)host toRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(connectedOverTCP:callbackId:)]) {
        [self.delegate connectedOverTCP:host callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)tcpConnectionDisconnectedWithError:(NSError *)error forRobot:(NeatoRobot *)neatoRobot forcedDisconnected:(BOOL)forcedDisconneted {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(tcpConnectionDisconnected:callbackId:)]) {
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

- (void)connectedOverTCP2:(NSString*)host toRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(connectedOverTCP2:callbackId:)]) {
        [self.delegate connectedOverTCP2:host callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)failedToConnectToTCP2WithError:(NSError *)error forRobot:(NeatoRobot *)robot forcedDisconnected:(BOOL)forcedDisconnected {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:callbackId:)]) {
        [self.delegate failedToConnectToTCP2WithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId {

    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    RobotCommandHelper *robotCommandHelper = [[RobotCommandHelper alloc] init];
    [robotCommandHelper sendCommandToRobot2:robotId commandId:commandId params:params delegate:self];
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

- (void)updateScheduleForScheduleId:(NSString *)scheduleId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    RobotScheduleManager *scheduleManager = [[RobotScheduleManager alloc] init];
    [scheduleManager updateScheduleForScheduleId:scheduleId delegate:self];
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

- (void)commandSentWithResult:(NSDictionary *)result {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(commandSentWithResult:callbackId:)]) {
        [self.delegate commandSentWithResult:result callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)failedtoSendCommandWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedtoSendCommandWithError:callbackId:)]) {
        [self.delegate failedtoSendCommandWithError:error callbackId:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)driveRobotWithId:(NSString *)robotId navigationControlId:(NSString *)navigationControlId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    RobotDriveManager *driveManager = [[RobotDriveManager alloc] init];
    driveManager.delegate = self;
    [driveManager driveRobotWithRobotId:robotId navigationControlId:navigationControlId];
}

- (void)driveRobotFailedWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(driveRobotFailedWithError:callbackId:)]) {
        [self.delegate performSelector:@selector(driveRobotFailedWithError:callbackId:) withObject:error withObject:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}


- (void)stopRobotDriveForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    RobotDriveManager *driveManager = [[RobotDriveManager alloc] init];
    driveManager.delegate = self;
    [driveManager stopDriveRobotForRobotId:robotId];
}

- (void)stopRobotDriveSucceded {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(stopRobotDriveSuccededForCallbackId:)]) {
        [self.delegate performSelector:@selector(stopRobotDriveSuccededForCallbackId:) withObject:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)stopRobotDriveFailedWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(stopRobotDriveFailedWithError:callbackId:)]) {
        [self.delegate performSelector:@selector(stopRobotDriveFailedWithError:callbackId:) withObject:error withObject:self.callbackId];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

- (void)sendCommandOverTCPToRobotWithId:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId {
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    RobotCommandHelper *commandHelper = [[RobotCommandHelper alloc] init];
    [commandHelper sendCommandOverTCPToRobotWithId:robotId commandId:commandId params:params delegate:self];
}

- (void)failedToFormTCPConnectionForRobotId:(NSString *)robotId {
    debugLog(@"");
    // Empty
}

@end
