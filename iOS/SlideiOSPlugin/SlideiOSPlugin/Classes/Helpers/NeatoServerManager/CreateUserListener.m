#import "CreateUserListener.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "XMPPConnectionHelper.h"
#import "NeatoUserHelper.h"

@interface CreateUserListener()

@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) CreateUserListener *retained_self;
@end

@implementation CreateUserListener
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

- (void)failedToGetUserDetailsWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToCreateUserWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)gotUserDetails:(NeatoUser *)neatoUser {
    debugLog(@"");
    // XMPP login.
    XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
    helper.delegate = self;
    [helper connectJID:neatoUser.chatId password:neatoUser.chatPassword host:NEATO_XMPP_SERVER_ADDRESS];
}

- (void)didConnectOverXMPP {
    debugLog(@"Did connect over XMPP.");
    NeatoUser *user = [NeatoUserHelper getNeatoUser];
    [self.delegate performSelector:@selector(userCreated:) withObject:user];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)xmppLoginfailedWithError:(NSXMLElement *)error {
	debugLog(@"XMPP login failed");
    [self.delegate performSelector:@selector(failedToCreateUserWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
