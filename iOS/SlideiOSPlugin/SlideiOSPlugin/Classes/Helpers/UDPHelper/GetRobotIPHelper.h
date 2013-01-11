#import <Foundation/Foundation.h>
#import "GCDAsyncUdpSocket.h"

@interface GetRobotIPHelper : NSObject <GCDAsyncUdpSocketDelegate>

-(void) getRobotIPAddress:(NSString *) serialId delegate:(id)delegate action:(SEL)action;
@end
