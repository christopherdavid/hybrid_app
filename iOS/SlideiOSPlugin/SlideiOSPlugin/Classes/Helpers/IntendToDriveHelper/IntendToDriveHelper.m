#import "IntendToDriveHelper.h"
#import "NeatoServerManager.h"
#import "NeatoRobotCommand.h"
#import "TCPConnectionHelper.h"
#import "AppHelper.h"
#import "NeatoConstants.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"
#import "ProfileDetail.h"
#import "LogHelper.h"
#import "NeatoCommandExpiryHelper.h"

// Default time is 2 minutes.
#define DEFAULT_ROBOT_DRIVE_WIFI_ON_TIME 2 * 60 * 1000

@interface IntendToDriveHelper()
@property(nonatomic, strong) IntendToDriveHelper *retainedSelf;
- (void)requestIntentToDriveForRobotWithId:(NSString *)robotId;
- (id)isDriveRobotAllowedForRobotId:(NSString *)robotId;
- (NSString *)intendToDriveProfileParamsStringWithWifiOnTime:(NSInteger)wifiOnTime;
@end

@implementation IntendToDriveHelper

@synthesize retainedSelf = _retainedSelf;

- (void)requestIntentToDriveForRobotWithId:(NSString *)robotId {
    self.retainedSelf = self;
    id driveStatus = [self isDriveRobotAllowedForRobotId:robotId];
    if ([driveStatus isKindOfClass:[NSNumber class]] && [(NSNumber *)driveStatus boolValue]) {
        // Send intend to drive in the profile parameter of the robot along with
        // the wifi on time and cause agent id.
        NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
        serverManager.delegate = self;
        NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
        robotCommand.robotId = robotId;
        robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
        [robotCommand.profileDict setValue:[self intendToDriveProfileParamsStringWithWifiOnTime:DEFAULT_ROBOT_DRIVE_WIFI_ON_TIME] forKey:KEY_INTEND_TO_DRIVE];
        [serverManager sendCommand:robotCommand];
    }
    else {
        // Not allowed to send drive to robot so send error back.
        NSError *error = (NSError *)driveStatus;
        if ([self.delegate respondsToSelector:@selector(intentToDriveRequestFailedWithError:)]) {
            [self.delegate performSelector:@selector(intentToDriveRequestFailedWithError:) withObject:[AppHelper nserrorWithDescription:[error localizedDescription] code:[error code]]];
            self.retainedSelf = nil;
            self.delegate = nil;
        }
    }
}

// It checks if user is connected to robot on TCP or not.If connected then drive
// robot is allowed otherwise not.
- (id)isDriveRobotAllowedForRobotId:(NSString *)robotId {
    // Check frst if TCP is connected.
    TCPConnectionHelper *tcpConnectionHelper = [[TCPConnectionHelper alloc] init];
    BOOL tcpConnected = [tcpConnectionHelper isConnected];
    if (tcpConnected) {
        NSInteger errorCode = DIFFERENT_ROBOT_ALREADY_CONNECTED;
        NSString *errorMessage = @"Different robot is currrently being driven.";
        // Check if this robot is connected on TCP.
        if ([tcpConnectionHelper isRobotConnectedOverTCP:robotId]) {
            errorCode = ROBOT_ALREADY_CONNECTED;
            errorMessage = @"Robot already connected.";
        }
        return [AppHelper nserrorWithDescription:errorMessage code:errorCode];
    }
    return [NSNumber numberWithBool:YES];
}

// Intend to drive profile params string.
- (NSString *)intendToDriveProfileParamsStringWithWifiOnTime:(NSInteger)wifiOnTime {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] initWithCapacity:2];
    [params setValue:[NSNumber numberWithInteger:wifiOnTime] forKey:KEY_ROBOT_WIFI_ON_TIME_IN_MS];
    [params setValue:[NeatoUserHelper uniqueDeviceIdForUser] forKey:KEY_DEVICE_ID];
    return [AppHelper jsonStringFromNSDictionary:params];
}

- (void)failedtoSendCommandWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(intentToDriveRequestFailedWithError:)]) {
        [self.delegate performSelector:@selector(intentToDriveRequestFailedWithError:) withObject:error];
        self.retainedSelf = nil;
        self.delegate = nil;
    }
}

- (void)command:(NeatoRobotCommand *)command sentWithResult:(NSDictionary *)result {
    debugLog(@"");
    // Set driveRequest for robotId to YES in database.
    [NeatoRobotHelper setDriveRequestForRobotWithId:command.robotId];
    // Update timestamp in db.
    ProfileDetail *profileDetail = [[ProfileDetail alloc] init];
    profileDetail.key = KEY_INTEND_TO_DRIVE;
    profileDetail.timestamp = [result objectForKey:KEY_TIMESTAMP];
    [NeatoRobotHelper updateProfileDetail:profileDetail forRobotWithId:command.robotId];
    // Start a timer if the command is expirable and if a timer is not already in progress.
    if (![[NeatoCommandExpiryHelper expirableCommandHelper] isTimerRunningForRobotId:command.robotId]) {
        [[NeatoCommandExpiryHelper expirableCommandHelper] startCommandTimerForRobotId:command.robotId];
    }
    // Send back success.
    if ([self.delegate respondsToSelector:@selector(intentToDriveRequestSuccededWithResult:)]) {
        [self.delegate intentToDriveRequestSuccededWithResult:result];
        self.delegate = nil;
        self.retainedSelf = nil;
    }
}

@end
