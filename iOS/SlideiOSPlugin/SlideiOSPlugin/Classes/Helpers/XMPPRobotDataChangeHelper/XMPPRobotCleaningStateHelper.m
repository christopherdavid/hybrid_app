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
        return ROBOT_STATE_IDLE;
    }
    else if (commandId == COMMAND_PAUSE_CLEANING) {
        return ROBOT_STATE_PAUSED;
    }
    else if (commandId == COMMAND_RESUME_CLEANING) {
        return ROBOT_STATE_CLEANING;
    }
    else if (commandId == COMMAND_SEND_TO_BASE) {
        return ROBOT_STATE_RETURNING;
    }
    return ROBOT_STATE_INVALID;
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

// For now Current state is returned as the actual state.
// (As virtual state concept is removed).
+ (NSInteger)robotActualStateFromRobotProfile:(NSDictionary *)robotProfile {
    debugLog(@"");
    NSInteger actualState = ROBOT_STATE_INVALID;
    NSInteger currentState = [self robotCurrentStateFromRobotProfile:robotProfile];
    if (currentState != ROBOT_STATE_INVALID) {
        actualState = currentState;
    }
    debugLog(@"Actual state of robot is %d", actualState);
    return actualState;
}

@end
