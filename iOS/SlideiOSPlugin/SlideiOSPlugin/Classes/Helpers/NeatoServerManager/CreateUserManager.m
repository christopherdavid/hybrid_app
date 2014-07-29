#import "CreateUserManager.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoUserAttributes.h"
#import "AppHelper.h"
#import "XMPPConnectionHelper.h"
#import "AppSettings.h"
#import "NeatoErrorCodes.h"

@interface CreateUserManager()
@property(nonatomic, strong) CreateUserManager *retained_self;
@property (nonatomic, strong) RequestCompletionBlockDictionary completion;
@property (nonatomic, strong) NSDictionary *response;
@end

@implementation CreateUserManager

#pragma mark - Init API
- (id)init {
    debugLog(@"");
    if ((self = [super init])) {
        self.retained_self = self;
    }
    return self;
}

#pragma mark - Public
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
                   
                     NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                     NSString *authToken = [responseResultDict valueForKey:USER_HANDLE];
                     // NOTE: If auth token value is NULL then validation status would
                     // be -2 and we have to send back error message in extra params
                     // to caller.
                     if ([AppHelper isStringNilOrEmpty:authToken]) {
                         NSError *error = [AppHelper nserrorWithDescription:[[responseResultDict valueForKey:NEATO_RESPONSE_EXTRA_PARAMS] valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_USER_UNAUTHORIZED];
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
                                          completion ? completion(nil, error) : nil;
                                          return;
                                      };
                                    
                                      NSDictionary *responseResultDict = [response valueForKey:NEATO_RESPONSE_RESULT];
                                      weakSelf.response = responseResultDict;
                                      // Get user from dictionary
                                      NeatoUser *neatoUser = [[NeatoUser alloc] initWithDictionary:responseResultDict];
                                      [NeatoUserHelper saveNeatoUser:neatoUser];
                                      
                                      // XMPP login.
                                      XMPPConnectionHelper *helper = [[XMPPConnectionHelper alloc] init];
                                      helper.delegate = weakSelf;
                                      [helper connectJID:neatoUser.chatId password:neatoUser.chatPassword host:XMPP_SERVER_ADDRESS];
                                  }];
                 }];
}

#pragma mark - NeatoServerHelperProtocol
- (void)failedToSetUserAttributesWithError:(NSError *)error {
    debugLog(@"Failed to set user attribute.");
    self.retained_self = nil;
}

- (void)setUserAttributesSucceeded {
    debugLog(@"Set user attributes succeeded.");
    self.retained_self = nil;
}

#pragma mark - XMPPConnectionHelperProtocol
- (void)didConnectOverXMPP {
    debugLog(@"Did connect over XMPP.");
    self.completion ? self.completion(self.response, nil) : nil;

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
    self.retained_self = nil;
}

@end
