#import <Foundation/Foundation.h>

@interface NetworkUtils : NSObject

- (NSString *)getIPAddress;
- (NSString *) getMacAddress;
- (NSString *) getSubnetIPAddress;
- (BOOL) isCommandFromRemoteDevice:(NSString *) remoteHost;

@end
