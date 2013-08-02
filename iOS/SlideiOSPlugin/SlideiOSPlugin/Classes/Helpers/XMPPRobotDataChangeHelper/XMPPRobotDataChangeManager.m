#import "XMPPRobotDataChangeManager.h"
#import "NeatoConstants.h"
#import "LogHelper.h"
#import "CommandsHelper.h"
#import "NeatoServerManager.h"
#import "NeatoRobotHelper.h"
#import "ProfileDetail.h"
#import "XMPPRobotCleaningStateHelper.h"
#import "NeatoRobotHelper.h"
#import "NeatoCommandExpiryHelper.h"
#import "XMPP.h"
#import "NeatoUserHelper.h"
#import "AppHelper.h"
#import "RobotDriveManager.h"

#define NOTIFICATION_UPDATE_UI_FOR_XMPP_DATA_CHANGE @"com.neato.plugin.xmpp.updateUI"
static XMPPRobotDataChangeManager *sharedInstance  = nil;

@interface XMPPRobotDataChangeManager()
// UI update notification observer.
@property (nonatomic, weak) id robotStateChangeObserver;

- (BOOL)isNotificationLocal:(NSDictionary *)notificationData;
- (void)processXMPPRobotDataChangedForRobotId:(NSString *)robotId;
- (void)notifyIfRobotProfileHasChanged:(NSDictionary *)robotProfile;
- (BOOL)hasDataChangedForKey:(NSString *)key withRobotProfile:(NSDictionary *)robotProfile;
- (BOOL)hasRobotCleaningStateChangedForProfile:(NSDictionary *)robotProfile;
- (BOOL)hasRobotCurrentStateChangedForProfile:(NSDictionary *)robotProfile;
- (void)notifyDataChangeForRobotId:(NSString *)robotId withKeyCode:(NSNumber *)key andData:(NSDictionary *)data;
- (void)notifyCleaningStateChangedForProfile:(NSDictionary *)robotProfile;
- (BOOL)hasRobotNameChangedForProfile:(NSDictionary *)robotProfile;
- (void)notifyRobotNameChangeForProfile:(NSDictionary *)robotProfile;
- (BOOL)hasRobotScheduleStateChangedForProfile:(NSDictionary *)robotProfile;
- (void)notifyScheduleStateChangeForProfile:(NSDictionary *)robotProfile;
- (BOOL)hasRobotScheduleUpdatedForProfile:(NSDictionary *)robotProfile;
- (void)notifyScheduleUpdatedForProfile:(NSDictionary *)robotProfile;
@end
@implementation XMPPRobotDataChangeManager
@synthesize robotStateChangeObserver = _robotStateChangeObserver;

+ (id)sharedXmppDataChangeManager {
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[XMPPRobotDataChangeManager alloc] init];
    });
    return sharedInstance;
}

- (void)startListeningRobotDataChangeNotificationsFor:(id)notificationObserver {
    debugLog(@"");
    // Remove any exisiting observer before adding a new one
    // so that we don't have multiple observers.
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    if (self.robotStateChangeObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:self.robotStateChangeObserver];
    }
    
    self.robotStateChangeObserver = notificationObserver;
    // Register self for robot data change notification.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(xmppRobotDataChanged:) name:NOTIFICATION_XMPP_DATA_CHANGE object:nil];
    // Register caller for robot state changed notification.
    if (self.robotStateChangeObserver) {
        [[NSNotificationCenter defaultCenter] addObserver:self.robotStateChangeObserver selector:@selector(updateUIForRobotDataChangeNotification:) name:NOTIFICATION_UPDATE_UI_FOR_XMPP_DATA_CHANGE object:nil];
    }
}

- (void)xmppRobotDataChanged:(NSNotification *)notification {
    debugLog(@"XMPP notification received: %@", [notification.userInfo objectForKey:KEY_XMPP_MESSAGE]);
    @synchronized (self) {
        CommandsHelper *commandsHelper = [[CommandsHelper alloc] init];
        NSDictionary *notificationData = [commandsHelper parseXMPPDataChangeNotification:[notification.userInfo objectForKey:KEY_XMPP_MESSAGE]];
        if ([self isNotificationLocal:notificationData]) {
            // Data changed due to local changes. Don't process data or update the UI
            debugLog(@"Notification causing agent Id matches with current device.");
            return;
        }
        NeatoRobot *robot = [NeatoRobotHelper getRobotForId:[notificationData objectForKey:KEY_ROBOT_ID]];
        // Stop command timer if already running
        if([robot.chatId isEqual:[notification.userInfo objectForKey:KEY_CHAT_ID]] && [[NeatoCommandExpiryHelper expirableCommandHelper] isTimerRunningForRobotId:robot.serialNumber]) {
            [[NeatoCommandExpiryHelper expirableCommandHelper] stopCommandTimerForRobotId:robot.serialNumber];
        }
        [self processXMPPRobotDataChangedForRobotId:robot.serialNumber];
    }
}

- (void)processXMPPRobotDataChangedForRobotId:(NSString *)robotId {
    // Server call to get robot profile details.
    NeatoServerManager *serverManager = [[NeatoServerManager alloc] init];
    serverManager.delegate = self;
    [serverManager profileDetails2ForRobotWithId:robotId];
}

- (void)gotRobotProfileDetails2WithResult:(NSDictionary *)result {
    NSDictionary *neatoResult = [result valueForKeyPath:NEATO_RESPONSE_RESULT];
    NSDictionary *robotProfileDetails = [neatoResult valueForKeyPath:NEATO_PROFILE_DETAILS];
    debugLog(@"RobotProfileDetails received from server : %@", robotProfileDetails);
    [self notifyIfRobotProfileHasChanged:robotProfileDetails];
}

- (void)failedToGetRobotProfileDetails2WithError:(NSError *)error {
    debugLog(@"");
    // TODO: Retry atleast once
}

// Checks if the downloaded data is different than what we have locally.
// If the data has changed, local data gets updated and UI is notified of the change.
- (void)notifyIfRobotProfileHasChanged:(NSDictionary *)robotProfile {
    debugLog(@"");
    // Check if robot name has changed.
    BOOL robotNameChanged = [self hasRobotNameChangedForProfile:robotProfile];
    if (robotNameChanged) {
        [self notifyRobotNameChangeForProfile:robotProfile];
    }
    // Check if robot cleaning state has changed.
    BOOL robotCleaningCommandChanged = [self hasRobotCleaningStateChangedForProfile:robotProfile];
    // Check if robot current state has changed.
    BOOL robotCurrentStateChanged = [self hasRobotCurrentStateChangedForProfile:robotProfile];
    //  Notify only when one of the virtual or current state has changed.
    if (robotCleaningCommandChanged || robotCurrentStateChanged) {
        [self notifyCleaningStateChangedForProfile:robotProfile];
    }
    // Check if schedule state has changed.
    BOOL robotScheduleStateChanged = [self hasRobotScheduleStateChangedForProfile:robotProfile];
    if (robotScheduleStateChanged) {
        [self notifyScheduleStateChangeForProfile:robotProfile];
    }
    // Check if schedule has changed
    BOOL robotScheduleUpdated = [self hasRobotScheduleUpdatedForProfile:robotProfile];
    if (robotScheduleUpdated) {
        [self notifyScheduleUpdatedForProfile:robotProfile];
    }
    // Check for intend to drive.
    BOOL robotIntendToDriveStatusChanged = [self hasRobotIntendToDriveStatusChangedForProfile:robotProfile];
    if (robotIntendToDriveStatusChanged) {
        [self processRobotIntendToDriveForProfile:robotProfile];
    }
    // Check for available to drive.
    BOOL robotDriveAvailableStatusChanged = [self hasRobotDriveAvailableStatusChangedForProfile:robotProfile];
    if (robotDriveAvailableStatusChanged) {
        [self processRobotAvailableToDriveForProfile:robotProfile];
    }
}

- (BOOL)hasRobotIntendToDriveStatusChangedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_INTEND_TO_DRIVE withProfile:robotProfile];
}

- (BOOL)hasRobotDriveAvailableStatusChangedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_AVAILABLE_TO_DRIVE withProfile:robotProfile];
}

- (BOOL)hasRobotNameChangedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_NAME withProfile:robotProfile];
}

- (BOOL)hasRobotCleaningStateChangedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_ROBOT_CLEANING_COMMAND withProfile:robotProfile];
}

- (BOOL)hasRobotCurrentStateChangedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_ROBOT_CURRENT_STATE withProfile:robotProfile];
}

- (BOOL)hasRobotScheduleStateChangedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_ENABLE_BASIC_SCHEDULE withProfile:robotProfile];
}

- (BOOL)hasRobotScheduleUpdatedForProfile:(NSDictionary *)robotProfile {
    return [self updateDataTimestampIfChangedForKey:KEY_ROBOT_SCHEDULE_UPDATED withProfile:robotProfile];
}

// This method returns YES in two cases :
// 1. Data is updated (timestamp returned in robot profile details from server
// is greater than in our database).
// 2. If a key doesn't exist in robot profile details from server and it exists
// in our database.(This will happen when cleaning command is cleared by robot
// and in notification that key won't exist even though it will there in our
// database.)
- (BOOL)updateDataTimestampIfChangedForKey:(NSString *)key withProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    @synchronized (self) {
        if ([robotProfile objectForKey:key]) {
            if ([self hasDataChangedForKey:key withRobotProfile:robotProfile]) {
                // Update timestamp for key in database.
                ProfileDetail *profileDetail = [[ProfileDetail alloc] init];
                profileDetail.key = key;
                profileDetail.timestamp = [NSNumber numberWithLongLong:[[[robotProfile objectForKey:key] objectForKey:KEY_TIMESTAMP] longLongValue]];
                [NeatoRobotHelper updateProfileDetail:profileDetail forRobotWithId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE]];
                return YES;
            }
            debugLog(@"Data has not changed for key : %@", key);
            return NO;
        }
        else {
            // Delete the key from database.
            // Return true only if the delete was successful. If the key is not
            // present in database then return false.
            id dbResult = [NeatoRobotHelper profileDetailForKey:key robotWithId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE]];
            if ([dbResult isKindOfClass:[NSError class]]) {
                return NO;
            }
            if (dbResult) {
                // Key exists in database now delete it.
                debugLog(@"Deleting profile detail record from database with Key = %@ for robotId = %@.", key, [[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE]);
                id deletedRobotProfile = [NeatoRobotHelper deleteProfileDetail:(ProfileDetail *)dbResult forRobot:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE]];
                if ([deletedRobotProfile isKindOfClass:[NSError class]]) {
                    return NO;
                }
                return YES;
            }
            // Key doesn't exists in database.
            return NO;
        }
    }
}

- (BOOL)hasDataChangedForKey:(NSString *)key withRobotProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    // TODO: Hack!! timestamp for key 'name' is always 0 (Server bug).
    if ([key isEqualToString:KEY_NAME]) {
        return YES;
    }
    id dbResult = [NeatoRobotHelper profileDetailForKey:key robotWithId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE]];
    if ([dbResult isKindOfClass:[NSError class]]) {
        return NO;
    }
    ProfileDetail *dbProfileDetail = (ProfileDetail *)dbResult;
    NSString *serverTimstamp = [[robotProfile objectForKey:key] objectForKey:@"timestamp"];
    debugLog(@"ServerTimetamp : %@ \n DBTimestamp : %@ for Key : %@", serverTimstamp, dbProfileDetail.timestamp, key);
    if (dbProfileDetail) {
        if ([serverTimstamp longLongValue] > [dbProfileDetail.timestamp longLongValue]) {
            return YES;
        }
        else {
            return NO;
        }
    }
    else {
        // Profile detail for this key is not there in database so return YES.
        return YES;
    }
}

- (void)notifyCleaningStateChangedForProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    // Update UI for current state change notification.
    NSInteger currentState = [XMPPRobotCleaningStateHelper robotCurrentStateFromRobotProfile:robotProfile];
    if (currentState != ROBOT_STATE_INVALID) {
        NSMutableDictionary *stateData = [[NSMutableDictionary alloc] init];
        [stateData setObject:[NSString stringWithFormat:@"%d", currentState] forKey:KEY_ROBOT_CURRENT_STATE];
        [self notifyDataChangeForRobotId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE] withKeyCode:[NSNumber numberWithInt:ROBOT_CURRENT_STATE_CHANGED_CODE] andData:stateData];
    }
    // Update UI for actual state update notification.
    // Actual state is set to virtual state if it is valid otherwise
    // current state is set.
    NSInteger state = [XMPPRobotCleaningStateHelper robotActualStateFromRobotProfile:robotProfile];
    if (state != ROBOT_STATE_INVALID) {
        NSMutableDictionary *stateData = [[NSMutableDictionary alloc] init];
        [stateData setObject:[NSString stringWithFormat:@"%d", state] forKey:KEY_ROBOT_STATE_UPDATE];
        [self notifyDataChangeForRobotId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE] withKeyCode:[NSNumber numberWithInt:ROBOT_STATE_UPDATE_CODE] andData:stateData];
    }
}

- (void)notifyRobotNameChangeForProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSString *newRobotName = [[robotProfile objectForKey:KEY_NAME] objectForKey:KEY_VALUE];
    NSString *robotId = [[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE];
    if (!newRobotName) {
        debugLog(@"Empty robot name in robot profile details.");
        return;
    }
    NeatoRobot *robot = [NeatoRobotHelper getRobotForId:robotId];
    if (!robot) {
        debugLog(@"No robot with robotId = %@ in database.",robotId);
        return;
    }
    if ([robot.name isEqualToString:newRobotName]) {
        debugLog(@"Robot name has not changed.");
        return;
    }
    // Update database with new robot name.
    robot.name = newRobotName;
    [NeatoRobotHelper saveNeatoRobot:robot];
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setObject:newRobotName forKey:KEY_ROBOT_NAME];
    [self notifyDataChangeForRobotId:robotId withKeyCode:[NSNumber numberWithInt:ROBOT_NAME_UPDATE] andData:data];
}

- (void)notifyScheduleStateChangeForProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSString *basicScheduleState = [[robotProfile objectForKey:KEY_ENABLE_BASIC_SCHEDULE] objectForKey:KEY_VALUE];
    // Basic schedule state can be true (enabled) or false (disabled).
    // We need to notify UI for schedule state change with basic schedule
    // state in data.
    if (basicScheduleState) {
        NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
        [data setObject:basicScheduleState forKey:KEY_SCHEDULE_STATE];
        [data setObject:[NSString stringWithFormat:@"%d", NEATO_SCHEDULE_BASIC_INT] forKey:KEY_SCHEDULE_TYPE];
        [self notifyDataChangeForRobotId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE] withKeyCode:[NSNumber numberWithInt:ROBOT_SCHEDULE_STATE_CHANGED] andData:data];
    }
}

- (void)notifyScheduleUpdatedForProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSString *hasScheduleUpdated = [[robotProfile objectForKey:KEY_ROBOT_SCHEDULE_UPDATED] objectForKey:KEY_VALUE];
    // Server returns 'true' if schedule is updated so we
    // have to notify UI.
    if ([AppHelper boolValueFromString:hasScheduleUpdated]) {
        NSDictionary *data = [[NSDictionary alloc] init];
        [self notifyDataChangeForRobotId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE] withKeyCode:[NSNumber numberWithInt:ROBOT_HAS_SCHEDULE_UPDATED] andData:data];
    }
}

- (void)notifyDataChangeForRobotId:(NSString *)robotId withKeyCode:(NSNumber *)key andData:(NSDictionary *)data {
    debugLog(@"");
    @synchronized (self) {
        NSMutableDictionary *dataChanged = [[NSMutableDictionary alloc] init];
        [dataChanged setObject:robotId forKey:KEY_ROBOT_ID];
        [dataChanged setObject:key forKey:KEY_ROBOT_DATA_ID];
        [dataChanged setObject:data forKey:KEY_ROBOT_DATA];
        NSString *callBackId = [NeatoRobotHelper xmppCallbackId];
        // Post UI update notification.
        NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
        if (callBackId) {
            [userInfo setObject:dataChanged forKey:KEY_UI_UPDATE_DATA];
            [userInfo setObject:callBackId forKey:KEY_CALLBACK_ID];
            [userInfo setObject:[NSNumber numberWithBool:YES] forKey:SUCCESS_CALLBACK];
        }
        else {
            // TODO: Do we need to send back any error to UI??
            debugLog(@"ERROR!!CallBackId is nil.");
            return;
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_UPDATE_UI_FOR_XMPP_DATA_CHANGE object:nil userInfo:userInfo];
    }
}

// Checks if the notification received is due to local changes.
// If the causing agent Id sent by server matches local causing agent Id, this is local change
- (BOOL)isNotificationLocal:(NSDictionary *)notificationData {
    if ([[notificationData objectForKey:KEY_CAUSE_AGENT_ID] isEqual:[NeatoUserHelper uniqueDeviceIdForUser]]) {
        return YES;
    }
    return NO;
}

- (void)stopListeningRobotDataChangeNotificationsFor:(id)notificationObserver {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    if (notificationObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:notificationObserver];
    }
    self.robotStateChangeObserver = nil;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    if (self.robotStateChangeObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:self.robotStateChangeObserver];
    }
    self.robotStateChangeObserver = nil;
}

- (void)processRobotAvailableToDriveForProfile:(NSDictionary *)robotProfile {
    // Assuming that availability reposnse will be a JSON string.
    NSDictionary *availabilityResponse = [AppHelper parseJSON:[[[robotProfile objectForKey:KEY_AVAILABLE_TO_DRIVE] objectForKey:KEY_VALUE] dataUsingEncoding:NSUTF8StringEncoding]];
    if (availabilityResponse) {
        NSNumber *driveAvailableStatus = [availabilityResponse objectForKey:KEY_DRIVE_AVAILABLE_STATUS];
        if ([driveAvailableStatus boolValue]) {
            // Robot is ready to drive.
            [[[RobotDriveManager alloc] init] robotWithRobotId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE] isReadyToDriveWithIP:[availabilityResponse objectForKey:KEY_ROBOT_IP_ADDRESS]];
        }
        else {
            // Robot not available to drive.
            [[[RobotDriveManager alloc] init] robotWithRobotId:[[robotProfile objectForKey:KEY_SERIAL_NUMBER] objectForKey:KEY_VALUE] isNotAvailableToDriveWithErrorCode:[[availabilityResponse objectForKey:KEY_ERROR_DRIVE_REASON_CODE] integerValue]];
        }
    }
    else {
        // Log
    }
}

- (void)processRobotIntendToDriveForProfile:(NSDictionary *)robotProfile {
    // If value of intend to drive is an empty string or it doesn't not exist in
    // robotProfile then somebody has deleted intend to drive on server otherwise
    // somebody has initiated intend to drive.
    NSString *intenToDriveProfileDetail = [[robotProfile objectForKey:KEY_INTEND_TO_DRIVE] objectForKey:KEY_VALUE];
    if (!intenToDriveProfileDetail || (intenToDriveProfileDetail.length == 0)) {
        // Somebody deleted intend to drive on server.
        debugLog(@"Somebody deleted intend to drive on server.");
    }
    else {
        // Somebody initiated intend to drive on server.
        debugLog(@"Somebody initiated intend to drive on server.");
    }
}

@end
