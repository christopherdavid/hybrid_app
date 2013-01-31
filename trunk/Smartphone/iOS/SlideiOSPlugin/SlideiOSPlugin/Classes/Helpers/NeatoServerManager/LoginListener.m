#import "LoginListener.h"
#import "LogHelper.h"
#import "NeatoUser.h"

@interface LoginListener()
{

}
@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) LoginListener *retained_self;
@end

@implementation LoginListener

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
    [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void) gotUserDetails:(NeatoUser *)neatoUser
{
    debugLog(@"");
    [self.delegate performSelector:@selector(loginSuccess:) withObject:neatoUser];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
