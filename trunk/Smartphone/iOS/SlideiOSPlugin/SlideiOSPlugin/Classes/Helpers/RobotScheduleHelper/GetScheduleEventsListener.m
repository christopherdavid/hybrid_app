#import "GetScheduleEventsListener.h"
#import "LogHelper.h"
#import "ScheduleServerHelper.h"
#import "Schedule.h"
#import "ScheduleEvent.h"
#import "BasicScheduleEvent.h"
#import "SchedulerConstants.h"
#import "AppHelper.h"
#import "ScheduleDBHelper.h"
#import "ScheduleUtils.h"
#import "ScheduleJsonHelper.h"

@interface GetScheduleEventsListener()
@property(nonatomic, strong) GetScheduleEventsListener *retainedSelf;
@property(nonatomic, weak) id delegate;

@end

@implementation GetScheduleEventsListener
@synthesize robotId = _robotId;
@synthesize scheduleType = _scheduleType;
@synthesize retainedSelf = _retainedSelf;

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
    if ([self.scheduleType isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
        NSError *error = [AppHelper nserrorWithDescription:@"Advance schedule type is not supported" code:ERROR_NOT_SUPPORTED];
        [self.delegate performSelector:@selector(failedToGetScheduleEventsWithError:) withObject:error];
        self.delegate = nil;
        self.retainedSelf = nil;
        return;
    }
    ScheduleServerHelper *serverHelper = [[ScheduleServerHelper alloc] init];
    serverHelper.delegate = self;
    [serverHelper scheduleBasedOnType:[NSString stringWithFormat:@"%d", [ScheduleUtils serverScheduleIntFromString:self.scheduleType]] forRobotId:self.robotId];
}

- (void)gotScheduleWithData:(id)data {
    debugLog(@"");
    if (![data isKindOfClass:[NSArray class]]) {
        if ([self.delegate respondsToSelector:@selector(failedToGetScheduleEventsWithError:)]) {
            [self.delegate performSelector:@selector(failedToGetScheduleEventsWithError:) withObject:[AppHelper nserrorWithDescription:@"Failed to parse server response!" code:ERROR_SERVER_ERROR]];
            self.delegate = nil;
            self.retainedSelf = nil;     
        }
        return;
    }
    NSArray *resultArray = (NSArray *)data;
    NSDictionary *serverSchedule = [resultArray objectAtIndex:0];
    Schedule *schedule = [ScheduleJsonHelper scheduleFromDictionary:serverSchedule];
    // Save schedule in DB.
    [ScheduleDBHelper saveSchedule:schedule ofType:self.scheduleType forRobotWithId:self.robotId];
    // Notify caller
    if ([self.delegate respondsToSelector:@selector(gotScheduleEventsForSchedule:ofType:forRobotWithId:)]) {
        [self.delegate gotScheduleEventsForSchedule:schedule ofType:[ScheduleUtils scheduleIntFromString:self.scheduleType] forRobotWithId:self.robotId];
        self.delegate = nil;
        self.retainedSelf = nil;
    }
}

- (void)failedToGetScheduleWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToGetScheduleEventsWithError:)]) {
        [self.delegate performSelector:@selector(failedToGetScheduleEventsWithError:) withObject:error];
        self.delegate = nil;
        self.retainedSelf = nil;     
    }
}

@end

