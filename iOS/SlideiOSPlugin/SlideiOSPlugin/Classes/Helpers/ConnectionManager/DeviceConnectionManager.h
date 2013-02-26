#import <Foundation/Foundation.h>
#import "TCPConnectionHelper.h"

@protocol DeviceConnectionManagerProtocol <NSObject>

- (void)connectedOverTCP2:(NSString*)host;
- (void)failedToConnectToTCP2WithError:(NSError *)error;

@end

@interface DeviceConnectionManager : NSObject

- (void)tryDirectConnection2:(NSString *)robotId delegate:(id)delegate;

@end
