#import "LoginManager.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "XMPPConnectionHelper.h"
#import "XMPPConnection.h"
#import "AppSettings.h"
#import "AppHelper.h"
#import "NeatoErrorCodes.h"
#import "NeatoUserAttributes.h"

@interface LoginManager()
@property (nonatomic, retain) LoginManager *retained_self;
@property (nonatomic, strong) RequestCompletionBlockDictionary completion;
@property (nonatomic, strong) NSDictionary *response;
@end

@implementation LoginManager

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
                   
                     NSString *authToken = [response valueForKey:NEATO_RESPONSE_RESULT];
                     // NOTE: If auth token value is nil then validation status would
                     // be -2 and we have to send back error message in extra params
                     // to caller.
                     if ([AppHelper isStringNilOrEmpty:authToken]) {
                         NSError *error = [AppHelper nserrorWithDescription:[[response valueForKey:NEATO_RESPONSE_EXTRA_PARAMS] valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_USER_UNAUTHORIZED];
                         completion ? completion(nil, error) : nil;
                         return;
                     }

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
                                      
                                      // Login Over XMPP only if user validation status is VALID.
                                      if ([[neatoUser userValidationStatus] integerValue] == VALIDATION_STATUS_VALIDATED) {
                                          // Save authtoken and User details only if 'Validation status' is VALID.
                                          [NeatoUserHelper saveUserAuthToken:authToken];
                                          [NeatoUserHelper saveNeatoUser:neatoUser];
                                          
                                          // Set user attributes.
                                          [weakSelf setUserAttributesOnServer];
                                          
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
                                      }
                                      else {
                                          completion ? completion(responseResultDict, nil) : nil;
                                      }
                                  }];
                 }];
}

#pragma mark - XMPPConnectionHelperProtocol
- (void)didConnectOverXMPP {
    debugLog(@"didConnectOverXMPP called");
    self.completion ? self.completion(self.response, nil) : nil;
    self.retained_self = nil;
}

- (void)xmppLoginfailedWithError:(NSError *)error {
	debugLog(@"xmpp Login failed");
    self.completion ? self.completion(nil, error) : nil;
    self.retained_self = nil;
}

- (void)didDisConnectFromXMPP {
  NSError *error = [AppHelper nserrorWithDescription:@"Failed to connect over XMPP" code:UI_ERROR_TYPE_UNKNOWN];
  self.completion ? self.completion(nil, error) : nil;
    self.retained_self = nil;
}

#pragma mark - Private
- (void)alreadyConnectedToXMPP {
    debugLog(@"XMPP connection already exists");
    self.completion ? self.completion(self.response, nil) : nil;
    self.retained_self = nil;
}

#pragma mark - NeatoServerHelperProtocol
- (void)failedToSetUserAttributesWithError:(NSError *)error {
    debugLog(@"Failed to set user attribute.");
}

- (void)setUserAttributesSucceeded {
    debugLog(@"Set user attributes succeeded.");
}

#pragma mark - Private Helper
- (void)setUserAttributesOnServer {
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

@end
