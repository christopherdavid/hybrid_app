#import "CreateUserListener2.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoUserAttributes.h"
#import "AppHelper.h"
#import "XMPPConnectionHelper.h"
#import "AppSettings.h"
#import "NeatoErrorCodes.h"

#define CREATE_USER3_POST_STRING @"api_key=%@&name=%@&email=%@&alternate_email=%@&password=%@&account_type=%@&extra_param=%@"
#define GET_USER_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@"

@interface CreateUserListener2()

@property(nonatomic, weak) id delegate;
@property(nonatomic, strong) CreateUserListener2 *retained_self;
@property (nonatomic, strong) RequestCompletionBlockDictionary completion;
@property (nonatomic, strong) NSDictionary *response;

@end

@implementation CreateUserListener2

@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;

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
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createUser3:self.user];
}

- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion {
    self.completion = completion;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_CREATE_USER3_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:CREATE_USER3_POST_STRING, API_KEY, self.user.name, self.user.email, self.user.alternateEmail, self.user.password, self.user.account_type, [self.user extraParam]] dataUsingEncoding:NSUTF8StringEncoding]];
    
    __weak typeof(self) weakSelf = self;
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    [serverHelper dataForRequest:request
                 completionBlock:^(id response, NSError *error) {
                     if (error) {
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     NSString *authToken = [response valueForKey:USER_HANDLE];
                     // NOTE: If auth token value is NULL then validation status would
                     // be -2 and we have to send back error message in extra params
                     // to caller.
                     if ([AppHelper isStringNilOrEmpty:authToken]) {
                         NSError *error = [AppHelper nserrorWithDescription:[[response valueForKey:NEATO_RESPONSE_EXTRA_PARAMS] valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_USER_UNAUTHORIZED];
                         completion ? completion(nil, error) : nil;
                         return;
                     }
                     // Save users auth token to local storage
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
                                      
                                      // XMPP login.
                                      XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
                                      helper.delegate = weakSelf;
                                      [helper connectJID:neatoUser.chatId password:neatoUser.chatPassword host:XMPP_SERVER_ADDRESS];
                                  }];
                 }];
}

#pragma mark - NeatoServerHelperProtocol
- (void)gotUserDetails:(NeatoUser *)neatoUser {
    debugLog(@"");
    // Save the details to local storage
    [NeatoUserHelper saveNeatoUser:neatoUser];
    
    // XMPP login.
    XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
    helper.delegate = self;
    [helper connectJID:neatoUser.chatId password:neatoUser.chatPassword host:XMPP_SERVER_ADDRESS];
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
                   
- (void)failedToSetUserAttributesWithError:(NSError *)error {
    debugLog(@"Failed to set user attribute.");
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)setUserAttributesSucceeded {
    debugLog(@"Set user attributes succeeded.");
    self.delegate = nil;
    self.retained_self = nil;
}

#pragma mark - XMPPConnectionHelperProtocol
- (void)didConnectOverXMPP {
    debugLog(@"Did connect over XMPP.");
    self.completion ? self.completion(self.response, nil) : nil;
    NeatoUser *user = [NeatoUserHelper getNeatoUser];
    [self.delegate performSelector:@selector(userCreated2:) withObject:user];
    
    // Set user attributes.
    NSString *authToken = [NeatoUserHelper getUsersAuthToken];
    NeatoUserAttributes *attributes = [[NeatoUserAttributes alloc] init];
    attributes.systemName = [AppHelper deviceSystemName];
    attributes.systemVersion = [AppHelper deviceSystemVersion];
    attributes.deviceModelName = [AppHelper deviceModelName];
    
    // Setting user attributes
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper setUserAttributes:attributes forAuthToken:authToken];
}

- (void)xmppLoginfailedWithError:(NSError *)error {
	debugLog(@"XMPP login failed.");
    self.completion ? self.completion(nil, error) : nil;
    [self.delegate performSelector:@selector(failedToCreateUser2WithError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
