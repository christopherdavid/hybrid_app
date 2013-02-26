#import <Foundation/Foundation.h>
#import "GCDAsyncUdpSocket.h"

@interface GetRobotIPHelper : NSObject <GCDAsyncUdpSocketDelegate>

- (void)robotIPAddress:(NSString *)serialId delegate:(id)delegate action:(SEL)action;
@end
