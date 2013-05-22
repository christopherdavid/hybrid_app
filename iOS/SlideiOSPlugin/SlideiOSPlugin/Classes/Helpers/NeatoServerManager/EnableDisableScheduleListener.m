#import "EnableDisableScheduleListener.h"
#import "LogHelper.h"
#import "NeatoServerHelper.h"
#import "PluginConstants.h"

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
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper enableDisable:self.enable scheduleType:self.scheduleType forRobot:self.robotId withUserEmail:self.email];
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
