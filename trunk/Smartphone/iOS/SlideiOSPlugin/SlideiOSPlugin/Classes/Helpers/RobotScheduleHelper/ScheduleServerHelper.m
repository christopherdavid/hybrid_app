#import "ScheduleServerHelper.h"
#import "LogHelper.h"
#import "NSURLConnectionHelper.h"
#include <objc/runtime.h>
#import "AppHelper.h"
#import "PostScheduleResult.h"
#import "NeatoConstants.h"
#import "AppSettings.h"
#import "NeatoErrorCodes.h"
#import "NeatoErrorCodesHelper.h"

@interface ScheduleServerHelper() <NSURLConnectionHelperProtocol>
@property(nonatomic, strong) ScheduleServerHelper *retainedSelf;
@end

@implementation ScheduleServerHelper

- (void)getSchedulesForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retainedSelf = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_SCHEDULES_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_SCHEDULES_POST_STRING, API_KEY, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:robotId forHTTPHeaderField:ROBOT_ID_SERVER_KEY];
    [request setValue:GET_SCHEDULES_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)gotSchedulesResponse:(id)value forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if (value == nil) {
        debugLog(@"Get schedules request failed!");
        [self.delegate failedToGetSchedulesForRobotId:robotId withError:[AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:UI_ERROR_TYPE_UNKNOWN]];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Get schedules request failed. With Error = %@!", value);
        [self.delegate failedToGetSchedulesForRobotId:robotId withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        id schedules = [jsonData objectForKey:NEATO_RESPONSE_RESULT];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate gotSchedulesData:schedules forRobotId:robotId];
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
    else {
        [self.delegate failedToGetSchedulesForRobotId:robotId withError:[AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_UNKNOWN]];
    }
}

- (void)didLoadData:(NSData *)responseData forRequest:(NSURLRequest *)request {
    debugLog(@"");
    id serverResponse = responseData;
    if ([AppHelper hasServerRequestFailedForResponse:[AppHelper parseJSON:responseData]]) {
        NSDictionary *errorDict = [[AppHelper parseJSON:serverResponse] objectForKey:KEY_NEATO_SERVER_ERROR];
        serverResponse = [AppHelper nserrorWithDescription:[errorDict objectForKey:NEATO_RESPONSE_MESSAGE] code:[[NeatoErrorCodesHelper sharedErrorCodesHelper] uiErrorCodeForServerErrorCode:[[errorDict objectForKey:KEY_NEATO_SERVER_ERROR_CODE] integerValue]]];
    }

    NSString *selectorStr = [request valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    if ([selectorStr isEqualToString:GET_SCHEDULES_RESPONSE_HANDLER]) {
        NSString *robotId = [request valueForHTTPHeaderField:ROBOT_ID_SERVER_KEY];
        [self performSelector:selector withObject:serverResponse withObject:robotId];
        return;
    }
    else if ([selectorStr isEqualToString:GET_SCHEDULE_DATA_RESPONSE_HANDLER]) {
        NSString *scheduleId = [request valueForHTTPHeaderField:SCHEDULE_ID_SERVER_KEY];
        [self performSelector:selector withObject:serverResponse withObject:scheduleId];
        return;
    }
    [self performSelector:selector withObject:serverResponse];
}

// This gets called when the request fails for any reason.
- (void)didFailToLoadWithError:(NSError *)error forRequest:(NSURLRequest *)request {
    debugLog(@"");
    NSString *selectorStr = [request valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    if ([selectorStr isEqualToString:GET_SCHEDULES_RESPONSE_HANDLER]) {
        NSString *robotId = [request valueForHTTPHeaderField:ROBOT_ID_SERVER_KEY];
        [self performSelector:selector withObject:error withObject:robotId];
        return;
    }
    else if ([selectorStr isEqualToString:GET_SCHEDULE_DATA_RESPONSE_HANDLER]) {
        NSString *scheduleId = [request valueForHTTPHeaderField:SCHEDULE_ID_SERVER_KEY];
        [self performSelector:selector withObject:error withObject:scheduleId];
        return;
    }
    [self performSelector:selector withObject:error];
}

- (void)getDataForScheduleWithId:(NSString *)scheduleId {
    debugLog(@"");
    self.retainedSelf = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_SCHEDULE_DATA_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_SCHEDULE_DATA_POST_STRING, API_KEY, scheduleId] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:scheduleId forHTTPHeaderField:SCHEDULE_ID_SERVER_KEY];
    [request setValue:GET_SCHEDULE_DATA_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)gotScheduleResponseData:(id)value forScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    if (value == nil) {
        debugLog(@"Get schedule data request failed!");
        [self.delegate failedToGetScheduleDataForScheduleId:scheduleId withError:[AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:200]];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Get schedule data request failed. With Error = %@!", value);
        [self.delegate failedToGetScheduleDataForScheduleId:scheduleId withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        id scheduleData = [jsonData objectForKey:NEATO_RESPONSE_RESULT];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate gotScheduleData:scheduleData forScheduleId:scheduleId];
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
    else {
        [self.delegate failedToGetScheduleDataForScheduleId:scheduleId withError:[AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:200]];
    }
}

- (void)postScheduleForRobotId:(NSString *)robotId withScheduleData:(NSString *)xmlData ofScheduleType:(NSString *)scheduleType {
    debugLog(@"");
    self.retainedSelf = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_POST_ROBOT_SCHEDULE_DATA]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_POST_ROBOT_SCHEDULE_STRING, API_KEY, robotId,scheduleType, xmlData] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_POST_ROBOT_SCHEDULE_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void )postRobotScheduleHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:200];
        debugLog(@"Post Robot Schedules request failed!");
        [self notifyRequestFailed:@selector(postScheduleError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Post Robot Schedules request failed!");
        [self notifyRequestFailed:@selector(postScheduleError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        PostScheduleResult *postScheduleResult = [[PostScheduleResult alloc] initWithDictionary:[jsonData valueForKey:NEATO_RESPONSE_RESULT]];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(postedSchedule:)]) {
                [self.delegate performSelector:@selector(postedSchedule:) withObject:postScheduleResult];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:200];
        debugLog(@"error reason : %@", [error localizedDescription]);
        [self notifyRequestFailed:@selector(postScheduleError:) withError:error];
    }
}

- (void)updateScheduleDataForScheduleId:(NSString *)scheduleId withScheduleVersion:(NSString *)scheduleVersion withScheduleData:(NSString *)data ofScheduleType:(NSString *)scheduleType {
    debugLog(@"");
    self.retainedSelf = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_UPDATE_ROBOT_SCHEDULE_DATA]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_UPDATE_ROBOT_SCHEDULE_DATA_POST_STRING, API_KEY, scheduleId, scheduleType, scheduleVersion, data] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_UPDATE_ROBOT_SCHEDULE_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)upadateRobotScheduleHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:UI_ERROR_TYPE_UNKNOWN];
        [self notifyRequestFailed:@selector(updateScheduleError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        [self notifyRequestFailed:@selector(updateScheduleError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    NSDictionary *result = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(updatedScheduleWithResult:)]) {
                [self.delegate performSelector:@selector(updatedScheduleWithResult:) withObject:result];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:UI_ERROR_TYPE_UNKNOWN];
        [self notifyRequestFailed:@selector(updateScheduleError:) withError:error];
    }
}

- (void)deleteScheduleDataForScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    self.retainedSelf = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_DELETE_SCHEDULE_DATA]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_DELETE_SCHEDULE_POST_STRING, API_KEY, scheduleId] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_DELETE_SCHEDULE_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)deleteScheduleDataResponseHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:200];
        debugLog(@"Delete Robot Schedules request failed!");
        [self notifyRequestFailed:@selector(deleteScheduleDataError:) withError:error];
        return;
    }
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"delete Robot Schedules request failed!");
        [self notifyRequestFailed:@selector(deleteScheduleDataError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInteger:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
    NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(deletedScheduleData:)]) {
                [self.delegate performSelector:@selector(deletedScheduleData:) withObject:message];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:NEATO_RESPONSE_MESSAGE code:200];
        debugLog(@"error reason : %@",[error localizedDescription]);
        [self notifyRequestFailed:@selector(deleteScheduleDataError:) withError:error];
    }
}

- (void)notifyRequestFailed:(SEL) selector withError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector]) {
            [self.delegate performSelector:selector withObject:error];
        }
        self.delegate = nil;
        self.retainedSelf = nil;
    });
}

- (void)dataForRequest:(NSURLRequest *)request completionBlock:(ServerHelperCompletionBlock)completionBlock {
  [[[NSURLConnectionHelper alloc] init] getDataForRequest:request
                                          completionBlock:^(id response, NSError *error) {
                                              if (error) {
                                                  completionBlock ? completionBlock(nil, error) : nil;
                                                  return;
                                              }
                                              NSDictionary *completeServerResponseDict = [AppHelper parseJSON:response];
                                              completionBlock ? completionBlock(completeServerResponseDict, nil) : nil;
                                          }];
}

@end
