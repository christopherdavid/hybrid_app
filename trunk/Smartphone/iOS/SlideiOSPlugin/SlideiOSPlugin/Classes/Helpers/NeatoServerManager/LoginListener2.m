#import "LoginListener2.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "XMPPConnectionHelper.h"
#import "XMPPConnection.h"

@interface LoginListener2()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) LoginListener2 *retained_self;
@end

@implementation LoginListener2

@synthesize email = _email;
@synthesize password = _password;


- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)start {
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper loginNativeUser:self.email password:self.password];
}

- (void)gotUserHandleForLogin:(NSString *)userHandle {
    debugLog(@"");
    self.retained_self = self;
    // save auth token to local storage
    [NeatoUserHelper saveUserAuthToken:userHandle];
    
    // Get user details from server
    NeatoServerHelper *helper = [[NeatoServerHelper alloc]init];
    helper.delegate = self;
    [helper getUserAccountDetails:userHandle email:nil];
}

- (void)failedToGetLoginHandle:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)gotUserDetails:(NeatoUser *)user {
    debugLog(@"");
    [NeatoUserHelper saveNeatoUser:user];
    
    XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
	// If XMPP is already connected then we need to tell
	// the upper layer that login succeeded
	// Because of some issue on the CleaningRobotApp UI, PhoneGap UI assumes that
	// user is not logged in. XMPP Login succeed but CleaningRobotApp shows login UI
    if (![helper isConnected]) {
      helper.delegate = self;
      [helper connectJID:user.chatId password:user.chatPassword host:XMPP_SERVER_ADDRESS];
    }
    else {
      [self alreadyConnectedToXMPP];
    }
}

- (void)alreadyConnectedToXMPP {
  debugLog(@"XMPP connection already exists");
  NeatoUser *user = [NeatoUserHelper getNeatoUser];
  [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
  self.delegate = nil;
  self.retained_self = nil;
}

- (void)failedToGetUserDetailsWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)didConnectOverXMPP {
    debugLog(@"didConnectOverXMPP called");
    NeatoUser *user = [NeatoUserHelper getNeatoUser];
    [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)xmppLoginfailedWithError:(NSXMLElement *)error {
	debugLog(@"xmpp Login failed");
    [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
