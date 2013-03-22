#import "RobotScheduleManager.h"
#import "LogHelper.h"
#import "ScheduleDBHelper.h"
#import "AppHelper.h"
#import "ScheduleUtils.h"
#import "ScheduleXMLHelper.h"
#import "BasicScheduleEvent.h"
#import "GetScheduleEventDataPluginResult.h"
#import "GetScheduleDataPluginResult.h"
#import "Schedule.h"
#import "ScheduleServerHelper.h"
#import "GetScheduleEventsListener.h"
#import "SetAdvancedScheduleListener.h"
#import "GetAdvancedScheduleListener.h"
#import "DeleteAdvancedScheduleListener.h"

// PluginResult Classes
#import "CreateSchedulePluginResult.h"
#import "CreateScheduleEventPluginResult.h"
#import "CreateSchedulePluginResult.h"
#import "CreateScheduleEventPluginResult.h"
#import "UpdateBasicScheduleListener.h"

@interface RobotScheduleManager() <GetScheduleEventsListenerProtocol>
@property(nonatomic, strong) RobotScheduleManager *retained_self;
@property(nonatomic, weak) id<RobotScheduleManagerProtocol> scheduleDelegate;
@property(nonatomic, strong) SetAdvancedScheduleListener *setScheduleListener;
@property(nonatomic, strong) GetAdvancedScheduleListener *getScheduleListener;
@property(nonatomic, strong) DeleteAdvancedScheduleListener *deleteScheduleListener;
@end

@implementation RobotScheduleManager

@synthesize retained_self = _retained_self;
@synthesize scheduleDelegate = _scheduleDelegate;
@synthesize setScheduleListener = _setScheduleListener;
@synthesize getScheduleListener = _getScheduleListener;
@synthesize deleteScheduleListener = _deleteScheduleListener;

- (id)createScheduleForRobotId:(NSString *)robotId forScheduleType:(NSString *)scheduleType {
    debugLog(@"");
    if([NEATO_SCHEDULE_ADVANCE isEqualToString:[ScheduleUtils getScheduleTypeString:scheduleType]]) {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Advance Schedule Type is not supported" forKey:NSLocalizedDescriptionKey];
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        return error;
    }
    NSString *scheduleId = [AppHelper generateUniqueString];
    id result = [ScheduleDBHelper createScheduleForRobotId:robotId ofScheduleType:[ScheduleUtils getScheduleTypeString:scheduleType] withScheduleId:scheduleId];
    if([result isKindOfClass:[NSError class]]) {
        return result;
    }
    else {
        CreateSchedulePluginResult *pluginResult = [[CreateSchedulePluginResult alloc] init];
        pluginResult.robotId = robotId;
        pluginResult.scheduleType = scheduleType;
        pluginResult.scheduleId = scheduleId;
        return pluginResult;
    }
}

- (id)addScheduleEventData:(NSDictionary *)scheduleEventData forScheduleWithScheduleId:(NSString *)scheduleId {
    id dbResult = [ScheduleDBHelper getScheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self addBasicScheduleEventData:scheduleEventData forscheduleWithScheduleId:scheduleId];
        }
        else {
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Advance Schedule Type is not supported" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            return error;
        }
    }
}

- (id)addBasicScheduleEventData:(NSDictionary *)scheduleEventData forscheduleWithScheduleId:(NSString *)scheduleId {
    NSString *scheduleEventId = [AppHelper generateUniqueString];
    BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] initWithDictionary:scheduleEventData andEventId:scheduleEventId];
    id dbResult = [ScheduleDBHelper addBasicScheduleEventData:[ScheduleXMLHelper getXMlfromBasicScheduleEvent:basicScheduleEvent] withScheduleEventId:scheduleEventId forScheduleId:scheduleId];
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
    id dbResult = [ScheduleDBHelper getScheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self updateBasicScheduleEventWithScheduleEventId:scheduleEventId forScheduleId:scheduleId withScheduleEventdata:scheduleEventData];
        }
        else {
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Advance Schedule Type is not supported" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            return error;
        }
    }
}

- (id)updateBasicScheduleEventWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId withScheduleEventdata:(NSDictionary *)scheduleEventData {
    BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] initWithDictionary:scheduleEventData andEventId:scheduleEventId];
    id dbResult = [ScheduleDBHelper updateBasicScheduleEventWithId:scheduleEventId withXMLData:[ScheduleXMLHelper getXMlfromBasicScheduleEvent:basicScheduleEvent]];
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
    id dbResult = [ScheduleDBHelper getScheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self deleteBasicScheduleWithScheduleEventId:scheduleEventId forScheduleId:scheduleId];
        }
        else {
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Advance Schedule Type is not supported" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            return error;
        }
    }
    
}

- (id)deleteBasicScheduleWithScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    id dbResult = [ScheduleDBHelper deleteBasicSchedleEventWithId:scheduleEventId];
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

- (id)getSchedueEventDataWithScheduleEventId:(NSString *)scheduleEventId withScheduleId:(NSString *)scheduleId {
    id dbResult = [ScheduleDBHelper getScheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self getBasicScheduleWithId:scheduleEventId withScheduleId:scheduleId];
        }
        else {
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Advance Schedule Type is not supported" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            return error;
        }
    }
}

- (id)getBasicScheduleWithId:(NSString *)scheduleEventId withScheduleId:(NSString *)scheduleId{
    id result = [ScheduleDBHelper getBasicScheduleEventDataWithId:scheduleEventId];
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

- (id)getScheduleDataForScheduleId:(NSString *)scheduleId {
    id dbResult = [ScheduleDBHelper getScheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        return dbResult;
    }
    else {
        NSString *scheduleType = (NSString *)dbResult;
        if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
            return [self getBasicScheduleWithId:scheduleId];
        }
        else {
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Advance Schedule Type is not supported" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            return error;
        }
    }
}

- (id)getBasicScheduleWithId:(NSString *)scheduleId {
    id result = [ScheduleDBHelper getBasicScheduleForScheduleId:scheduleId];
    if([result isKindOfClass:[NSError class]]) {
        return result;
    }
    else {
        Schedule *schedule = (Schedule *)result;
        GetScheduleDataPluginResult *pluginResult = [[GetScheduleDataPluginResult alloc] init];
        pluginResult.scheduleId = scheduleId;
        pluginResult.scheduleType = schedule.scheduleType;
        pluginResult.schedule = schedule;
        return pluginResult;
    }
}

- (void)getScheduleEventsForRobotWithId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType delegate:(id<RobotScheduleManagerProtocol>)delgate {
    self.retained_self = self;
    self.scheduleDelegate = delgate;
    
    debugLog(@"");
    NSString *scheduleTypeStr = [ScheduleUtils getScheduleTypeString:scheduleType];
    if (!scheduleTypeStr) {
        [self.scheduleDelegate failedToGetScheduleEventsForRobotWithId:robotId error:[AppHelper nserrorWithDescription:@"Invalid schedule type." code:200]];
        return;
    }
    
    GetScheduleEventsListener *eventsListener = [[GetScheduleEventsListener alloc]initWithDelegate:self];
    eventsListener.robotId = robotId;
    eventsListener.scheduleType = scheduleTypeStr;
    
    [eventsListener start];
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.scheduleDelegate performSelector:@selector(failedToGetScheduleEventsForRobotWithId:error:) withObject:robotId withObject:error];
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
}

- (void)gotScheduleEventsForSchedule:(Schedule *)schedule ofType:(NSInteger)scheduleType forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.scheduleDelegate gotScheduleEventsForSchedule:schedule ofType:scheduleType forRobotWithId:robotId];
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
}

- (void)updateScheduleForScheduleId:(NSString *)scheduleId delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.scheduleDelegate = delegate;
    id dbResult = [ScheduleDBHelper getScheduleTypeForScheduleId:scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        // TODO: Notify caller
        debugLog(@"Error in database.");
	return;
    }
    NSString *scheduleType = (NSString *)dbResult;
    if([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC] ) {
        [self updateBasicScheduleForScheduleId:scheduleId];
    }
    else {
        NSError *error = [AppHelper nserrorWithDescription:@"Advance Schedule Type is not supported." code:200];
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
    UpdateBasicScheduleListener *updateBasicScheduleListener = [[UpdateBasicScheduleListener alloc] initWithDelegate:self];
    updateBasicScheduleListener.scheduleId = scheduleId;
    [updateBasicScheduleListener start];
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

- (void)setRobotSchedule:(NSArray *)schedulesArray forRobotId:(NSString *)robotId ofType:(NSString *)scheduleType delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.scheduleDelegate = delegate;
    if([[ScheduleUtils getScheduleTypeString:scheduleType] isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        [self setRobotAdvancedSchedule:schedulesArray forRobotId:robotId];
    }
    else {
        // TODO: Basic Schedule.
        NSError *error = [AppHelper nserrorWithDescription:@"Basic Schedule type not implemented" code:200];
        [self notifyRequestFailed:@selector(setScheduleError:) withError:error];
    }
}

- (void)setRobotAdvancedSchedule:(NSArray *)scheduleGroup forRobotId:(NSString *)robotId {
    debugLog(@"");
    // Create appropriate listener and start
    self.setScheduleListener = [[SetAdvancedScheduleListener alloc] initWithDelegate:self];
    self.setScheduleListener.robotId = robotId;
    self.setScheduleListener.scheduleGroup = [[NSArray alloc] init];
    self.setScheduleListener.scheduleGroup = scheduleGroup;
    [self.setScheduleListener start]; 
}

- (void)getSchedulesForRobotId:(NSString *)robotId OfType:(NSString *)scheduleType delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.scheduleDelegate = delegate;
    if([[ScheduleUtils getScheduleTypeString:scheduleType] isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        [self getAdvancedSchedulesForRobotId:robotId];
    }
    else {
        // TODO: Basic Schedule.
        NSError *error = [AppHelper nserrorWithDescription:@"Basic Schedule type not implemented" code:200];
        [self notifyRequestFailed:@selector(getScheduleError:) withError:error];
    }  
}

- (void)getAdvancedSchedulesForRobotId:(NSString *)robotId {
    self.getScheduleListener = [[GetAdvancedScheduleListener alloc] initWithDelegate:self];
    self.getScheduleListener.robotId = robotId;
    [self.getScheduleListener start];
}

- (void)deleteScheduleForRobotId:(NSString *)robotId OfType:(NSString *)scheduleType delegate:(id)delegate {
    debugLog(@"");
    self.retained_self = self;
    self.scheduleDelegate = delegate;
    if([[ScheduleUtils getScheduleTypeString:scheduleType] isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        // Schedule Type is Advanced
        [self deleteAdvancedScheduleForRobotId:robotId];
    }
    else {
        // TODO: Basic Schedule.
        NSError *error = [AppHelper nserrorWithDescription:@"Basic Schedule type not implemented" code:200];
        [self notifyRequestFailed:@selector(deleteScheduleError:) withError:error];
    }  
}

- (void)deleteAdvancedScheduleForRobotId:(NSString *)robotId {
    self.deleteScheduleListener = [[DeleteAdvancedScheduleListener alloc] initWithDelegate:self];
    self.deleteScheduleListener.robotId = robotId;
    [self.deleteScheduleListener start];
}

- (void)setScheduleSuccess:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.scheduleDelegate respondsToSelector:@selector(setScheduleSuccess:)]) {
            [self.scheduleDelegate performSelector:@selector(setScheduleSuccess:) withObject:message];
        }
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });  
}

- (void)getScheduleSuccess:(NSDictionary *)jsonObject {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.scheduleDelegate respondsToSelector:@selector(getScheduleSuccess:)]) {
            [self.scheduleDelegate performSelector:@selector(getScheduleSuccess:) withObject:jsonObject];
        }
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
}

- (void)deleteScheduleSuccess:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.scheduleDelegate respondsToSelector:@selector(deleteScheduleSuccess:)]) {
            [self.scheduleDelegate performSelector:@selector(deleteScheduleSuccess:) withObject:message];
        }
        self.scheduleDelegate = nil;
        self.retained_self = nil;
    });
}

// Error callbacks
- (void)setScheduleError:(NSError *)error {
    [self notifyRequestFailed:@selector(setScheduleError:) withError:error];
}

- (void)getScheduleError:(NSError *)error {
    [self notifyRequestFailed:@selector(getScheduleError:) withError:error];
}

- (void)deleteScheduleError:(NSError *)error {
    [self notifyRequestFailed:@selector(deleteScheduleError:) withError:error];
}

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

@end
