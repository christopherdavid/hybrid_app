#import "RobotDissociationListener.h"
#import "NeatoServerHelper.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"

@interface RobotDissociationListener()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) RobotDissociationListener *retained_self;

@end
@implementation RobotDissociationListener

@synthesize userEmail = _userEmail;
@synthesize robotId = _robotId;

- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init]))
    {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)start {
    debugLog(@"");
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper dissociateRobotWithId:self.robotId fromUserWithEmail:self.userEmail];
}
- (void)robotDissociatedWithMessage:(NSString *)message {
    debugLog(@"");
    //Updating the DB before notifying the caller.
    [NeatoUserHelper deleteRobotWithRobotId:self.robotId forUser:[NeatoUserHelper getNeatoUser].userId];
    if ([self.delegate respondsToSelector:@selector(robotDissociatedWithMessage:)]) {
            [self.delegate performSelector:@selector(robotDissociatedWithMessage:) withObject:message];
    }
        self.delegate = nil;
        self.retained_self = nil;
}

- (void)failedToDissociateRobotWithError:(NSError *)error {
    debugLog(@"");
        if ([self.delegate respondsToSelector:@selector(failedToDissociateRobotWithError:)]) {
            [self.delegate performSelector:@selector(failedToDissociateRobotWithError:) withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;

}

@end
