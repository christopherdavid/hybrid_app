#import <Foundation/Foundation.h>
#import "NeatoRobot.h"
#import "NeatoRobotManager.h"
#import "XMPPConnectionHelper.h"
#import "Schedule.h"


@protocol RobotManagerProtocol <NSObject>

- (void)foundRobotsNearby:(id)value callbackId:(NSString *)callbackId;
- (void)gotRemoteRobotIP:(id)value callbackId:(NSString *)callbackId;
- (void)connectedOverTCP:(NSString*) host callbackId:(NSString *)callbackId;
- (void)tcpConnectionDisconnected:(NSError *) error callbackId:(NSString *)callbackId;
- (void)commandSentOverTCP:(NSString *)callbackId;
- (void)receivedDataOverTCP:(NSData *)data callbackId:(NSString *)callbackId;
- (void)didConnectOverXMPP:(NSString *)callbackId;
- (void)didDisConnectFromXMPP:(NSString *)callbackId ;
- (void)commandSentOverXMPP:(NSString *)callbackId;
- (void)commandReceivedOverXMPP:(XMPPMessage *)message sender:(XMPPStream *) sender callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverXMPP:(NSString *)callbackId;
- (void)failedToSendCommandOverTCP:(NSString *)callbackId;
- (void)getAtlasDataFailed:(NSError *)error callbackId:(NSString *)callbackId;
- (void)gotAtlasData:(NeatoRobotAtlas *)robotAtlas  callbackId:(NSString *)callbackId;
- (void)gotAtlasGridMetadata:(AtlasGridMetadata *) atlasGridMetadata callbackId:(NSString *) callbackId;
- (void)getAtlasGridMetadataFailed:(NSError *) error callbackId:(NSString *)callbackId;
- (void)atlasMetadataUpdated:(NeatoRobotAtlas *) robotAtlas callbackId:(NSString *)callbackId;
- (void)failedToUpdateAtlasMetadataWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToUpdateRobotNameWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)gotRobotDetails:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId;
- (void)failedToGetRobotDetailsWihError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)failedToConnectToTCP2WithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)connectedOverTCP2:(NSString*)host callbackId:(NSString *)callbackId;
- (void)tcpConnectionDisconnected2:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverTCPWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToSendCommandOverXMPP2:(NSString *)callbackId;
- (void)commandSentOverTCP2:(NSString *)callbackId;
- (void)commandSentOverXMPP2:(NSString *)callbackId;
- (void)failedToGetScheduleEventsWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)gotScheduleEventsForSchedule:(Schedule *)scheduleEvents ofType:(NSInteger)scheduleType forRobotWithId:(NSString *)robotId  callbackId:(NSString *)callbackId;
- (void)setScheduleSuccess:(NSString *)message callbackId:(NSString *)callbackId;
- (void)getScheduleSuccess:(NSDictionary *)jsonObject callbackId:(NSString *)callbackId;
- (void)setScheduleError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)getScheduleError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)deleteScheduleSuccess:(NSString *)message callbackId:(NSString *)callbackId;
- (void)deleteScheduleError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)updatedSchedule:(NSString *)scheduleId callbackId:(NSString *)callbackId;
- (void)updateScheduleError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedToEnableDisableScheduleWithError:(NSError *) error callbackId:(NSString *)callbackId;
- (void)enabledDisabledScheduleWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId;
- (void)gotScheduleStatus:(NSDictionary *)status callbackId:(NSString *)callbackId;
- (void)failedToGetScheduleStatusWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)virtualOnlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)failedToGetRobotVirtualOnlineStatusWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)failedtoSendCommandWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)commandSentWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId;
- (void)gotCleaningStateWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId;
- (void)failedToGetCleaningStateWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)intentToDriveRequestSuccededWithResult:(NSDictionary *)result callbackId:(NSString *)callbackId;
- (void)intentToDriveRequestFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)driveRobotSentforCallBackId:(NSString *)callbackId;
- (void)driveRobotFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)cancelIntendToDriveSuccededForCallbackId:(NSString *)callbackId;
- (void)cancelIntendToDriveFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
- (void)stopRobotDriveSuccededForCallbackId:(NSString *)callbackId;
- (void)stopRobotDriveFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
@end

@interface RobotManagerCallWrapper : NSObject

@property(nonatomic, weak) id delegate;

- (void)findRobotsNearBy:(NSString *)callbackId;
- (void)connectToRobotOverTCP:(NeatoRobot *)robot delegate:(id)delegate callbackId:(NSString *) callbackId;
- (void)diconnectRobotFromTCP:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendStartCleaningTo:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendStopCleaningTo:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendCommandToRobot:(NSString *)robotId commandId:(NSString *)commandId callbackId:(NSString *)callbackId;
- (void)tryDirectConnection:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getRobotAtlasMetadataForRobotId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getAtlasGridMetadata:(NSString *)robotId gridId:(NSString *)gridId  callbackId:(NSString *)callbackId;
- (void)updateRobotAtlasData:(NeatoRobotAtlas *)robotAtlas callbackId:(NSString *)callbackId;
- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getDetailsForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)onlineStatusForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)tryDirectConnection2:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendCommandToRobot2:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId;
- (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType;
- (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId;
- (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData;
- (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
- (id)scheduleEventDataWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
- (id)scheduleDataForScheduleId:(NSString *)scheduleId;
- (void)scheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType callbackId:(NSString *)callbackId;
- (void)updateScheduleForScheduleId:(NSString *)scheduleId callbackId:(NSString *)callbackId;
- (void)setRobotSchedule:(NSArray *)schedulesArray forRobotId:(NSString *)robotId ofType:(NSString *)schedule_type callbackId:(NSString *)callbackId;
- (void)getRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type callbackId:(NSString *) callbackId;
- (void)deleteRobotScheduleForRobotId:(NSString *)robotId ofType:(NSString *)schedule_type callbackId:(NSString *)callbackId;
- (void)enabledDisable:(BOOL)enable schedule:(int)scheduleType forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email callbackId:(NSString *)callbackId;
- (void)turnVacuumOnOff:(int)on forRobotWithId:(NSString *)robotId withUserEmail:(NSString *)email withParams:(NSDictionary *)params commandId:(NSString *)commandId callbackId:(NSString *)callbackId;
- (void)isScheduleType:(NSString *)scheduleType enabledForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (id)setSpotDefinitionForRobotWithId:(NSString *)robotId cleaningAreaLength:(int)cleaningAreaLength cleaningAreaHeight:(int)cleaningAreaHeight;
- (id)spotDefinitionForRobotWithId:(NSString *)robotId;
- (void)virtualOnlineStatusForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)getCleaningStateForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)requestIntentToDriveForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)driveRobotWithId:(NSString *)robotId navigationControlId:(NSString *)navigationControlId callbackId:(NSString *)callbackId;
- (void)cancelIntendToDriveForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)stopRobotDriveForRobotWithId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (id)isConnectedOverTCPWithRobotId:(NSString *)robotId callbackId:(NSString *)callbackId;
- (void)sendCommandOverTCPToRobotWithId:(NSString *)robotId commandId:(NSString *)commandId params:(NSDictionary *)params callbackId:(NSString *)callbackId;
@end
