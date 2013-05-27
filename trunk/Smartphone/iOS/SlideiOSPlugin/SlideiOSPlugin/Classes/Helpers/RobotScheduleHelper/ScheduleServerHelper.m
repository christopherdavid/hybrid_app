#import "ScheduleServerHelper.h"
#import "LogHelper.h"
#import "NSURLConnectionHelper.h"
#include <objc/runtime.h>
#import "AppHelper.h"
#import "PostScheduleResult.h"
#import "NeatoConstants.h"

#define GET_SCHEDULE_DATA_POST_STRING @"api_key=%@&robot_schedule_id=%@"
#define GET_SCHEDULE_DATA_RESPONSE_HANDLER @"gotScheduleResponseData:forScheduleId:"

#define GET_SCHEDULES_POST_STRING @"api_key=%@&serial_number=%@"
#define GET_SCHEDULES_RESPONSE_HANDLER @"gotSchedulesResponse:forRobotWithId:"
#define SERVER_REPONSE_HANDLER_KEY @"getSchedulesForRobotWithId:"
#define ROBOT_ID_SERVER_KEY @"robot_id_header_key"
#define SCHEDULE_ID_SERVER_KEY @"schedule_id_header_key"

#define GET_POST_ROBOT_SCHEDULE_STRING @"api_key=%@&serial_number=%@&schedule_type=%@&xml_data=%@"
#define GET_UPDATE_ROBOT_SCHEDULE_DATA_POST_STRING @"api_key=%@&robot_schedule_id=%@&schedule_type=%@&xml_data_version=%@&xml_data=%@"
#define GET_DELETE_SCHEDULE_POST_STRING @"api_key=%@&robot_schedule_id=%@"
#define GET_POST_ROBOT_SCHEDULE_RESPONSE_HANDLER @"postRobotScheduleHandler:"
#define GET_UPDATE_ROBOT_SCHEDULE_RESPONSE_HANDLER @"upadateRobotScheduleHandler:"
#define GET_DELETE_SCHEDULE_RESPONSE_HANDLER @"deleteScheduleDataResponseHandler:"
#define GET_SCHEDULE_BASED_ON_TYPE_POST_STRING @"api_key=%@&robot_serial_number=%@&schedule_type=%@"
#define GET_SCHEDULE_BASED_ON_TYPE_RESPONSE_HANDLER @"getScheduleBasedOnTypeResponseHandler:"

@interface ScheduleServerHelper()

@property(nonatomic, strong) ScheduleServerHelper *retainedSelf;
@end

@implementation ScheduleServerHelper
@synthesize retainedSelf = _retainedSelf;
@synthesize delegate = _delegate;

- (void)getSchedulesForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retainedSelf = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_SCHEDULES_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_SCHEDULES_POST_STRING, NEATO_API_KEY, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
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
        [self.delegate failedToGetSchedulesForRobotId:robotId withError:[AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:ERROR_TYPE_UNKNOWN]];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        debugLog(@"Get schedules request failed. With Error = %@!", value);
        NSError *serverError = (NSError *)value;
        [self.delegate failedToGetSchedulesForRobotId:robotId withError:[AppHelper nserrorWithDescription:[serverError.userInfo objectForKey:NSLocalizedDescriptionKey] code:ERROR_NETWORK_ERROR]];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
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
        [self.delegate failedToGetSchedulesForRobotId:robotId withError:[AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:ERROR_SERVER_ERROR]];
    }
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection responseData:(NSData *)responseData {
    debugLog(@"");
    NSURLRequest *request = [connection originalRequest];
    NSString *selectorStr = [request valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    if ([selectorStr isEqualToString:GET_SCHEDULES_RESPONSE_HANDLER]) {
        NSString *robotId = [request valueForHTTPHeaderField:ROBOT_ID_SERVER_KEY];
        [self performSelector:selector withObject:responseData withObject:robotId];
        return;
    }
    else if ([selectorStr isEqualToString:GET_SCHEDULE_DATA_RESPONSE_HANDLER]) {
        NSString *scheduleId = [request valueForHTTPHeaderField:SCHEDULE_ID_SERVER_KEY];
        [self performSelector:selector withObject:responseData withObject:scheduleId];
        return;
    }
    [self performSelector:selector withObject:responseData];
}

// This gets called when the connection fails for any reason.
- (void)requestFailedForConnection:(NSURLConnection *)connection error:(NSError *) error {
    debugLog(@"");
    NSURLRequest *request = [connection originalRequest];
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
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_SCHEDULE_DATA_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_SCHEDULE_DATA_POST_STRING, NEATO_API_KEY, scheduleId] dataUsingEncoding:NSUTF8StringEncoding]];
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
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
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
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_POST_ROBOT_SCHEDULE_DATA]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_POST_ROBOT_SCHEDULE_STRING, NEATO_API_KEY, robotId,scheduleType, xmlData] dataUsingEncoding:NSUTF8StringEncoding]];
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
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
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
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_UPDATE_ROBOT_SCHEDULE_DATA]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_UPDATE_ROBOT_SCHEDULE_DATA_POST_STRING, NEATO_API_KEY, scheduleId, scheduleType, scheduleVersion, data] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_UPDATE_ROBOT_SCHEDULE_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)upadateRobotScheduleHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:ERROR_TYPE_UNKNOWN];
        [self notifyRequestFailed:@selector(updateScheduleError:) withError:error];
        return;
    }
    
    if ([value isKindOfClass:[NSError class]]) {
        NSError *networkError = (NSError *)value;
        NSError *error = [AppHelper nserrorWithDescription:[networkError.userInfo objectForKey:NSLocalizedDescriptionKey] code:ERROR_NETWORK_ERROR];
        [self notifyRequestFailed:@selector(updateScheduleError:) withError:error];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
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
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:ERROR_SERVER_ERROR];
        [self notifyRequestFailed:@selector(updateScheduleError:) withError:error];
    }
}

- (void)deleteScheduleDataForScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    self.retainedSelf = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_DELETE_SCHEDULE_DATA]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_DELETE_SCHEDULE_POST_STRING, NEATO_API_KEY, scheduleId] dataUsingEncoding:NSUTF8StringEncoding]];
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
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
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

- (void)scheduleBasedOnType:(NSString *)scheduleType forRobotId:(NSString *)robotId {
    debugLog(@"");
    self.retainedSelf = self;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_SCHEDULE_BASED_ON_TYPE]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_SCHEDULE_BASED_ON_TYPE_POST_STRING, NEATO_API_KEY, robotId, scheduleType] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:GET_SCHEDULE_BASED_ON_TYPE_RESPONSE_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

- (void)getScheduleBasedOnTypeResponseHandler:(id)value {
    debugLog(@"");
    if (value == nil) {
        NSError *error = [AppHelper nserrorWithDescription:@"Server did not respond with any data!" code:ERROR_TYPE_UNKNOWN];
        debugLog(@"Get schedule based on type failed!");
        [self notifyRequestFailed:@selector(failedToGetScheduleWithError:) withError:error];
        return;
    }
    if ([value isKindOfClass:[NSError class]]) {
        NSError *networkError = (NSError *)value;
        NSError *error = [AppHelper nserrorWithDescription:[networkError.userInfo objectForKey:NSLocalizedDescriptionKey] code:ERROR_NETWORK_ERROR];
        debugLog(@"Get schedule based on type failed!");
        [self notifyRequestFailed:@selector(failedToGetScheduleWithError:) withError:error];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    NSArray *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotScheduleWithData:)]) {
                [self.delegate performSelector:@selector(gotScheduleWithData:) withObject:data];
            }
            self.delegate = nil;
            self.retainedSelf = nil;
        });
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] code:ERROR_SERVER_ERROR];
        debugLog(@"error reason : %@",[error localizedDescription]);
        [self notifyRequestFailed:@selector(failedToGetScheduleWithError:) withError:error];
    }
}

@end
