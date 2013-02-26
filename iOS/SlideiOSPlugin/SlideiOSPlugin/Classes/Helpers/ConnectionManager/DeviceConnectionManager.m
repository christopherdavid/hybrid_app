#import "DeviceConnectionManager.h"
#import "GetRobotIPHelper.h"
#import "LogHelper.h"
#import "NeatoRobot.h"
#import "TCPConnectionHelper.h"
#import "AppHelper.h"

@interface DeviceConnectionManager()
@property(nonatomic, retain) DeviceConnectionManager *retained_self;
@property(nonatomic, retain) id delegate;
@end
@implementation DeviceConnectionManager

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;

- (void)tryDirectConnection2:(NSString *)robotId delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.delegate = delegate;
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
        if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:)]) {
            [self.delegate performSelector:@selector(failedToConnectToTCP2WithError:) withObject:[AppHelper nserrorWithDescription:@"Failed to get remote device IP. Will not connect over TCP." code:200]];
        }
        self.retained_self = nil;
        self.delegate = nil;
    }
}

- (void)connectToRobotOverTCP2:(NeatoRobot *)robot {
    TCPConnectionHelper *helper = [[TCPConnectionHelper alloc] init];
    [helper connectToRobotOverTCP2:robot delegate:self];
}

- (void)connectedOverTCP:(NSString*)host {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(connectedOverTCP2:)])
    {
        [self.delegate performSelector:@selector(connectedOverTCP2:) withObject:host];
    }
    self.retained_self = nil;
    self.delegate = nil;

}
- (void)tcpConnectionDisconnected:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToConnectToTCP2WithError:)]) {
        [self.delegate performSelector:@selector(failedToConnectToTCP2WithError:) withObject:error];
    }
    self.retained_self = nil;
    self.delegate = nil;
}



@end
