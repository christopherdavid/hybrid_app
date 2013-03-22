#import "DeleteAdvancedScheduleListener.h"
#import "NeatoConstants.h"
#import "LogHelper.h"
#import "AppHelper.h"

@interface DeleteAdvancedScheduleListener()
@property(nonatomic, retain) DeleteAdvancedScheduleListener *retained_self;
@property(nonatomic, weak) id delegate;
@end
@implementation DeleteAdvancedScheduleListener
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
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
            NSString *scheduleId = [schedule objectForKey:@"id"];
            [self deleteAdvancedScheduleforScheduleId:scheduleId];
            return;
        }
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(deleteScheduleError:)]) {
            [self.delegate performSelector:@selector(deleteScheduleError:) withObject:[AppHelper nserrorWithDescription:@"Failed to delete robot schedule data" code:200]];
	}
        self.delegate = nil;
        self.retained_self = nil;
    });
    
}

- (void)failedToGetSchedulesForRobotId:(NSString *)robotId withError:(NSError *)error {
    [self notifyRequestFailed:@selector(deleteScheduleError:) withError:error];
}

- (void)deleteAdvancedScheduleforScheduleId:(NSString *)scheduleId {
    ScheduleServerHelper *serverhelper = [[ScheduleServerHelper alloc]init];
    serverhelper.delegate = self;
    [serverhelper deleteScheduleDataForScheduleId:scheduleId];
}

- (void)deletedScheduleData:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(deleteScheduleSuccess:)]) {
            [self.delegate performSelector:@selector(deleteScheduleSuccess:) withObject:message];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)deleteScheduleDataError:(NSError *)error {
    [self notifyRequestFailed:@selector(deleteScheduleError:) withError:error];
}

- (void)notifyRequestFailed:(SEL) selector withError:(NSError *)error {
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
