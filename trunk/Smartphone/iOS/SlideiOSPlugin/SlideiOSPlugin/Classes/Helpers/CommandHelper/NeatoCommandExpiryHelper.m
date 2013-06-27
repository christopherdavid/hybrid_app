#import "NeatoCommandExpiryHelper.h"
#import "LogHelper.h"
#import "NeatoServerManager.h"
#import "NeatoUserHelper.h"

static NeatoCommandExpiryHelper *sharedInstance = nil;

@interface NeatoCommandExpiryHelper()
@property (nonatomic, strong) NSMutableDictionary *runningCommandExpiryTimers;
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

- (void)startCommandTimerForRobotId:(NSString *)robotId {
    debugLog(@"");
    @synchronized (self) {
        if(![self isTimerRunningForRobotId:robotId]) {
            NSTimer *commandExpiryTimer = [NSTimer scheduledTimerWithTimeInterval:COMMAND_EXPIRY_TIME target:self selector:@selector(commandTimerExpiredForRobot:) userInfo:robotId repeats:NO];
            [self.runningCommandExpiryTimers setValue:commandExpiryTimer forKey:robotId];
        }
        else {
            debugLog(@"Command timer already running for robot with id = %@", robotId);
        }
    }
}

- (void)stopCommandTimerForRobotId:(NSString *)robotId {
    debugLog(@"");
    @synchronized (self) {
        if ([self isTimerRunningForRobotId:robotId]) {
            NSTimer *commandExpiryTimer = [self.runningCommandExpiryTimers valueForKey:robotId];
            [commandExpiryTimer invalidate];
            [self.runningCommandExpiryTimers removeObjectForKey:robotId];
        }
        else {
            debugLog(@"No running timer for robot with Id = %@", robotId);
        }
    }
}

- (BOOL)isTimerRunningForRobotId:(NSString *)robotId {
    debugLog(@"");
    NSTimer *commandExpiryTimer = [self.runningCommandExpiryTimers valueForKey:robotId];
    if (commandExpiryTimer) {
        return [commandExpiryTimer isValid];
    }
    else {
        return NO;
    }
}

- (void)commandTimerExpiredForRobot:(NSTimer *)commandTimer {
    @synchronized (self) {
        [self.runningCommandExpiryTimers removeObjectForKey:commandTimer.userInfo];
        [self clearCleaningCommandForRobotWithId:commandTimer.userInfo];
    }
}

// Resets state for the robot at the server
- (void)clearCleaningCommandForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
    robotCommand.xmlCommand = @"";
    robotCommand.commandId = @"";
    robotCommand.robotId = robotId;
    robotCommand.causingAgentId = [NeatoUserHelper uniqueDeviceIdForUser];
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager sendCommand:robotCommand];
}


- (void)failedtoSendCommandWithError:(NSError *)error {
    debugLog(@"Failed to clear command with error:%@", error);
}

// TODO: Retry atleast once if failed?
// Notify user?
- (void)command:(NeatoRobotCommand *)command sentWithResult:(NSDictionary *)result {
    debugLog(@"Command cleared with result:%@", result);
}

@end
