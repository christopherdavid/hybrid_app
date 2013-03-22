#import "GetScheduleEventsListener.h"
#import "LogHelper.h"
#import "ScheduleServerHelper.h"
#import "AppHelper.h"
#import "FileDownloadManager.h"
#import "Schedule.h"
#import "ScheduleXMLHelper.h"
#import "NeatoConstants.h"
#import "ScheduleUtils.h"
#import "ScheduleDBHelper.h"
#import "NSDictionary+StringValueForKey.h"

#define SCHEDULE_TYPE_KEY @"schedule_type"
#define SCHEDULE_ID_KEY @"id"

@interface GetScheduleEventsListener()
@property(nonatomic, retain) GetScheduleEventsListener *retainedSelf;
@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) NSMutableDictionary *robotScheduleData;

@end

@implementation GetScheduleEventsListener
@synthesize robotId = _robotId;
@synthesize scheduleType = _scheduleType;
@synthesize retainedSelf = _retainedSelf;
@synthesize robotScheduleData = _robotScheduleData;

- (id)initWithDelegate:(id) delegate {
    debugLog(@"");
    if ((self = [super init]))
    {
        self.delegate = delegate;
        self.retainedSelf = self;
    }
    return self;
}

- (void)start {
    debugLog(@"scheduleType = %@", self.scheduleType);
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper getSchedulesForRobotWithId:self.robotId];
}

- (void)gotSchedulesData:(id)scheduleData forRobotId:(NSString *)robotId {
    debugLog(@"");
    if (![scheduleData isKindOfClass:[NSArray class]]) {
        [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:robotId withObject:[AppHelper nserrorWithDescription:@"Failed to parse server response!" code:200]];
        self.delegate = nil;
        self.retainedSelf = nil;
        return;
    }
    NSArray *schedules = (NSArray *)scheduleData;
    
    // Find the first matching schedule
    // TODO: This is under assumption that the schedules are sent in descending order.
    NSDictionary *scheduleDictionary = nil;
    for (int i = [schedules count] - 1; i>=0; i--) {
        NSDictionary *data = [schedules objectAtIndex:i];
        id scheduleType = [data valueForKey:SCHEDULE_TYPE_KEY];
        if ([scheduleType isKindOfClass:[NSString class]] && [scheduleType caseInsensitiveCompare:self.scheduleType] == NSOrderedSame) {
            scheduleDictionary = data;
            break;
        }
    }
    
    if (!scheduleDictionary) {
        [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:robotId withObject:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"%@ schedule doesn't exist for robot with Id = %@", self.scheduleType, self.robotId] code:200]];
        self.delegate = nil;
        self.retainedSelf = nil;
        return;
    }
    
    self.robotScheduleData = [[NSMutableDictionary alloc] initWithDictionary:scheduleDictionary];
    
    // Fetch the data for schedule.
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper getDataForScheduleWithId:[self.robotScheduleData valueForKey:SCHEDULE_ID_KEY]];
    
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:robotId withObject:error];
    self.delegate = nil;
    self.retainedSelf = nil;
}

- (void)gotScheduleData:(id)scheduleData forScheduleId:(NSString *)scheduleId {
    debugLog(@"");
    if (![scheduleData isKindOfClass:[NSDictionary class]]) {
        [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:self.robotId withObject:[AppHelper nserrorWithDescription:@"Could not fetch detaills for schedule." code:200]];
        self.delegate = nil;
        self.retainedSelf = nil;
        return;
    }
    [self.robotScheduleData addEntriesFromDictionary:scheduleData];
    
    // Now we need to download xml data
    NSString *xmlUrl = [self.robotScheduleData valueForKey:NEATO_RESPONSE_XML_DATA_URL];
    // Download the xml
    FileDownloadManager *manager = [[FileDownloadManager alloc] init];
    [manager downloadFileFromURL:xmlUrl getFromCache:NO delegate:self];
}

- (void)failedToGetScheduleDataForScheduleId:(NSString *)scheduleId withError:(NSError *)error {
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:self.robotId withObject:error];
    self.delegate = nil;
    self.retainedSelf = nil;
}

-(void) fileDownloadedForURL:(NSString *) url atPath:(NSURL *)path
{
    debugLog(@"File download at path = %@", path);
    NSError *error = nil;
    // Read the file
    NSString *xmlString = [NSString stringWithContentsOfFile:[path path] encoding:NSUTF8StringEncoding error:&error];
    if (error)
    {
        debugLog(@"Could not parse the file at location = %@", path);
        [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:self.robotId withObject:[AppHelper nserrorWithDescription:[NSString stringWithFormat:@"Could not parse the file at location = %@", path] code:200]];
        self.delegate = nil;
        self.retainedSelf = nil;
        return;
    }
    
    debugLog(@"jsonString = %@", xmlString);
    Schedule *schedule = [ScheduleXMLHelper basicScheduleFromString:xmlString];
    if ([self.scheduleType caseInsensitiveCompare:NEATO_SCHEDULE_BASIC] == NSOrderedSame) {
        schedule = [ScheduleXMLHelper basicScheduleFromString:xmlString];
    }
    else {
        // TODO: parse advanced schedule
    }
    
    if (!schedule) {
        [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:self.robotId withObject:[AppHelper nserrorWithDescription:@"Failed to parse downloaded XML" code:200]];
        self.delegate = nil;
        self.retainedSelf = nil;
        return;
    }
    schedule.server_scheduleId = [self.robotScheduleData valueForKey:@"id"];
    schedule.scheduleType = self.scheduleType;
    schedule.xml_data_version = [self.robotScheduleData stringForKey:@"xml_data_version"];
    [ScheduleDBHelper saveSchedule:schedule ofType:self.scheduleType forRobotWithId:self.robotId];
    
    // Notify caller
    [self.delegate gotScheduleEventsForSchedule:schedule ofType:[ScheduleUtils getScheduleIntFromString:self.scheduleType] forRobotWithId:self.robotId];
    self.delegate = nil;
    self.retainedSelf = nil;
    
}


-(void) fileDownloadFailedForURL:(NSString *) url withError:(NSError *) error
{
    debugLog(@"");
    [self.delegate performSelector:@selector(failedToGetSchedulesForRobotId:withError:) withObject:self.robotId withObject:error];
    self.delegate = nil;
    self.retainedSelf = nil;
}

@end
