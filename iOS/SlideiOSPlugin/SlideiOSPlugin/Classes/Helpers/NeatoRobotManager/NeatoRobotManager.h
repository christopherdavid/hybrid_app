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
+ (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType;
+ (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId;
+ (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData;
+ (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
+ (id)getSchedueEventDataWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
+ (id)getScheduleDataForScheduleId:(NSString *)scheduleId;
+ (void)getScheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType delegate:(id)delegate;
+ (void)updateScheduleForScheduleId:(NSString *)scheduleId delegate:(id)delegate;

+ (void)setRobotSchedule:(NSArray *)schedulesArray forRobotId:(NSString *)robotId ofType:(NSString *)schedule_type delegate:(id)delegate;
+ (void)getRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type delegate:(id)delegate;
+ (void)deleteRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type delegate:(id)delegate;
@end
