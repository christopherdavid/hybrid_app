#import "RobotAssociationListener2.h"
#import "LogHelper.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"

@interface RobotAssociationListener2()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) RobotAssociationListener2 *retained_self;

@end

@implementation RobotAssociationListener2
@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;
@synthesize robotId;
@synthesize userEmail;

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
    [helper setRobotUserEmail:self.userEmail serialNumber:self.robotId];
}

-(void)robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId{
    debugLog(@"");
    // Fetch all robots for user and update the DB
    // Then notify the caller
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper associatedRobotsForUserWithEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken]];
}

- (void)failedToGetAssociatedRobotsWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(robotAssociation2FailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void) gotUserAssociatedRobots:(NSMutableArray *)robots
{
    debugLog(@"");
    // Update the DB with latest robot info
    for (NeatoRobot *robot in robots) {
        [NeatoRobotHelper saveNeatoRobot:robot];
    }
    NeatoRobot *associatedRobot = [NeatoRobotHelper getRobotForId:self.robotId];
    [self.delegate performSelector:@selector(userAssociateWithRobot:) withObject:associatedRobot];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void)robotAssociationFailedWithError:(NSError *)error{
    debugLog(@"");
    [self.delegate performSelector:@selector(robotAssociation2FailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
