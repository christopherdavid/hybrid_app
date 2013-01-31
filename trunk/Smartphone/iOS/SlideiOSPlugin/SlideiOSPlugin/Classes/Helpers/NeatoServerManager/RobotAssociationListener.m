#import "RobotAssociationListener.h"
#import "LogHelper.h"
#import "NeatoRobot.h"
#import "NeatoRobotHelper.h"

@interface RobotAssociationListener()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) RobotAssociationListener *retained_self;
@end

@implementation RobotAssociationListener
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize associatedRobotId = _associatedRobotId;

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

-(void) failedToGetAssociatedRobotsWithError:(NSError *) error
{
    debugLog(@"");
    [self.delegate performSelector:@selector(robotAssociationFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void) gotUserAssociatedRobots:(NSMutableArray *) robots
{
    debugLog(@"");
    // Update the DB with latest robot info
    for (NeatoRobot *robot in robots) {
        [NeatoRobotHelper saveNeatoRobot:robot];
    }
    
    [self.delegate performSelector:@selector(robotAssociationCompletedSuccessfully:) withObject:self.associatedRobotId];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
