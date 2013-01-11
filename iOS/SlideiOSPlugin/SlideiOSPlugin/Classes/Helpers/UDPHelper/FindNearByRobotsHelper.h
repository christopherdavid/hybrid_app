#import <Foundation/Foundation.h>
#import "GCDAsyncUdpSocket.h"

@interface FindNearByRobotsHelper : NSObject <GCDAsyncUdpSocketDelegate>

-(void) findNearbyRobots:(id) delegate action:(SEL) action;

@end
