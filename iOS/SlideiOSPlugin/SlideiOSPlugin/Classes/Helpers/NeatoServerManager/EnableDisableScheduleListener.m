#import "EnableDisableScheduleListener.h"
#import "LogHelper.h"
#import "NeatoServerHelper.h"
#import "PluginConstants.h"
#import "NeatoUserHelper.h"
#import "AppHelper.h"

@interface EnableDisableScheduleListener()

@property(nonatomic, retain) EnableDisableScheduleListener *retained_self;
@property(nonatomic, weak)id delegate;
@end

@implementation EnableDisableScheduleListener

@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;
@synthesize email = _email;
@synthesize robotId = _robotId;
@synthesize scheduleType = _scheduleType;
@synthesize enable = _enable;

-(id) initWithDelegate:(id) delegate {
    debugLog(@"");
    if ((self = [super init]))
    {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

-(void)start {
   // Return error if advance schedule type.
    if (self.scheduleType == NEATO_SCHEDULE_ADVANCE_INT) {
        NSError *error = [AppHelper nserrorWithDescription:@"Invalid schedule type." code:INVALID_SCHEDULE_TYPE];
        [self.delegate performSelector:@selector(failedToEnableDisableScheduleWithError:) withObject:error];
        self.delegate = nil;
        self.retained_self = nil;
        return;
    }
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    // TODO: Server helper should get the causing agent Id from NeatoUserHelper.
    [helper enableDisable:self.enable scheduleType:self.scheduleType forRobot:self.robotId withUserEmail:self.email withCauseAgentId:[NeatoUserHelper uniqueDeviceIdForUser]];
}


- (void)failedToEnableDisableScheduleWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToEnableDisableScheduleWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)enabledDisabledScheduleSuccess {
    debugLog(@"");
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:self.robotId forKey:KEY_ROBOT_ID];
    [data setValue:[NSNumber numberWithInteger:self.scheduleType] forKey:KEY_SCHEDULE_TYPE];
    [data setValue:[NSNumber numberWithBool:self.enable] forKey:KEY_SCHEDULE_IS_ENABLED];
    [self.delegate performSelector:@selector(enabledDisabledScheduleWithResult:) withObject:data];
    self.delegate = nil;
    self.retained_self = nil;
}
@end
