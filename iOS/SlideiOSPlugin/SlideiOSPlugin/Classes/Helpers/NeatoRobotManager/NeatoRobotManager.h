
#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "TCPConnectionHelper.h"
#import "NeatoRobotAtlas.h"
#import "AtlasGridMetadata.h"

@interface NeatoRobotManager : NSObject

+ (void)findRobotsNearBy:(id) delegate action:(SEL)action;
+ (void)getRobotInfoBySerialId:(NSString *) serialId delegate:(id) delegate action:(SEL) action;
+ (void)connectToRobotOverTCP:(NeatoRobot *) robot delegate:(id<TCPConnectionHelperProtocol>) delegate;
+ (void)diconnectRobotFromTCP:(NSString*) robotId delegate:(id) delegate;
+ (void)sendStartCleaningTo:(NSString *) roboId delegate:(id) delegate;
+ (void)sendStopCleaningTo:(NSString *) roboId delegate:(id) delegate;
+ (void)sendCommand:(NSString *) commandId to:(NSString*) robotId delegate:(id) delegate;
+ (void)logoutFromXMPP:(id) delegate;
+ (void)getRobotAtlasMetadataForRobotId:(NSString *) robotId delegate:(id) delegate;
+ (void)getAtlasGridMetadata:(NSString *) robotId gridId:(NSString *) gridId delegate:(id) delegate;
+ (void)updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas  delegate:(id) delegate;
+ (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId delegate:(id)delegate;
+ (void)getDetailsForRobotWithId:(NSString *)robotId delegate:(id)delegate;
+ (void)onlineStatusForRobotWithId:(NSString *)robotId delegate:(id)delegate;
+ (void)tryDirectConnection2:(NSString *)robotId delegate:(id)delegate;
+ (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params delegate:(id)delegate;

@end
