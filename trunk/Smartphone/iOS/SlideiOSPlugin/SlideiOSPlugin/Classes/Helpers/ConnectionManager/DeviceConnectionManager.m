#import "DeviceConnectionManager.h"
#import "GetRobotIPHelper.h"
#import "LogHelper.h"
#import "NeatoRobot.h"
#import "TCPConnectionHelper.h"
#import "AppHelper.h"
#import "NeatoRobotHelper.h"

@interface DeviceConnectionManager()
@property(nonatomic, retain) DeviceConnectionManager *retained_self;
@property(nonatomic, retain) id delegate;
@property(nonatomic, strong) NSString *robotId;
@end
@implementation DeviceConnectionManager

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize robotId = _robotId;

- (void)tryDirectConnection2:(NSString *)robotId delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.delegate = delegate;
    self.robotId = robotId;
    GetRobotIPHelper *ipHelper = [[GetRobotIPHelper alloc] init];
    [ipHelper robotIPAddress:robotId delegate:self action:@selector(robotInfoBySerialIdHandler:)];
}

- (void)robotInfoBySerialIdHandler:(id)value {
    debugLog(@"");
    if (value) {
        debugLog(@"Got remote device IP. Will try to connect over TCP.");
        // Now we should associate the user with the robot
        NeatoRobot *robot = (NeatoRobot *) value;
        debugLog(@"Robot IP address = %@", robot.ipAddress);
        [self connectToRobotOverTCP2:robot];
    }
    else {
        debugLog(@"Failed to get remote device IP. Will not connect over TCP.");
        if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:forRobot:forcedDisconnected:)]) {
            NeatoRobot *neatoRobot = [NeatoRobotHelper getRobotForId:self.robotId];
            neatoRobot.robotId = self.robotId;
            if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:forRobot:forcedDisconnected:)]) {
                [self.delegate failedToConnectToTCP2WithError:[AppHelper nserrorWithDescription:@"Failed to get remote device IP. Will not connect over TCP." code:200] forRobot:neatoRobot forcedDisconnected:NO];
                self.retained_self = nil;
                self.delegate = nil;
            }
        }
    }
}

- (void)connectToRobotOverTCP2:(NeatoRobot *)robot {
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    [helper connectToRobotOverTCP2:robot delegate:self];
}

- (void)connectedOverTCP:(NSString*)host toRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(connectedOverTCP2:toRobotWithId:)]) {
        [self.delegate performSelector:@selector(connectedOverTCP2:toRobotWithId:) withObject:host withObject:robotId];
    }
}

- (void)tcpConnectionDisconnectedWithError:(NSError *)error forRobot:(NeatoRobot *)neatoRobot forcedDisconnected:(BOOL)forcedDisconneted {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:forRobot:forcedDisconnected:)]) {
        [self.delegate failedToConnectToTCP2WithError:error forRobot:neatoRobot forcedDisconnected:forcedDisconneted];
    }
    self.retained_self = nil;
    self.delegate = nil;
}

@end
