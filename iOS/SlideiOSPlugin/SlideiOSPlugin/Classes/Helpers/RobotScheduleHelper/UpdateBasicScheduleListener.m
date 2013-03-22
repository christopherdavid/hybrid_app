#import "UpdateBasicScheduleListener.h"
#import "Schedule.h"
#import "ScheduleDBHelper.h"
#import "ScheduleXMLHelper.h"
#import "ScheduleServerhelper.h"
#import "AppHelper.h"
#import "NeatoConstants.h"
#import "PostScheduleResult.h"
#import "LogHelper.h"
#import "NSDictionary+StringValueForKey.h"

@interface UpdateBasicScheduleListener()

@property(nonatomic, retain) UpdateBasicScheduleListener *retained_self;
@property(nonatomic, retain) NSString *robotId;
@property(nonatomic, retain) Schedule *schedule;
@property(nonatomic, weak) id delegate;

@end

@implementation UpdateBasicScheduleListener

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize scheduleId = _scheduleId;
@synthesize robotId = _robotId;
@synthesize schedule = _schedule;

- (id)initWithDelegate:(id)delegate {
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)start {
    id dbResult = [ScheduleDBHelper getBasicScheduleForScheduleId:self.scheduleId];
    if([dbResult isKindOfClass:[NSError class]]) {
        [self updateScheduleError:[AppHelper nserrorWithDescription:@"No basic schedule for this scheduleId in database." code:200]];
        return;
    }
    self.schedule = (Schedule *)dbResult;
    self.robotId = [ScheduleDBHelper getRobotIdForScheduleId:self.scheduleId];
    NSString *xmlData = [ScheduleXMLHelper getXmlDataFromSchedule:self.schedule];
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    if([AppHelper isStringNilOrEmpty:self.schedule.server_scheduleId]) {
        [serverHelper postScheduleForRobotId:self.robotId withScheduleData:xmlData ofScheduleType:NEATO_SCHEDULE_BASIC];
    }
    else {
        [serverHelper updateScheduleDataForScheduleId:self.schedule.server_scheduleId withXMLDataVersion:self.schedule.xml_data_version withScheduleData:xmlData ofScheduleType:NEATO_SCHEDULE_BASIC];
    }
}

- (void)postedSchedule:(PostScheduleResult *)result {
    debugLog(@"");
    // Save server_scheduleId and xml_Data_version in DB.
    [self saveServerScheduleId:result.server_scheduleId andXmlDataVersion:result.xmlDataVersion forScheduleWithScheduleId:self.scheduleId];
    [self.delegate performSelector:@selector(updatedSchedule:) withObject:self.scheduleId];
    self.delegate = nil;
    self.retained_self = nil;   
}

- (void)postScheduleError:(NSError *)error {
    [self.delegate performSelector:@selector(updateScheduleError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;   
}

- (void)saveServerScheduleId:(NSString *)server_scheduleId andXmlDataVersion:(NSString *)xml_data_version forScheduleWithScheduleId:(NSString *)scheduleId {
    [ScheduleDBHelper updateScheduleWithScheduleId:scheduleId withServerScheduleId:server_scheduleId andXmlDataVersion:xml_data_version];
}

- (void)updatedSchedule:(NSString *)message {
    [self updateXmlDataVersion];
}

- (void)updateXmlDataVersion {
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper getSchedulesForRobotWithId:self.robotId];
}

- (void)gotSchedulesData:(id)scheduleData forRobotId:(NSString *)robotId {
    NSArray *scheduleGroup = (NSArray *)scheduleData;
    for(int i=0; i<[scheduleGroup count]; i++) {
        NSDictionary *serverSchedule = [scheduleGroup objectAtIndex:i];
        if([self.schedule.server_scheduleId isEqualToString:[serverSchedule stringForKey:@"id"]]) {
            [self saveXmlDataVersion:[serverSchedule stringForKey:@"xml_data_version"] forScheduleWithScheduleId:self.scheduleId];
            return;
        }
    }
    NSError *err = [AppHelper nserrorWithDescription:@"Error in update schedule" code:200];
    [self updateScheduleError:err];
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    // Delegate back the error.
    [self.delegate performSelector:@selector(updateScheduleError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)saveXmlDataVersion:(NSString *)xml_data_version forScheduleWithScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    // Update xml_data_version in db.
    [ScheduleDBHelper updateScheduleWithScheduleId:scheduleId forXmlDataVersion:xml_data_version];
    [self.delegate performSelector:@selector(updatedSchedule:) withObject:self.scheduleId];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)updateScheduleError:(NSError *)error {
    [self.delegate performSelector:@selector(updateScheduleError:) withObject:error];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
