
#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "TCPConnectionHelper.h"

@interface NeatoRobotManager : NSObject

+(void) findRobotsNearBy:(id) delegate action:(SEL)action;
+(void) getRobotInfoBySerialId:(NSString *) serialId delegate:(id) delegate action:(SEL) action;
+(void) connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id<TCPConnectionHelperProtocol>) delegate;
+(void) diconnectRobotFromTCP:(NSString*) robotId delegate:(id) delegate;
+(void) sendStartCleaningTo:(NSString *) roboId delegate:(id) delegate;
+(void) sendStopCleaningTo:(NSString *) roboId delegate:(id) delegate;
+(void) sendCommand:(NSString *) commandId to:(NSString*) robotId delegate:(id) delegate;
+(void) logoutFromXMPP:(id) delegate;

@end
