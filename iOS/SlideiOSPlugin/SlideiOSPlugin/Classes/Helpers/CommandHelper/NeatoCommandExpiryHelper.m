#import "NeatoCommandExpiryHelper.h"
#import "LogHelper.h"
#import "NeatoServerManager.h"
#import "NeatoUserHelper.h"
#import "XMPPRobotDataChangeManager.h"
#import "AppHelper.h"

static NeatoCommandExpiryHelper *sharedInstance = nil;

@interface NeatoCommandExpiryHelper()
@property (nonatomic, strong) NSMutableDictionary *runningCommandExpiryTimers;
@property (nonatomic, strong) NSMutableDictionary *expirableCommandIds;
@end
@implementation NeatoCommandExpiryHelper

@synthesize runningCommandExpiryTimers = _runningCommandExpiryTimers;

+ (NeatoCommandExpiryHelper *)expirableCommandHelper {
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[NeatoCommandExpiryHelper alloc] init];
    });
    return sharedInstance;
}

- (NSMutableDictionary *)runningCommandExpiryTimers {
    if (!_runningCommandExpiryTimers) {
        _runningCommandExpiryTimers = [[NSMutableDictionary alloc] init];
    }
    return _runningCommandExpiryTimers;
}

- (NSMutableDictionary *)expirableCommandIds {
    if (!_expirableCommandIds) {
        _expirableCommandIds = [[NSMutableDictionary alloc] init];
    }
    return _expirableCommandIds;
}

- (void)startCommandTimerForRobotId:(NSString *)robotId withCommandId:(NSString *)commandId {
    debugLog(@"");
    @synchronized (self) {
        if(![self isTimerRunningForRobotId:[robotId lowercaseString]]) {
            NSTimer *commandExpiryTimer = [NSTimer scheduledTimerWithTimeInterval:COMMAND_EXPIRY_TIME target:self selector:@selector(commandTimerExpiredForRobot:) userInfo:[robotId lowercaseString] repeats:NO];
            [self.runningCommandExpiryTimers setValue:commandExpiryTimer forKey:[robotId lowercaseString]];
            if (commandId) {
                [self.expirableCommandIds setObject:commandId forKey:[robotId lowercaseString]];
            }
        }
        else {
            debugLog(@"Command timer already running for robot with id = %@", robotId);
        }
    }
}

- (void)stopCommandTimerForRobotId:(NSString *)robotId {
    debugLog(@"");
    @synchronized (self) {
        if ([self isTimerRunningForRobotId:[robotId lowercaseString]]) {
            NSTimer *commandExpiryTimer = [self.runningCommandExpiryTimers valueForKey:[robotId lowercaseString]];
            [commandExpiryTimer invalidate];
            [self.runningCommandExpiryTimers removeObjectForKey:[robotId lowercaseString]];
        }
        else {
            debugLog(@"No running timer for robot with Id = %@", robotId);
        }
    }
}

- (BOOL)isTimerRunningForRobotId:(NSString *)robotId {
    debugLog(@"");
    NSTimer *commandExpiryTimer = [self.runningCommandExpiryTimers valueForKey:[robotId lowercaseString]];
    if (commandExpiryTimer) {
        return [commandExpiryTimer isValid];
    }
    else {
        return NO;
    }
}

- (void)commandTimerExpiredForRobot:(NSTimer *)commandTimer {
    debugLog(@"");
    @synchronized (self) {
        [self.runningCommandExpiryTimers removeObjectForKey:commandTimer.userInfo];
        [self resetStateForRobotWithId:commandTimer.userInfo];
        [self notifyCommandFailureWithRobotId:commandTimer.userInfo];
    }
}

// Resets state for the robot at the server.
// Clears cleaning and intend to drive commands at server.
- (void)resetStateForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    
    // As command might me sent via XMPP,
    // So no need to reset 'empty command' over server.
    // We need to set empty command on failure, only when we send command by setting 'robot profile details' on server.
    NSString *commandId = [self.expirableCommandIds objectForKey:[robotId lowercaseString]];
    BOOL isCommandSentOverServer = ![AppHelper shouldSendCommandDirectlyViaXMPP:commandId];
    if (isCommandSentOverServer) {
        NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
        robotCommand.xmlCommand = @"";
        robotCommand.commandId = @"";
        robotCommand.robotId = robotId;
        robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
        [robotCommand.profileDict setValue:robotCommand.xmlCommand forKey:KEY_ROBOT_CLEANING_COMMAND];
        NeatoServerManager *manager = [[NeatoServerManager alloc] init];
        manager.delegate = self;
        [manager sendCommand:robotCommand];
    }
}


- (void)failedtoSendCommandWithError:(NSError *)error {
    debugLog(@"Failed to clear command with error:%@", error);
}

// TODO: Retry atleast once if failed?
// Notify user?
- (void)command:(NeatoRobotCommand *)command sentWithResult:(NSDictionary *)result {
    debugLog(@"Command cleared with result:%@", result);
}

- (void)notifyCommandFailureWithRobotId:(NSString *)robotId {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    NSString *failedCommandId = [self.expirableCommandIds objectForKey:[robotId lowercaseString]];
    failedCommandId ? [data setObject:failedCommandId forKey:KEY_FAILED_COMMAND_ID] : nil;
    [[XMPPRobotDataChangeManager sharedXmppDataChangeManager] notifyDataChangeForRobotId:robotId
                                                                             withKeyCode:[NSNumber numberWithInteger:ROBOT_COMMAND_FAILED]
                                                                                 andData:data];
}

@end
