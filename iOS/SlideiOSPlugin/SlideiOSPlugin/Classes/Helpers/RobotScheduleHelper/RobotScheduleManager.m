#import "RobotScheduleManager.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "ScheduleUtils.h"
#import "ScheduleXMLHelper.h"
#import "BasicScheduleEvent.h"
#import "GetScheduleEventDataPluginResult.h"
#import "GetScheduleDataPluginResult.h"
#import "Schedule.h"
#import "ScheduleServerHelper.h"
#import "ScheduleJsonHelper.h"
#import "NeatoErrorCodes.h"
#import "NeatoDataStore.h"

// PluginResult Classes
#import "CreateSchedulePluginResult.h"
#import "CreateScheduleEventPluginResult.h"
#import "CreateSchedulePluginResult.h"
#import "CreateScheduleEventPluginResult.h"
#import "BasicScheduleManager.h"
#import "PluginConstants.h"

// Helpers
#import "AppSettings.h"

@interface RobotScheduleManager()
@property(nonatomic, strong) RobotScheduleManager *retained_self;
@property(nonatomic, weak) id<RobotScheduleManagerProtocol> scheduleDelegate;
@end

@implementation RobotScheduleManager

- (id)createScheduleForRobotId:(NSString *)robotId forScheduleType:(NSString *)scheduleType {
    debugLog(@"");
    if([NEATO_SCHEDULE_ADVANCE isEqualToString:[ScheduleUtils scheduleTypeString:scheduleType]]) {
        NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported" code:UI_ERROR_NOT_SUPPORTED];
        return error;
    }
    NSString *scheduleId = [AppHelper generateUniqueString];
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    id result = [database createScheduleForRobotId:robotId forScheduleType:[ScheduleUtils scheduleTypeString:scheduleType] withScheduleId:scheduleId];
    if([result isKindOfClass:[NSError class]]) {
        return result;
    }
    else {
        CreateSchedulePluginResult *pluginResult = [[CreateSchedulePluginResult alloc] init];
        pluginResult.robotId = robotId;
        pluginResult.scheduleType = [scheduleType integerValue];
        pluginResult.scheduleId = scheduleId;
        return pluginResult;
    }
}

- (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    id dbResult = [database scheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self addBasicScheduleEventData:scheduleEventData forscheduleWithScheduleId:scheduleId];
        }
        else {
            NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported" code:UI_ERROR_NOT_SUPPORTED];
            return error;
        }
    }
}

- (id)addBasicScheduleEventData:(NSDictionary *)scheduleEventData forscheduleWithScheduleId:(NSString *)scheduleId {
    NSString *scheduleEventId = [AppHelper generateUniqueString];
    BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] initWithDictionary:scheduleEventData andEventId:scheduleEventId];
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    id dbResult = [database addBasicScheduleEventData:basicScheduleEvent.parameterStr withScheduleEventId:scheduleEventId forScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        CreateScheduleEventPluginResult *pluginResult = [[CreateScheduleEventPluginResult alloc] init];
        pluginResult.scheduleId = scheduleId;
        pluginResult.scheduleEventId = scheduleEventId;
        return pluginResult;
    }
    
}

- (id)updateScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData {
    id dbResult =  [[NeatoDataStore sharedNeatoDataStore] scheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self updateBasicScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId withScheduleEventdata:scheduleEventData];
        }
        else {
            NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported" code:UI_ERROR_NOT_SUPPORTED];
            return error;
        }
    }
}

- (id)updateBasicScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData {
    BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] initWithDictionary:scheduleEventData andEventId:scheduleEventId];
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    id dbResult = [database updateBasicScheduleEventWithId:scheduleEventId withData:basicScheduleEvent.parameterStr];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        CreateScheduleEventPluginResult *pluginResult = [[CreateScheduleEventPluginResult alloc] init];
        pluginResult.scheduleId = scheduleId;
        pluginResult.scheduleEventId = scheduleEventId;
        return pluginResult;
    }
}

- (id)deleteScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    id dbResult = [[NeatoDataStore sharedNeatoDataStore] scheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self deleteBasicScheduleWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
        }
        else {
            NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported" code:UI_ERROR_NOT_SUPPORTED];
            return error;
        }
    }
}

- (id)deleteBasicScheduleWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    id dbResult = [[NeatoDataStore sharedNeatoDataStore] deleteBasicScheduleEventWithId:scheduleEventId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        CreateScheduleEventPluginResult *pluginResult = [[CreateScheduleEventPluginResult alloc] init];
        pluginResult.scheduleId = scheduleId;
        pluginResult.scheduleEventId = scheduleEventId;
        return pluginResult;
    }
}

- (id)scheduleEventDataWithScheduleEventId:(NSString *)scheduleEventId withScheduleId:(NSString *)scheduleId {
    id dbResult = [[NeatoDataStore sharedNeatoDataStore] scheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self basicScheduleWithScheduleEventId:scheduleEventId withScheduleId:scheduleId];
        }
        else {
            NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported" code:UI_ERROR_NOT_SUPPORTED];
            return error;
        }
    }
}

- (id)basicScheduleWithScheduleEventId:(NSString *)scheduleEventId withScheduleId:(NSString *)scheduleId{
    id result = [[NeatoDataStore sharedNeatoDataStore] basicScheduleEventWithId:scheduleEventId];
    if([result isKindOfClass:[NSError class]]) {
        return result;
    }
    else {
        GetScheduleEventDataPluginResult *pluginResult = [[GetScheduleEventDataPluginResult alloc] init];
        pluginResult.scheduleEventId = scheduleEventId;
        pluginResult.scheduleId = scheduleId;
        pluginResult.basicScheduleEvent = (BasicScheduleEvent *)result;
        return pluginResult;
    }
}

- (id)scheduleDataForScheduleId:(NSString *)scheduleId {
    id dbResult = [[NeatoDataStore sharedNeatoDataStore] scheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self basicScheduleWithScheduleId:scheduleId];
        }
        else {
            NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported" code:UI_ERROR_NOT_SUPPORTED];
            return error;
        }
    }
}

- (id)basicScheduleWithScheduleId:(NSString *)scheduleId {
    id result = [[NeatoDataStore sharedNeatoDataStore] basicScheduleForScheduleId:scheduleId];
    if([result isKindOfClass:[NSError class]]) {
        return result;
    }
    else {
        Schedule *schedule = (Schedule *)result;
        GetScheduleDataPluginResult *pluginResult = [[GetScheduleDataPluginResult alloc] init];
        pluginResult.scheduleId = scheduleId;
        pluginResult.scheduleType = [schedule.scheduleType integerValue];
        pluginResult.schedule = schedule;
        return pluginResult;
    }
}

- (void)updateScheduleForScheduleId:(NSString *)scheduleId delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.scheduleDelegate = delegate;
    id dbResult = [[NeatoDataStore sharedNeatoDataStore] scheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        debugLog(@"Error in database.");
        NSError *error = [AppHelper nserrorWithDescription:@"Could not get schedule type from database." code:UI_ERROR_DB_ERROR];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.scheduleDelegate respondsToSelector:@selector(updateScheduleError:)]) {
                [self.scheduleDelegate performSelector:@selector(updateScheduleError:) withObject:error];
            }
            self.scheduleDelegate = nil;
            self.retained_self = nil;
        });
        return;
    }
    NSString *scheduleType = (NSString *)dbResult;
    if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
        [self updateBasicScheduleForScheduleId:scheduleId];
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported." code:UI_ERROR_NOT_SUPPORTED];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.scheduleDelegate respondsToSelector:@selector(updateScheduleError:)]) {
                [self.scheduleDelegate performSelector:@selector(updateScheduleError:) withObject:error];
            }
            self.scheduleDelegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)updateBasicScheduleForScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    BasicScheduleManager *basicScheduleManager = [[BasicScheduleManager alloc] initWithDelegate:self];
    basicScheduleManager.scheduleId = scheduleId;
    [basicScheduleManager start];
}

- (void)updatedSchedule:(NSString *)scheduleId {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.scheduleDelegate respondsToSelector:@selector(updatedSchedule:)]) {
            [self.scheduleDelegate performSelector:@selector(updatedSchedule:) withObject:scheduleId];
        }
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
    
}

- (void)updateScheduleError:(NSError *)error {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.scheduleDelegate respondsToSelector:@selector(updateScheduleError:)]) {
            [self.scheduleDelegate performSelector:@selector(updateScheduleError:) withObject:error];
        }
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
}

// Error callbacks
- (void)notifyRequestFailed:(SEL) selector withError:(NSError *) error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.scheduleDelegate respondsToSelector:selector]) {
            [self.scheduleDelegate performSelector:selector withObject:error];
        }
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
}

- (void)scheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType completion:(RequestCompletionBlockDictionary)completion {
  debugLog(@"");
  NSString *scheduleTypeString = [ScheduleUtils scheduleTypeString:scheduleType];
  // Check if it is other than Basic or Advanced,
  // else send error.
  if (!scheduleTypeString) {
    NSError *invalidScheduleError = [AppHelper nserrorWithDescription:@"Invalid schedule type." code:UI_ERROR_INVALID_SCHEDULE_TYPE];
    completion(nil, invalidScheduleError);
    return;
  }
  
  // Advanced schedule is not supported, send error.
  if ([scheduleTypeString isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
    NSError *notSupportedError = [AppHelper nserrorWithDescription:@"Advance schedule type is not supported" code:UI_ERROR_NOT_SUPPORTED];
    completion(nil, notSupportedError);
    return;
  }
  
  // Get schedule from server.
  // At server '1' means Basic and '2' means Advanced.
  // So convert those strings.
  NSString *serverScheduleType = [NSString stringWithFormat:@"%ld", (long)[ScheduleUtils serverScheduleIntFromString:scheduleTypeString]];
  NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_SCHEDULE_BASED_ON_TYPE]];
  [request setHTTPMethod:@"POST"];
  [request setHTTPBody:[[NSString stringWithFormat:GET_SCHEDULE_BASED_ON_TYPE_POST_STRING, API_KEY, robotId, serverScheduleType] dataUsingEncoding:NSUTF8StringEncoding]];
  
  ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
  [serverHelper dataForRequest:request
               completionBlock:^(id response, NSError *error) {
                 if (error) {
                   completion ? completion(nil, error) : nil;
                   return;
                 }
                 
                 id responseResult = [response valueForKey:NEATO_RESPONSE_RESULT];
                 if (![responseResult isKindOfClass:[NSArray class]]) {
                   NSError *parsingError = [AppHelper nserrorWithDescription:@"Failed to parse server response!" code:UI_JSON_PARSING_ERROR];
                   completion ? completion(nil, parsingError) : nil;
                   return;
                 }
                 
                 NSArray *resultArray = (NSArray *)responseResult;
                 NSDictionary *serverSchedule = [resultArray objectAtIndex:0];
                 Schedule *schedule = [ScheduleJsonHelper scheduleFromDictionary:serverSchedule];
                 
                 // Check if schedule is nil or not, as there could be a parsing error.
                 if (schedule) {
                   // Save schedule in DB.
                   [[NeatoDataStore sharedNeatoDataStore] saveSchedule:schedule ofType:scheduleTypeString forRobotWithId:robotId];
                   // Prepare data, to send to JS layer.
                   NSInteger scheduleTypeInteger = [ScheduleUtils scheduleIntFromString:scheduleTypeString];
                   NSMutableDictionary *pluginResultDictionary = [[NSMutableDictionary alloc] init];
                   [pluginResultDictionary setValue:schedule.scheduleId forKey:KEY_SCHEDULE_ID];
                   [pluginResultDictionary setValue:robotId forKey:KEY_ROBOT_ID];
                   [pluginResultDictionary setValue:[NSNumber numberWithInteger:scheduleTypeInteger] forKey:KEY_SCHEDULE_TYPE];
                   [pluginResultDictionary setValue:[schedule arrayOfScheduleEventIdsForType:scheduleTypeInteger] forKey:KEY_SCHEDULE_EVENTS_LIST];
                   completion ? completion(pluginResultDictionary, nil) : nil;
                 }
                 else {
                   NSError *parsingError = [AppHelper nserrorWithDescription:@"Failed to parse server response!" code:UI_JSON_PARSING_ERROR];
                   completion ? completion(nil, parsingError) : nil;
                   return;
                 }
               }];
}

@end
