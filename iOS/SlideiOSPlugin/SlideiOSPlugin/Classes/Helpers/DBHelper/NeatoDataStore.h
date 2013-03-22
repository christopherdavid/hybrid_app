#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NeatoSocialNetworks.h"

@class Schedule;

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


//Scheduling Methods.
- (id)createScheduleForRobotId:(NSString *)robotId forScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId;
- (id)getScheduleTypeForScheduleId:(NSString *)scheduleId;
- (id)addBasicScheduleEventData:(NSString *)xmlData withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId;
- (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withXMLData:(NSString *)xmlData;
- (id)deleteBasicScheduleEventWithId:(NSString *)scheduleEventId;
- (id)getBasicScheduleEventWithId:(NSString *)scheduleEventId;
- (id)getBasicScheduleForScheduleId:(NSString *)scheduleId;
- (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId;
- (id)getRobotIdForScheduleId:(NSString *)scheduleId;
- (id)updateScheduleWithScheduleId:(NSString *)scheduleId withServerScheduleId:(NSString *)serverScheduleId andXmlDataVersion:(NSString *)xmlDataVersion;
- (id)updateScheduleWithScheduleId:(NSString *)scheduleId forXmlDataVersion:(NSString *)xmlDataVersion;
@end
