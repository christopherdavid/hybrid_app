#import <Foundation/Foundation.h>
#import "TCPConnectionHelper.h"

@protocol DeviceConnectionManagerProtocol <NSObject>

- (void)connectedOverTCP2:(NSString*)host toRobotWithId:(NSString *)robotId;
- (void)failedToConnectToTCP2WithError:(NSError *)error forRobot:(NeatoRobot *)robot forcedDisconnected:(BOOL)forcedDisconnected;
@end

@interface DeviceConnectionManager : NSObject

- (void)tryDirectConnection2:(NSString *)robotId delegate:(id)delegate;

@end
