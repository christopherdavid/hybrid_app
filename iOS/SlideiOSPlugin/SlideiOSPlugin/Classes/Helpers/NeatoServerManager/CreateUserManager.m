#import "CreateUserManager.h"
#import "LogHelper.h"
#import "NeatoUser.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "AppHelper.h"
#import "AppSettings.h"
#import "NeatoErrorCodes.h"

@implementation CreateUserManager

#pragma mark - Public
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion {
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_CREATE_USER3_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:CREATE_USER3_POST_STRING, API_KEY, self.user.name, self.user.email, self.user.alternateEmail, self.user.password, self.user.account_type, [self.user extraParam]] dataUsingEncoding:NSUTF8StringEncoding]];
    
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
                                      completion ? completion(responseResultDict, nil) : nil;
                                  }];
                 }];
}

@end
