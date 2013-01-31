#import <Foundation/Foundation.h>

@interface UDPCommandHelper : NSObject

-(NSString*) getFindRobotsCommand:(NSString *) requestId;
-(NSString *) getRobotIPAddressCommandRequestId:(NSString *) requestId robotId:(NSString *) robotId;

@end
