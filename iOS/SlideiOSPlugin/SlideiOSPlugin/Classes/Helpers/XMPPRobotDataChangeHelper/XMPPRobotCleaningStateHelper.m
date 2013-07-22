#import "XMPPRobotCleaningStateHelper.h"
#import "NeatoConstants.h"
#import "LogHelper.h"
#import "NeatoConstants.h"
#import "CommandsHelper.h"

@interface XMPPRobotCleaningStateHelper()
// Returns the state of robot based on the command Id. (server command to robot state)
+ (NSInteger)robotStateFromCommandId:(NSInteger)commandId;
+ (NSInteger)robotActualStateFromRobotProfile:(NSDictionary *)robotProfile;
@end
@implementation XMPPRobotCleaningStateHelper

+ (NSInteger)robotStateFromCommandId:(NSInteger)commandId {
    if (commandId == COMMAND_START_ROBOT) {
        return ROBOT_STATE_CLEANING;
    }
    else if (commandId == COMMAND_STOP_ROBOT) {
        return ROBOT_STATE_STOPPED;
    }
    else if (commandId == COMMAND_PAUSE_CLEANING) {
        return ROBOT_STATE_PAUSED;
    }
    else if (commandId == COMMAND_RESUME_CLEANING) {
        return ROBOT_STATE_RESUMED;
    }
    else if (commandId == COMMAND_SEND_TO_BASE) {
        return ROBOT_STATE_ON_BASE;
    }
    return ROBOT_STATE_INVALID;
}

// If any queued command on server is returned in robot profile details, its
// commandId determines virtual state of robot.
+ (NSInteger)robotVirtualStateFromRobotProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSInteger virtualState = ROBOT_STATE_INVALID;
    if ([robotProfile objectForKey:KEY_ROBOT_CLEANING_COMMAND]) {
        NSString *cleaningCommand = [[robotProfile objectForKey:KEY_ROBOT_CLEANING_COMMAND] objectForKey:@"value"];
        CommandsHelper *helper = [[CommandsHelper alloc] init];
        NSString *commandId = [helper commandIdFromXmlCommand:cleaningCommand];
        virtualState = [self robotStateFromCommandId:[commandId integerValue]];
        debugLog(@"Cleaning command id is %@ and virtual state is %d", commandId, virtualState);
    }
    return virtualState;
}

// Returns the current state from profile details.
+ (NSInteger)robotCurrentStateFromRobotProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSInteger currentState = ROBOT_STATE_INVALID;
    if ([robotProfile objectForKey:KEY_ROBOT_CURRENT_STATE]) {
        debugLog(@"Robot current state is %@", [[robotProfile objectForKey:KEY_ROBOT_CURRENT_STATE] objectForKey:@"value"]);
        currentState = [[[robotProfile objectForKey:KEY_ROBOT_CURRENT_STATE] objectForKey:@"value"] integerValue];
    }
    return currentState;
}

// Virtual state has preference over current state. So if the profile from server
// contains a valid virtual state that will be considered as actual state.
// Otherwise current state is returned as the actual state.
+ (NSInteger)robotActualStateFromRobotProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSInteger actualState = ROBOT_STATE_INVALID;
    NSInteger virtualState = [self robotVirtualStateFromRobotProfile:robotProfile];
    NSInteger currentState = [self robotCurrentStateFromRobotProfile:robotProfile];
    if (virtualState != ROBOT_STATE_INVALID) {
        actualState = virtualState;
    }
    else if (currentState != ROBOT_STATE_INVALID) {
        actualState = currentState;
    }
    debugLog(@"Actual state of robot is %d", actualState);
    return actualState;
}

@end
