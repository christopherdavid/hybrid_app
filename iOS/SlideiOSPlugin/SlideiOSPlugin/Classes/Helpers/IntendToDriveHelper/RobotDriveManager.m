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
#import "DeviceConnectionManager.h"
#import "XMPPRobotDataChangeManager.h"
#import "RobotCommandHelper.h"
#import "ProfileDetail.h"

@interface RobotDriveManager()
@property(nonatomic, strong) RobotDriveManager *retainedSelf;
- (void)robotWithRobotId:(NSString *)robotId isReadyToDriveWithIP:(NSString *)robotIp;
- (void)robotWithRobotId:(NSString *)robotId isNotAvailableToDriveWithErrorCode:(NSInteger)errorCode;
- (void)notifyCannotDriveForRobotWIthRobotId:(NSString *)robotId withErrorResponseCode:(NSInteger)errorCode;
- (BOOL)isRobotDriveRequestedForRobotId:(NSString *)robotId;
- (void)tryTCPConnectionToRobotId:(NSString *)robotId withIpAddress:(NSString *)ipAddress;
- (void)driveRobotWithRobotId:(NSString *)robotId navigationControlId:(NSString *)navigationControlId;
- (void)cancelIntendToDriveForRobotId:(NSString *)robotId;
- (void)stopDriveRobotForRobotId:(NSString *)robotId;
@end

@implementation RobotDriveManager

@synthesize delegate = _delegate, retainedSelf = _retainedSelf;

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
    [data setObject:[NSString stringWithFormat:@"%d", errorCode] forKey:ERROR_DRIVE_RESPONSE_CODE];
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

// If ipAddress is not nil then it tries to connect to robot otherwise it does a
// UDP broadcast to get IP and then tries to connect to robot.
- (void)tryTCPConnectionToRobotId:(NSString *)robotId withIpAddress:(NSString *)ipAddress {
    if (ipAddress) {
        NeatoRobot *robot = [[NeatoRobot alloc] init];
        robot.robotId = robotId;
        robot.ipAddress = ipAddress;
        TCPConnectionHelper *tcpHelper = [[TCPConnectionHelper alloc] init];
        [tcpHelper connectToRobotOverTCP2:robot delegate:self];
    }
    else {
        DeviceConnectionManager *connectionManager = [[DeviceConnectionManager alloc] init];
        [connectionManager tryDirectConnection2:robotId delegate:self];
    }
}

// TCPConnectionHelper callbacks.
- (void)connectedOverTCP:(NSString*)host toRobotWithId:(NSString *)robotId {
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
    TCPConnectionHelper *tcpConnectionHelper = [[TCPConnectionHelper alloc] init];
    if ([tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
        debugLog(@"Sending drive robot over TCP to robotId: %@ navigationControlId : %@.", robotId, navigationControlId);
        RobotCommandHelper *commandHelper = [[RobotCommandHelper alloc] init];
        [commandHelper sendCommandOverTCPToRobotWithId:robotId commandId:[NSString stringWithFormat:@"%d", COMMAND_DRIVE_ROBOT] params:params delegate:self];
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:@"Drive Robot action cannot complete as robot connection does not exist." code:ROBOT_NOT_CONNECTED];
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
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(driveRobotSent)]) {
            [self.delegate performSelector:@selector(driveRobotSent)];
        }
        self.delegate = nil;
        self.retainedSelf = nil;
    });
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

- (void)cancelIntendToDriveForRobotId:(NSString *)robotId {
    // If the user is already connected to the robot via direct connection, return with a error that cannot cancel drive robot.
    self.retainedSelf = self;
    TCPConnectionHelper *tcpConnectionHelper = [[TCPConnectionHelper alloc] init];
    if ([tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
        // Send error back.
        NSError *error = [AppHelper nserrorWithDescription:@"Robot already connected cannot cancel intend to drive." code:ROBOT_ALREADY_CONNECTED];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(cancelIntendToDriveFailedWithError:)]) {
                [self.delegate performSelector:@selector(cancelIntendToDriveFailedWithError:) withObject:error];
            }
            self.retainedSelf = nil;
            self.delegate = nil;
        });
        return;
    }
    if ([self isRobotDriveRequestedForRobotId:robotId]) {
        NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
        serverManager.delegate = self;
        [serverManager deleteProfileDetailKey:KEY_INTEND_TO_DRIVE forRobotWithId:robotId notfify:YES];
    }
    else {
        // Send error back.
        NSError *error = [AppHelper nserrorWithDescription:@"No robot drive request found" code:ROBOT_NO_DRIVE_REQUEST_FOUND];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(cancelIntendToDriveFailedWithError:)]) {
                [self.delegate performSelector:@selector(cancelIntendToDriveFailedWithError:) withObject:error];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
}

- (void)deleteProfileDetailKeySuccededforRobotId:(NSString *)robotId {
    debugLog(@"Delete profile detail key succeded on server.");
    // Untrack robot.
    [NeatoRobotHelper removeDriveRequestForRobotWihId:robotId];
    // Delete profile key from database.
    ProfileDetail *profile = [[ProfileDetail alloc] init];
    profile.key = KEY_INTEND_TO_DRIVE;
    [NeatoRobotHelper deleteProfileDetail:profile forRobot:robotId];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(cancelIntendToDriveSucceded)]) {
            [self.delegate performSelector:@selector(cancelIntendToDriveSucceded)];
        }
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

// TODO: For now ignoring serve error and sending our error code and message.
// Android team have created a map as to when we have to send server error code
// and we have to send local error code.
- (void)failedToDeleteProfileDetailKeyWithError:(NSError *)error {
    debugLog(@"Delete profile detail key failed.");
    NSError *localError = [AppHelper nserrorWithDescription:@"Unable to cancel intend to drive" code:ROBOT_UNABLE_TO_CANCEL_INTEND_TO_DRIVE];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(cancelIntendToDriveFailedWithError:)]) {
            [self.delegate performSelector:@selector(cancelIntendToDriveFailedWithError:) withObject:localError];
        }
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)stopDriveRobotForRobotId:(NSString *)robotId {
    debugLog(@"");
    self.retainedSelf = self;
    TCPConnectionHelper *tcpConnectionHelper = [[TCPConnectionHelper alloc] init];
    if (![tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
        // Send error back.
        NSError *error = [AppHelper nserrorWithDescription:@"Robot is not connected" code:ROBOT_NOT_CONNECTED];
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
    TCPConnectionHelper *tcpHelper = [[TCPConnectionHelper alloc] init];
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

@end
