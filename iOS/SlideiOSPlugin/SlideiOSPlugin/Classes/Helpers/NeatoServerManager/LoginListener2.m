#import "LoginListener2.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "XMPPConnectionHelper.h"
#import "XMPPConnection.h"
#import "AppSettings.h"
#import "AppHelper.h"
#import "NeatoErrorCodes.h"

#define GET_AUTH_TOKEN_NATIVE_POST_STRING @"api_key=%@&account_type=%@&email=%@&password=%@"
#define GET_USER_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@"

@interface LoginListener2()

@property (nonatomic, weak) id delegate;
@property (nonatomic, retain) LoginListener2 *retained_self;
@property (nonatomic, strong) RequestCompletionBlockDictionary completion;
@property (nonatomic, strong) NSDictionary *response;

@end

@implementation LoginListener2

@synthesize email = _email;
@synthesize password = _password;

#pragma mark - Init API
- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

#pragma mark - Public
- (void)start {
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper loginNativeUser:self.email password:self.password];
}

- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion {
    self.completion = completion;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_USER_AUTH_TOKEN_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_AUTH_TOKEN_NATIVE_POST_STRING, API_KEY,ACCOUNT_TYPE_NATIVE, self.email, self.password] dataUsingEncoding:NSUTF8StringEncoding]];
    
    __weak typeof(self) weakSelf = self;
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     NSString *authToken = response;
                     // NOTE: If auth token value is NULL then validation status would
                     // be -2 and we have to send back error message in extra params
                     // to caller.
                     if ([AppHelper isStringNilOrEmpty:authToken]) {
                         NSError *error = [AppHelper nserrorWithDescription:[[response valueForKey:NEATO_RESPONSE_EXTRA_PARAMS] valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_USER_UNAUTHORIZED];
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     
                     [NeatoUserHelper saveUserAuthToken:authToken];
                     
                     NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_USER_DETAILS_URL]];
                     [request setHTTPMethod:@"POST"];
                     NSString *email =  @"";
                     [request setHTTPBody:[[NSString stringWithFormat:GET_USER_DETAILS_POST_STRING, API_KEY, email, authToken] dataUsingEncoding:NSUTF8StringEncoding]];
                     
                     NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
                     [serverHelper dataForRequest:request
                                  completionBlock:^(id response, NSError *error) {
                                      if (error) {
                                          completion ? completion(response, error) : nil;
                                          return;
                                      };
                                      weakSelf.response = response;
                                      // Get user from dictionary
                                      NeatoUser *neatoUser = [[NeatoUser alloc] initWithDictionary:response];
                                      [NeatoUserHelper saveNeatoUser:neatoUser];
                                      
                                      XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
                                      // If XMPP is already connected then we need to tell
                                      // the upper layer that login succeeded
                                      // Because of some issue on the CleaningRobotApp UI, PhoneGap UI assumes that
                                      // user is not logged in. XMPP Login succeed but CleaningRobotApp shows login UI
                                      if (![helper isConnected]) {
                                          helper.delegate = weakSelf;
                                          [helper connectJID:neatoUser.chatId password:neatoUser.chatPassword host:XMPP_SERVER_ADDRESS];
                                      }
                                      else {
                                          [weakSelf alreadyConnectedToXMPP];
                                      }
                                  }];
                 }];
}

#pragma mark - NeatoServerHelperProtocol
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

- (void)failedToGetUserDetailsWithError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

#pragma mark - XMPPConnectionHelperProtocol
- (void)didConnectOverXMPP {
    debugLog(@"didConnectOverXMPP called");
    self.completion ? self.completion(self.response, nil) : nil;
    NeatoUser *user = [NeatoUserHelper getNeatoUser];
    [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)xmppLoginfailedWithError:(NSError *)error {
	debugLog(@"xmpp Login failed");
    self.completion ? self.completion(nil, error) : nil;
    [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)didDisConnectFromXMPP {
  NSError *error = [AppHelper nserrorWithDescription:@"Failed to connect over XMPP" code:UI_ERROR_TYPE_UNKNOWN];
  self.completion ? self.completion(nil, error) : nil;
  [self.delegate performSelector:@selector(loginFailedWithError:) withObject:error];
  self.delegate = nil;
  self.retained_self = nil;
}

#pragma mark - Private
- (void)alreadyConnectedToXMPP {
    debugLog(@"XMPP connection already exists");
    self.completion ? self.completion(self.response, nil) : nil;
    NeatoUser *user = [NeatoUserHelper getNeatoUser];
    [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}
@end
