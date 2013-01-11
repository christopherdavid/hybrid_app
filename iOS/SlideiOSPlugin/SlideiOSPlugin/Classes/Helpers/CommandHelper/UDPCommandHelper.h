#import <Foundation/Foundation.h>

@interface UDPCommandHelper : NSObject

-(NSData*) getFindRobotsCommand;
-(NSData *) getRobotIPAddressCommand:(NSString *) serialId;

@end
