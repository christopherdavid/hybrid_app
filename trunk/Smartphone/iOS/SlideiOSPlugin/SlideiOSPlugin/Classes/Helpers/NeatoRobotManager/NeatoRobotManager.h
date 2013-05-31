#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "TCPConnectionHelper.h"
#import "NeatoRobotAtlas.h"
#import "AtlasGridMetadata.h"

@interface NeatoRobotManager : NSObject

+ (void)sendStartCleaningTo:(NSString *) roboId delegate:(id) delegate;
+ (void)sendStopCleaningTo:(NSString *) roboId delegate:(id) delegate;
+ (void)sendCommand:(NSString *) commandId to:(NSString*) robotId delegate:(id) delegate;
@end
