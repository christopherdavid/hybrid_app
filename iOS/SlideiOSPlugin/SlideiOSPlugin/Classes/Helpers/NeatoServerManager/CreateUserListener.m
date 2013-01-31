#import "CreateUserListener.h"
#import "LogHelper.h"
#import "NeatoUser.h"

@interface CreateUserListener()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) CreateUserListener *retained_self;
@end

@implementation CreateUserListener
@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;

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

-(void) failedToGetUserDetailsWithError:(NSError *) error
{
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToCreateUserWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void) gotUserDetails:(NeatoUser *)neatoUser
{
    debugLog(@"");
    [self.delegate performSelector:@selector(userCreated:) withObject:neatoUser];
    self.delegate = nil;
    self.retained_self = nil;
}
@end
