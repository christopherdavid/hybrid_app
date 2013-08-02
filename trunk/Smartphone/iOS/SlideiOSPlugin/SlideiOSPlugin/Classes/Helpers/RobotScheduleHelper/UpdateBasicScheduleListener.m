#import "UpdateBasicScheduleListener.h"
#import "Schedule.h"
#import "ScheduleDBHelper.h"
#import "ScheduleServerhelper.h"
#import "AppHelper.h"
#import "NeatoConstants.h"
#import "PostScheduleResult.h"
#import "LogHelper.h"
#import "NSDictionary+StringValueForKey.h"
#import "ScheduleUtils.h"
#import "ScheduleJsonHelper.h"
#import "SchedulerConstants.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotCommand.h"
#import "ProfileDetail.h"
#import "NeatoRobotHelper.h"


@interface UpdateBasicScheduleListener()

@property(nonatomic, strong) UpdateBasicScheduleListener *retained_self;
@property(nonatomic, strong) NSString *robotId;
@property(nonatomic, strong) Schedule *schedule;
@property(nonatomic, strong) NSString *scheduleData;
@property(nonatomic, weak) id delegate;

@end

@implementation UpdateBasicScheduleListener

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize scheduleId = _scheduleId;
@synthesize robotId = _robotId;
@synthesize schedule = _schedule;
@synthesize scheduleData = _scheduleData;

- (id)initWithDelegate:(id)delegate {
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)start {
    id dbResult = [ScheduleDBHelper basicScheduleForScheduleId:self.scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        [self updateScheduleError:[AppHelper nserrorWithDescription:@"No basic schedule for this scheduleId in database." code:INVALID_SCHEDULE_ID]];
        return;
    }
    self.schedule = (Schedule *)dbResult;
    self.robotId = [ScheduleDBHelper robotIdForScheduleId:self.scheduleId];
    if([AppHelper isStringNilOrEmpty:self.schedule.serverScheduleId]) {
        // If there is no serverScheduleId in database then get latest scheduleId
        // and version from server.
        [self currentScheduleIdAndVersionForRobotId:self.robotId];
    }
    else {
        [self updateScheduleDataForScheduleId:self.schedule.serverScheduleId withScheduleVersion:self.schedule.scheduleVersion withScheduleData:[ScheduleJsonHelper jsonFromSchedule:self.schedule] ofScheduleType:NEATO_SCHEDULE_BASIC];
    }
}

- (void)postedSchedule:(PostScheduleResult *)result {
    debugLog(@"");
    // Save serverScheduleId and schedule version in DB.
    [ScheduleDBHelper updateServerScheduleId:result.serverScheduleId andScheduleVersion:result.scheduleVersion forScheduleWithScheduleId:self.scheduleId];
    [self.delegate performSelector:@selector(updatedSchedule:) withObject:self.scheduleId];
    [self notifyScheduleUpdated];
}

- (void)postScheduleError:(NSError *)error {
    [self updateScheduleError:error];
}

- (void)updatedScheduleWithResult:(id)result {
    NSDictionary *resultDict = (NSDictionary *)result;
    
    // Update schedule version in DB.
    [ScheduleDBHelper updateScheduleVersion:[resultDict stringForKey:KEY_SCHEDULE_VERSION] forScheduleWithScheduleId:self.scheduleId];
    [self.delegate performSelector:@selector(updatedSchedule:) withObject:self.scheduleId];
     [self notifyScheduleUpdated];
}

- (void)updateScheduleError:(NSError *)error {
    [self.delegate performSelector:@selector(updateScheduleError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)currentScheduleIdAndVersionForRobotId:(NSString *)robotId {
    debugLog(@"");
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper getSchedulesForRobotWithId:self.robotId];
}


- (void)gotSchedulesData:(id)scheduleData forRobotId:(NSString *)robotId {
    debugLog(@"");
    if (![scheduleData isKindOfClass:[NSArray class]]) {
        [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:robotId withObject:[AppHelper nserrorWithDescription:@"Failed to parse server response!" code:ERROR_SERVER_ERROR]];
        self.delegate = nil;
        self.retained_self = nil;
        return;
    }
    NSArray *schedules = (NSArray *)scheduleData;
    
    // There should not be multiple schedules of a type for a robot although server supports it.
    // Programmatically we take care of not creating multiple schedules on
    // server.If there are multiple schedules on server we update latest one.
    // This is under assumption that schedules are sent in descending order.
    NSDictionary *scheduleDictionary = nil;
    for (int i = [schedules count] - 1; i>=0; i--) {
        NSDictionary *data = [schedules objectAtIndex:i];
        id scheduleType = [data valueForKey:SCHEDULE_TYPE];
        if ([scheduleType isKindOfClass:[NSString class]] && [scheduleType caseInsensitiveCompare:NEATO_SCHEDULE_BASIC] == NSOrderedSame) {
            scheduleDictionary = data;
            break;
        }
    }
    // If scheduleDictionary is nil then we need to post schedule
    // else update.
    if (!scheduleDictionary) {
        [self postScheduleForRobotId:self.robotId withScheduleData:[ScheduleJsonHelper jsonFromSchedule:self.schedule] ofScheduleType:NEATO_SCHEDULE_BASIC];
    }
    else {
        // Update latest serverScheduleId and scheduleVersion in DB.
        [ScheduleDBHelper updateServerScheduleId:[scheduleDictionary valueForKey:KEY_ID] andScheduleVersion:[scheduleDictionary valueForKey:KEY_XML_DATA_VERSION] forScheduleWithScheduleId:self.scheduleId];
        [self updateScheduleDataForScheduleId:[scheduleDictionary valueForKey:KEY_ID] withScheduleVersion:[scheduleDictionary valueForKey:KEY_XML_DATA_VERSION] withScheduleData:[ScheduleJsonHelper jsonFromSchedule:self.schedule] ofScheduleType:NEATO_SCHEDULE_BASIC];
    }
    
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    debugLog(@"");
    [self updateScheduleError:error];
}

- (void)postScheduleForRobotId:(NSString *)robotId withScheduleData:(NSString *)xmlData ofScheduleType:(NSString *)scheduleType {
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper postScheduleForRobotId:robotId withScheduleData:xmlData ofScheduleType:scheduleType];
}

- (void)updateScheduleDataForScheduleId:(NSString *)scheduleId withScheduleVersion:(NSString *)scheduleVersion withScheduleData:(NSString *)data ofScheduleType:(NSString *)scheduleType {
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper updateScheduleDataForScheduleId:scheduleId withScheduleVersion:scheduleVersion withScheduleData:data ofScheduleType:scheduleType];
}

- (void)notifyScheduleUpdated {
    debugLog(@"");
    
    NeatoRobotCommand *robotCommand = [[NeatoRobotCommand alloc] init];
    robotCommand.robotId = self.robotId;
    robotCommand.profileDict = [[NSMutableDictionary alloc] initWithCapacity:1];
    [robotCommand.profileDict setValue:@"true" forKey:KEY_ROBOT_SCHEDULE_UPDATED];
    NeatoServerHelper *serverHelper = [[NeatoServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper notifyScheduleUpdatedForProfileDetails:robotCommand forUserWithEmail:[NeatoUserHelper getLoggedInUserEmail]];
}

- (void)notifyScheduleUpdatedSucceededWithResult:(NSDictionary *)result {
    debugLog(@"");
    // Save timestamp returned from server in db.
    ProfileDetail *profileDetail = [[ProfileDetail alloc] init];
    profileDetail.key = KEY_ROBOT_SCHEDULE_UPDATED;
    profileDetail.timestamp = [result objectForKey:KEY_TIMESTAMP];
    [NeatoRobotHelper updateProfileDetail:profileDetail forRobotWithId:self.robotId];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)failedToNotifyScheduleUpdatedWithError:(NSError *)error {
    debugLog(@"");
    self.delegate = nil;
    self.retained_self = nil;
    
}

@end
