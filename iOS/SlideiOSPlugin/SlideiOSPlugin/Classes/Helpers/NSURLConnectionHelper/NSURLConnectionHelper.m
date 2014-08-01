#import "NSURLConnectionHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "NeatoUserHelper.h"

// Helpers
#import "AFHTTPRequestOperation.h"
#import "NeatoErrorCodesHelper.h"
#import "NeatoErrorCodes.h"

@interface NSURLConnectionHelper()
@property (nonatomic, retain) NSURLConnectionHelper *retained_self;
@property (nonatomic, strong) ConnectionCompletionBlock connectionCompletionBlock;
@end

@implementation NSURLConnectionHelper

#pragma mark - Public Methods
- (void)getDataForRequest:(NSURLRequest *)request {
  debugLog(@"");
  if (request == nil) {
    debugLog(@"URLRequest cannot be nil. Stopping!");
    return;
  }
  self.retained_self = self;
  
  // Trace app info
  [AppHelper traceAppInfo];
  
  // Add Neato headers to each server request.
  request = [self requestWithNeatoHeaders:request];
  
  __weak typeof(self) weakSelf = self;
  AFHTTPRequestOperation *operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
  [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [weakSelf sendSuccessWithServerResponse:responseObject operation:operation];
    });
  } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [weakSelf sendFailureWithServerError:error operation:operation];
    });
  }];
  [operation start];
}

- (void)getDataForRequest:(NSURLRequest *)request completionBlock:(ConnectionCompletionBlock)completionBlock {
  self.connectionCompletionBlock = completionBlock;
  [self getDataForRequest:request];
}

#pragma mark - Private Helpers
- (void)sendSuccessWithServerResponse:(id)response operation:(AFHTTPRequestOperation *)operation {
  id responseStatus = [self checkStatusOfServerResponse:response];
  
  // We may need to send back error,
  // if response is nil/has failure status/parsing error.
  if ([responseStatus isKindOfClass:[NSError class]]) {
    NSError *error = (NSError *)responseStatus;
    // To support both type of calls,
    // Block based and delegate based.
    if (self.connectionCompletionBlock) {
      self.connectionCompletionBlock(nil, error);
    }
    else {
      if ([self.delegate respondsToSelector:@selector(didFailToLoadWithError:forRequest:)]) {
        [self.delegate didFailToLoadWithError:error forRequest:operation.request];
      }
    }
  }
  else {
    // To support both type of calls,
    // Block based and delegate based.
    if (self.connectionCompletionBlock) {
      self.connectionCompletionBlock(response, nil);
    }
    else {
      if ([self.delegate respondsToSelector:@selector(didLoadData:forRequest:)]) {
        [self.delegate didLoadData:response forRequest:operation.request];
      }
    }
  }
  self.retained_self = nil;
}

- (void)sendFailureWithServerError:(NSError *)error operation:(AFHTTPRequestOperation *)operation {
  NSError *neatoError = nil;
  // Check if it is HTTP status code is 401(Authentication Error), then send UI_ERROR_TYPE_USER_UNAUTHORIZED,
  // Else send Generic Networking Error UI_ERROR_NETWORK_ERROR.
  if (operation.response.statusCode == HTTP_STATUS_CODE_USER_UNAUTHORIZED) {
    neatoError = [AppHelper nserrorWithDescription:[error.userInfo objectForKey:NSLocalizedDescriptionKey] code:UI_ERROR_TYPE_USER_UNAUTHORIZED];
  }
  else {
    neatoError = [AppHelper nserrorWithDescription:[error.userInfo objectForKey:NSLocalizedDescriptionKey] code:UI_ERROR_NETWORK_ERROR];
  }
  
  // Send error callback
  if (self.connectionCompletionBlock) {
    self.connectionCompletionBlock(nil, neatoError);
  }
  else {
    if ([self.delegate respondsToSelector:@selector(didFailToLoadWithError:forRequest:)]) {
      [self.delegate didFailToLoadWithError:neatoError forRequest:operation.request];
    }
  }
  self.retained_self = nil;
}

- (id)checkStatusOfServerResponse:(id)response {
  // Return parsing error if it is not NSData.
  if (![response isKindOfClass:[NSData class]]) {
    NSError *parsingError = [AppHelper nserrorWithDescription:@"Failed to parse server response!" code:UI_JSON_PARSING_ERROR];
    return parsingError;
  }
  
  // Return error if got empty response.
  if (!response) {
    NSError *emptyResponseError = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:200];
    return emptyResponseError;
  }
  
  // Check if status is OK('0' for success).
  NSDictionary *jsonData = [AppHelper parseJSON:response];
  NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
  if ([status intValue] == NEATO_STATUS_SUCCESS) {
    NSDictionary *resultDict = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
    return resultDict;
  }
  else {
    // Return error if status is not '0'.
    NSDictionary *errorDict = [jsonData objectForKey:KEY_NEATO_SERVER_ERROR];
    NSError *serverError = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE]
                                                        code:[[NeatoErrorCodesHelper sharedErrorCodesHelper] uiErrorCodeForServerErrorCode:[[errorDict objectForKey:KEY_NEATO_SERVER_ERROR_CODE] integerValue]]];
    return serverError;
  }
}

- (NSURLRequest *)requestWithNeatoHeaders:(NSURLRequest *)request {
  NSMutableURLRequest *requestWithHeaders = [request mutableCopy];
  [requestWithHeaders addValue:[NeatoUserHelper uniqueDeviceIdForUser] forHTTPHeaderField:@"X-NEATO-UUID"];
  NSString *authToken = [NeatoUserHelper getUsersAuthToken];
  if ([authToken length] != 0) {
    [requestWithHeaders addValue:[NeatoUserHelper getUsersAuthToken] forHTTPHeaderField:@"X-NEATO-SESSION-ID"];
  }
  [requestWithHeaders addValue:[AppHelper appInfo] forHTTPHeaderField:@"X-NEATO-APPINFO"];
  return requestWithHeaders;
}

@end
