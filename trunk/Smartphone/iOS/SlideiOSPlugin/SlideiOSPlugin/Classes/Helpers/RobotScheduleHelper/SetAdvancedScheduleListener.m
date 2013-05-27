#import "SetAdvancedScheduleListener.h"
#import "NeatoConstants.h"
#import "LogHelper.h"
#import "ScheduleServerhelper.h"
#import "ScheduleXMLHelper.h"
#import "PostScheduleResult.h"

@interface SetAdvancedScheduleListener()

@property(nonatomic, retain) SetAdvancedScheduleListener *retained_self;
@property(nonatomic, weak) id delegate;

@end
@implementation SetAdvancedScheduleListener
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize robotId = _robotId;
@synthesize scheduleGroup = _scheduleGroup;

- (id)initWithDelegate:(id)delegate {
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)start {
    ScheduleServerHelper *serverhelper = [[ScheduleServerHelper alloc]init];
    serverhelper.delegate = self;
    [serverhelper getSchedulesForRobotWithId:self.robotId];
}

- (void)gotSchedulesData:(id)scheduleData forRobotId:(NSString *)robotId {
    NSArray *scheduleGroup = (NSArray *)scheduleData;
    for(int i=0; i<[scheduleGroup count]; i++) {
        NSDictionary *schedule = [scheduleGroup objectAtIndex:i];
        NSString *scheduleType = [schedule objectForKey:@"schedule_type"];
        if([NEATO_SCHEDULE_ADVANCE isEqualToString:scheduleType]) {
            NSString *scheduleId = [schedule objectForKey:@"id"];
            NSString *xml_data_version = [schedule objectForKey:@"xml_data_version"];
            [self updateScheduleDataForRobotScheduleId:scheduleId forXMLDataVersion:xml_data_version andScheduleGroup:self.scheduleGroup];
            return;
        }
    }
    [self postScheduleForRobotId:self.robotId andScheduleGroup:self.scheduleGroup];
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    [self notifyRequestFailed:@selector(setScheduleError:) withError:error];
}

- (void)updateScheduleDataForRobotScheduleId:(NSString *)scheduleId forXMLDataVersion:(NSString *)xml_data_version andScheduleGroup:(NSArray *)scheduleGroup {
    debugLog(@"");
    NSString *schedule_xml = [ScheduleXMLHelper xmlFromScheduleGroup:scheduleGroup];
    ScheduleServerHelper *scheduleHelper = [[ScheduleServerHelper alloc] init];
    scheduleHelper.delegate = self;
    [scheduleHelper updateScheduleDataForScheduleId:scheduleId withScheduleVersion:xml_data_version withScheduleData:schedule_xml ofScheduleType:NEATO_SCHEDULE_ADVANCE];
}

- (void)postScheduleForRobotId:(NSString *)robotId andScheduleGroup:(NSArray *)scheduleGroup {
    debugLog(@"");
    ScheduleServerHelper *scheduleHelper = [[ScheduleServerHelper alloc] init];
    scheduleHelper.delegate = self;
    NSString *schedule_xml = [ScheduleXMLHelper xmlFromScheduleGroup:scheduleGroup];
    [scheduleHelper postScheduleForRobotId:robotId withScheduleData:schedule_xml ofScheduleType:NEATO_SCHEDULE_ADVANCE];
}

- (void)postedSchedule:(PostScheduleResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(setScheduleSuccess:)]) {
            [self.delegate performSelector:@selector(setScheduleSuccess:) withObject:result.serverScheduleId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)postScheduleError:(NSError *)error {
    [self notifyRequestFailed:@selector(setScheduleError:) withError:error];
}

- (void)updatedSchedule:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(setScheduleSuccess:)])
        {
            [self.delegate performSelector:@selector(setScheduleSuccess:) withObject:message];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)updatedScheduleError:(NSError *)error {
    [self notifyRequestFailed:@selector(setScheduleError:) withError:error];
}

- (void) notifyRequestFailed:(SEL)selector withError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector]) {
            [self.delegate performSelector:selector withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

@end
