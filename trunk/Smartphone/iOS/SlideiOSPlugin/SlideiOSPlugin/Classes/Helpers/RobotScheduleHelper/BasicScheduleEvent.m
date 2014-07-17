#import "BasicScheduleEvent.h"
#import "ScheduleUtils.h"
#import "NSDictionary+StringValueForKey.h"
#import "AppHelper.h"


@implementation BasicScheduleEvent

@synthesize startTime = _startTime;
@synthesize day = _day;
@synthesize scheduleEventId = _scheduleEventId;
@synthesize parameterStr = _parameterStr;
@synthesize cleaningMode = _cleaningMode;

- (id)initWithDictionary:(NSDictionary *)dictionary andEventId:(NSString *)eventId {
    if(self = [super init]) {
        if(!self.startTime) {
            self.startTime =[[ScheduleTimeObject alloc] initWithString:[dictionary objectForKey:KEY_START_TIME]];
        }
        if(!self.day) {
            self.day = [ScheduleUtils dayEnumValue:[[dictionary objectForKey:KEY_DAY] intValue]];
        }
        if(!self.scheduleEventId) {
            self.scheduleEventId = eventId;
        }
        if (!self.cleaningMode) {
            self.cleaningMode = [dictionary stringForKey:KEY_CLEANING_MODE];
        }
        self.parameterStr = [AppHelper jsonStringFromNSDictionary:[self toDictionary]];
    }
    return self;
}

- (id)initWithDictionary:(NSDictionary *)dictionary {
    return [self initWithDictionary:dictionary andEventId:[dictionary valueForKey:KEY_SCHEDULE_EVENT_ID]]; 
}

- (NSDictionary *)toDictionary {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    [dict setValue:[self.startTime toString] forKey:KEY_START_TIME];
    [dict setValue:self.scheduleEventId forKey:KEY_SCHEDULE_EVENT_ID];
    [dict setValue:self.cleaningMode forKey:KEY_CLEANING_MODE];
    [dict setValue:[NSNumber numberWithInteger:self.day] forKey:KEY_DAY];
    return dict;  
}

@end
