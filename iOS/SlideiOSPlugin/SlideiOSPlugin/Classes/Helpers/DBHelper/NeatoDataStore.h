#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NeatoSocialNetworks.h"
#import "NeatoNotification.h"
#import "CleaningArea.h"

@class Schedule;
@class ProfileDetail;

@interface NeatoDataStore : NSObject

+(NeatoDataStore *) sharedNeatoDataStore;


// Returns yes if save was successfull
- (BOOL)addCommandToTracker:(NSString *) xmlCommand withRequestId:(NSString *)requestId;
// Returns yes if deletion was successfull
- (BOOL)removeCommandForRequestId:(NSString *)requestId;
- (NSString *)getCommandForRequestId:(NSString *)requestId;

- (void)saveNeatoUser:(NeatoUser *)neatoUser;
- (NeatoUser *)getNeatoUser;
- (void)saveNeatoRobot:(NeatoRobot * )robot forUser:(NSString *)userId;
- (void)saveSocialNetwork:(NeatoSocialNetworks *)network forUser:(NSString *)userId;
- (NSMutableArray *)getAllRobotsForUser:(NSString *)userId;
- (NSMutableArray *)getAllSocialNetworksForUser:(NSString *)userId;
- (NeatoRobot *)getRobotForId:(NSString *)serialNumber;
- (void)updateRobotForRobotId:(NSString *)serialNumber andForName:(NSString *)robotName;
- (void)deleteUserDetails;
- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email;
- (void)deleteRobotForSerialNumber:(NSString *)serialNumber forUserId:(NSString *)userId;
- (void)updatePassword:(NSString *)newPassword;
- (id)setCleaningArea:(CleaningArea *)cleaningArea;
- (id)cleaningAreaForRobotWithId:(NSString *)robotId;
- (void)saveXMPPCallbackId:(NSString *)xmppCallbackId;
- (NSString *)xmppCallbackId;
- (void)removeXMPPCallbackId;


//Scheduling Methods.
- (id)createScheduleForRobotId:(NSString *)robotId forScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId;
- (id)scheduleTypeForScheduleId:(NSString *)scheduleId;
- (id)addBasicScheduleEventData:(NSString *)data withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
- (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withData:(NSString *)data;
- (id)deleteBasicScheduleEventWithId:(NSString *)scheduleEventId;
- (id)basicScheduleEventWithId:(NSString *)scheduleEventId;
- (id)basicScheduleForScheduleId:(NSString *)scheduleId;
- (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId;
- (id)robotIdForScheduleId:(NSString *)scheduleId;
- (id)updateServerScheduleId:(NSString *)serverScheduleId andScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId;
- (id)updateScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId;

// Notification Methods.
- (void)insertOrUpdateNotificaton:(NeatoNotification *)notification forEmail:(NSString *)email;
- (BOOL)notificationsExistForUserWithEmail:(NSString *)email;
- (void)setNotificationsFromNotificationsArray:(NSArray *)notificationOptionsArray forEmail:(NSString *)email;
- (NSArray *)notificationsForUserWithEmail:(NSString *)email;

// ProfileDetails method.
- (id)updateProfileDetail:(ProfileDetail *)profileDetail forRobotWithId:(NSString *)robotId;
- (id)timestampForRobotProfileKey:(NSString *)key forRobotWithId:(NSString *)robotId;
- (id)profileDetailForKey:(NSString *)key robotWithId:(NSString *)robotId;
- (id)deleteProfileDetail:(ProfileDetail *)profileDetail forRobot:(NSString *)robotId;

// Drive Requests APIs.
- (id)setDriveRequestForRobotWithId:(NSString *)robotId;
- (id)driveRequestForRobotWithId:(NSString *)robotId;
- (id)removeDriveRequestForRobotWihId:(NSString *)robotId;
@end
