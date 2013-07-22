#import "SetRobotNameListener.h"
#import "LogHelper.h"
#import "NeatoServerHelper.h"
#import "NeatoRobotHelper.h"
#import "NeatoUserHelper.h"

@interface SetRobotNameListener()

@property(nonatomic, retain) SetRobotNameListener *retained_self;
@property(nonatomic, weak)id delegate;
@end

@implementation SetRobotNameListener
@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;
@synthesize robotName = _robotName;
@synthesize robotId = _robotId;

-(id) initWithDelegate:(id) delegate
{
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
    [helper setRobotName2:self.robotName forRobotWithId:self.robotId forUserWithEmail:[NeatoUserHelper getLoggedInUserEmail]];
}

- (void)robotNameUpdated {
    debugLog(@"");
    [NeatoRobotHelper updateName:self.robotName forRobotwithId:self.robotId];
    
    [self.delegate performSelector:@selector(robotName:updatedForRobotWithId:) withObject:self.robotName withObject:self.robotId];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)failedToUpdateRobotNameWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToUpdateRobotNameWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}


@end
