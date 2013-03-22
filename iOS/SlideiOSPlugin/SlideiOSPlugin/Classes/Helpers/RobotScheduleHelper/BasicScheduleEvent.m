#import "BasicScheduleEvent.h"
#import "ScheduleUtils.h"


@implementation BasicScheduleEvent

@synthesize startTime = _startTime;
@synthesize day = _day;
@synthesize scheduleEventId = _scheduleEventId;
@synthesize xmlData = _xmlData;

- (id)initWithDictionary:(NSDictionary *)dictionary andEventId:(NSString *)eventId {
    if(self = [super init]) {
        if(!self.startTime) {
            self.startTime =[[ScheduleTimeObject alloc] initWithString:[dictionary objectForKey:KEY_START_TIME]];
        }
        if(!self.day) {
            self.day = [ScheduleUtils getDayEnumValue:[[dictionary objectForKey:KEY_DAY] integerValue]];
        }
        if(!self.scheduleEventId) {
            self.scheduleEventId = eventId;
        }
    }
    return self;
}
@end
