#import "RobotDriveManager.h"
#import "NeatoRobotCommand.h"
#import "NeatoServerManager.h"
#import "AppHelper.h"
#import "TCPConnectionHelper.h"
#import "NeatoConstants.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"
#import "ProfileDetail.h"
#import "LogHelper.h"
#import "NeatoCommandExpiryHelper.h"
#import "XMPPRobotDataChangeManager.h"
#import "RobotCommandHelper.h"
#import "ProfileDetail.h"
#import "NeatoErrorCodes.h"
#import "NeatoErrorCodesHelper.h"

@interface RobotDriveManager()
@property(nonatomic, strong) RobotDriveManager *retainedSelf;
@end

@implementation RobotDriveManager
- (id)init {
    if (self = [super init]) {
        // Listen for TCP disconnection notification.
        // Remove if already observing.
        // Every object will listen for notification.
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyTCPDisconnected:) name:NOTIFICATION_TCP_DISCONNECTION object:nil];
    }
    return self;
}


- (void)robotWithRobotId:(NSString *)robotId isReadyToDriveWithIP:(NSString *)robotIp {
    debugLog(@"");
    self.retainedSelf = self;
    if ([self isRobotDriveRequestedForRobotId:robotId]) {
        [NeatoRobotHelper removeDriveRequestForRobotWihId:robotId];
        [self tryTCPConnectionToRobotId:robotId withIpAddress:robotIp];
    }
}

- (void)robotWithRobotId:(NSString *)robotId isNotAvailableToDriveWithErrorCode:(NSInteger)errorCode {
    debugLog(@"");
    if ([self isRobotDriveRequestedForRobotId:robotId]) {
        [NeatoRobotHelper removeDriveRequestForRobotWihId:robotId];
        [self notifyCannotDriveForRobotWIthRobotId:robotId withErrorResponseCode:errorCode];
    }
}

- (void)notifyCannotDriveForRobotWIthRobotId:(NSString *)robotId withErrorResponseCode:(NSInteger)errorCode {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setObject:[NSString stringWithFormat:@"%ld", (long)errorCode] forKey:ERROR_DRIVE_RESPONSE_CODE];
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager] notifyDataChangeForRobotId:robotId withKeyCode:[NSNumber numberWithInt:ROBOT_ERROR_IN_CONNECTING] andData:data];
}


// Method to know whether the user has requested robot drive.
- (BOOL)isRobotDriveRequestedForRobotId:(NSString *)robotId {
    id dbResult = [NeatoRobotHelper driveRequestForRobotWithId:robotId];
    if ([dbResult isKindOfClass:[NSNumber class]]) {
        return [(NSNumber *)dbResult boolValue];
    }
    // TODO:REVISIT This is an error case from database.
    return NO;
}

// If ipAddress is nil, it does nothing.
- (void)tryTCPConnectionToRobotId:(NSString *)robotId withIpAddress:(NSString *)ipAddress {
    if (ipAddress) {
        NeatoRobot *robot = [[NeatoRobot alloc] init];
        robot.robotId = robotId;
        robot.ipAddress = ipAddress;
        TCPConnectionHelper *tcpHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
        [tcpHelper connectToRobotOverTCP2:robot delegate:self];
    }
}

// TCPConnectionHelper callbacks.
- (void)connectedOverTCP:(NSString*)host toRobotWithId:(NSString *)robotId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager]
     notifyDataChangeForRobotId:robotId withKeyCode:[NSNumber numberWithInt:ROBOT_IS_CONNECTED] andData:data];
}

- (void)tcpConnectionDisconnectedWithError:(NSError *)error forRobot:(NeatoRobot *)neatoRobot forcedDisconnected:(BOOL)forcedDisconneted {
    if (forcedDisconneted) {
        // User has disconnected.
        debugLog(@"User has disconnected TCP connection.");
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(stopRobotDriveSucceded)]) {
                [self.delegate performSelector:@selector(stopRobotDriveSucceded)];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
        return;
    }
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager]
     notifyDataChangeForRobotId:neatoRobot.robotId withKeyCode:[NSNumber numberWithInt:ROBOT_IS_DISCONNECTED] andData:data];
    self.retainedSelf = nil;
    self.delegate = nil;
}

- (void)failedToFormTCPConnectionForRobotId:(NSString *)robotId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setObject:[NSString stringWithFormat:@"%d", ROBOT_STATE_SUSPENDED_CLEANING] forKey:ERROR_DRIVE_RESPONSE_CODE];
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager] notifyDataChangeForRobotId:robotId
                                                                             withKeyCode:[NSNumber numberWithInt:ROBOT_ERROR_IN_CONNECTING]
                                                                                 andData:data];
    self.retainedSelf = nil;
    self.delegate = nil;
}

// Device connection manager callbacks.
- (void)connectedOverTCP2:(NSString*)host toRobotWithId:(NSString *)robotId {
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager]
     notifyDataChangeForRobotId:robotId withKeyCode:[NSNumber numberWithInt:ROBOT_IS_CONNECTED] andData:data];
}

- (void)failedToConnectToTCP2WithError:(NSError *)error forRobot:(NeatoRobot *)robot forcedDisconnected:(BOOL)forcedDisconnected {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager]
     notifyDataChangeForRobotId:robot.robotId withKeyCode:[NSNumber numberWithInt:ROBOT_IS_DISCONNECTED] andData:data];
    self.retainedSelf = nil;
    self.delegate = nil;
}

- (void)driveRobotWithRobotId:(NSString *)robotId navigationControlId:(NSString *)navigationControlId {
    debugLog(@"");
    self.retainedSelf = self;
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setObject:navigationControlId forKey:KEY_NAVIGATION_CONTROL_ID];
    TCPConnectionHelper *tcpConnectionHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
    if ([tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
        debugLog(@"Sending drive robot over TCP to robotId: %@ navigationControlId : %@.", robotId, navigationControlId);
        RobotCommandHelper *commandHelper = [[RobotCommandHelper alloc] init];
        [commandHelper sendCommandOverTCPToRobotWithId:robotId commandId:[NSString stringWithFormat:@"%d", COMMAND_DRIVE_ROBOT] params:params delegate:self];
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:@"Drive Robot action cannot complete as robot connection does not exist." code:UI_ROBOT_NOT_CONNECTED];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(driveRobotFailedWithError:)]) {
                [self.delegate performSelector:@selector(driveRobotFailedWithError:) withObject:error];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
}

- (void)commandSentOverTCP2 {
    debugLog(@"Drive command sent over TCP.");
    // No need to send success callback for 'drive robot' command to UI.
    // Only send callback in case of failure.
}

- (void)failedToSendCommandOverTCPWithError:(NSError *)error {
    debugLog(@"Failed to send drive command with error : %@", [error description]);
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(driveRobotFailedWithError:)]) {
            [self.delegate performSelector:@selector(driveRobotFailedWithError:) withObject:error];
        }
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)stopDriveRobotForRobotId:(NSString *)robotId {
    debugLog(@"");
    self.retainedSelf = self;
    TCPConnectionHelper *tcpConnectionHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
    if (![tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
        // Send error back.
        NSError *error = [AppHelper nserrorWithDescription:@"Robot is not connected" code:UI_ROBOT_NOT_CONNECTED];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(stopRobotDriveFailedWithError:)]) {
                [self.delegate performSelector:@selector(stopRobotDriveFailedWithError:) withObject:error];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
        return;
    }
    // Disconnect the robot connection.
    [tcpConnectionHelper disconnectFromRobot:robotId delegate:self];
}

- (id)isConnectedOverTCPWithRobotId:(NSString *)robotId {
    debugLog(@"");
    BOOL isPeerConnected = NO;
    TCPConnectionHelper *tcpHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
    if (robotId.length == 0) {
        isPeerConnected = [tcpHelper isConnected];
    }
    else {
        isPeerConnected = [tcpHelper isRobotConnectedOverTCP:robotId];
    }
    NSMutableDictionary *data = [[NSMutableDictionary alloc] initWithCapacity:2];
    [data setObject:robotId forKey:KEY_ROBOT_ID];
    [data setObject:[NSNumber numberWithBool:isPeerConnected] forKey:KEY_IS_CONNECTED];
    return data;
}

- (void)notifyTCPDisconnected:(NSNotification *)notification {
    debugLog(@"Tcp disconnected by notification received: %@", notification.userInfo);
    NeatoRobot *robot = [NeatoRobotHelper getRobotForId:[notification.userInfo objectForKey:KEY_ROBOT_ID]];
    robot.robotId = [notification.userInfo objectForKey:KEY_ROBOT_ID];
    [self tcpConnectionDisconnectedWithError:[notification.userInfo objectForKey:KEY_DISCONNECTION_ERROR] forRobot:robot forcedDisconnected:[[notification.userInfo objectForKey:KEY_TCP_FORCED_DISCONNECTED] boolValue]];
}

// It checks if user is already connected to the same robot/other robot on TCP or not.
// If already not connected then it can try for direct connecton.
+ (id)canRequestDirectConnectionWithRobotId:(NSString *)robotId {
    // Check frst if TCP is connected.
    TCPConnectionHelper *tcpConnectionHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
    BOOL tcpConnected = [tcpConnectionHelper isConnected];
    if (tcpConnected) {
        NSInteger errorCode = UI_DIFFERENT_ROBOT_ALREADY_CONNECTED;
        NSString *errorMessage = @"Different robot is currrently being driven.";
        // Check if this robot is already connected on TCP.
        if ([tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
            errorCode = UI_ROBOT_ALREADY_CONNECTED;
            errorMessage = @"Robot already connected.";
        }
        return [AppHelper nserrorWithDescription:errorMessage code:errorCode];
    }
    return [NSNumber numberWithBool:YES];
}

- (void)connectOverTCPWithRobotId:(NSString *)robotId ipAddress:(NSString *)ipAddress {
    self.retainedSelf = self;
    if (ipAddress) {
        NeatoRobot *robot = [[NeatoRobot alloc] init];
        robot.robotId = robotId;
        robot.ipAddress = ipAddress;
        TCPConnectionHelper *tcpHelper = [TCPConnectionHelper sharedTCPConnectionHelper];
        [tcpHelper connectToRobotOverTCP2:robot delegate:self];
    }
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
