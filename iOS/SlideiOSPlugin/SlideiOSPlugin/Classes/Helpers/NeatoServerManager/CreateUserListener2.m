#import "CreateUserListener2.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"

@interface CreateUserListener2()

@property(nonatomic, weak) id delegate;
@property(nonatomic, strong) CreateUserListener2 *retained_self;
@end

@implementation CreateUserListener2

@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;

- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)gotUserDetails:(NeatoUser *)neatoUser {
    debugLog(@"");
    // Save the details to local storage
    [NeatoUserHelper saveNeatoUser:neatoUser];
    [self.delegate performSelector:@selector(userCreated2:) withObject:neatoUser];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)start {
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createUser2:self.user];
}


- (void)gotHandleForCreateUser2:(NSString *)authToken {
    debugLog(@"");
    // Save users auth token to local storage
    [NeatoUserHelper saveUserAuthToken:authToken];
    
    // Get the user details now
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper getUserAccountDetails:authToken email:nil];
}

- (void)failedToGetCreateUserHandle2Error:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToCreateUser2WithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)failedToGetUserDetailsWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToCreateUser2WithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
