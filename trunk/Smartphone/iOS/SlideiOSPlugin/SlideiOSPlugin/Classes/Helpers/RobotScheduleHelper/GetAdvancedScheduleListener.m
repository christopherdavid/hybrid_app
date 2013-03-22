#import "GetAdvancedScheduleListener.h"
#import "ScheduleServerhelper.h"
#import "NeatoConstants.h"
#import "FileDownloadManager.h"
#import "ScheduleXMLHelper.h"
#import "SchedulerConstants.h"
#import "LogHelper.h"

@interface GetAdvancedScheduleListener()
@property(nonatomic, retain) GetAdvancedScheduleListener *retained_self;
@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) NSString *scheduleId;
@end

@implementation GetAdvancedScheduleListener

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize scheduleId = _scheduleId;
@synthesize robotId = _robotId;

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
            self.scheduleId = [schedule objectForKey:@"id"];
            [self getScheduleDataFileURLforScheduleId:self.scheduleId];
            return;
        }
    }
    NSMutableDictionary *jsonObject = [[NSMutableDictionary alloc] init];
    NSArray *schedules = [[NSArray alloc] init];
    [jsonObject setObject:@"" forKey:@"scheduleId"];
    [jsonObject setObject:schedules forKey:@"schedules"];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(getScheduleSuccess:)]) {
            [self.delegate performSelector:@selector(getScheduleSuccess:) withObject:jsonObject];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    // Delegate back the error.
    [self notifyRequestFailed:@selector(getScheduleError:) withError:error];
}

- (void)getScheduleDataFileURLforScheduleId:(NSString *)scheduleId {
    ScheduleServerHelper *serverhelper = [[ScheduleServerHelper alloc]init];
    serverhelper.delegate = self;
    [serverhelper getDataForScheduleWithId:scheduleId];
}

- (void)gotScheduleData:(id)scheduleData forScheduleId:(NSString *)scheduleId {
    NSString *xml_data_url = [scheduleData valueForKey:@"xml_data_url"];
    [self getXmlDataFileFromUrl:xml_data_url];   
}

- (void)getXmlDataFileFromUrl:(NSString *)xml_data_url {
    FileDownloadManager *downloadManager = [[FileDownloadManager alloc]init];
    [downloadManager downloadFileFromURL:xml_data_url getFromCache:NO delegate:self];
}

- (void)failedToGetScheduleDataForScheduleId:(NSString *)scheduleId withError:(NSError *)error {
    [self notifyRequestFailed:@selector(getScheduleError:) withError:error];
}

- (void)fileDownloadedForURL:(NSString *)url atPath:(NSURL *)filePath {
    debugLog(@"");
    NSString *path = [filePath path];
    debugLog(@"Path of schedule file downloaded from server is %@",path);
    NSDictionary *jsonObject = [ScheduleXMLHelper advanceScheduleGroupFromXMLFile:path forScheduleId:self.scheduleId];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(getScheduleSuccess:)]) {
            [self.delegate performSelector:@selector(getScheduleSuccess:) withObject:jsonObject];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)fileDownloadFailedForURL:(NSString *)url withError:(NSError *)error {
    debugLog(@"Failed to download schedule file from server.");
    [self notifyRequestFailed:@selector(getScheduleError:) withError:error];
}

- (void)notifyRequestFailed:(SEL)selector withError:(NSError *)error {
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
