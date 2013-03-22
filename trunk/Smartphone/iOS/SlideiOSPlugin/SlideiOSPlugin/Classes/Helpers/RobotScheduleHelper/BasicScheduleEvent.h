#import <Foundation/Foundation.h>
#import "ScheduleTimeObject.h"
#import "SchedulerConstants.h"

@interface BasicScheduleEvent : NSObject
@property(nonatomic, strong) ScheduleTimeObject *startTime;
@property(nonatomic, readwrite) Day day;
@property(nonatomic, strong) NSString *scheduleEventId;
@property(nonatomic, strong) NSString *xmlData;

- (id)initWithDictionary:(NSDictionary *)dictionary andEventId:(NSString *)eventId;
@end
