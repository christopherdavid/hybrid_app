#import <Foundation/Foundation.h>
#import "ScheduleTimeObject.h"
#import "SchedulerConstants.h"

@interface BasicScheduleEvent : NSObject
@property(nonatomic, strong) ScheduleTimeObject *startTime;
@property(nonatomic, readwrite) Day day;
@property(nonatomic, strong) NSString *scheduleEventId;
@property(nonatomic, strong) NSString *cleaningMode;
// Parameter string property is a JSON string of all the parameters.
@property(nonatomic, strong) NSString *parameterStr;

- (id)initWithDictionary:(NSDictionary *)dictionary andEventId:(NSString *)eventId;
- (id)initWithDictionary:(NSDictionary *)dictionary;
- (NSDictionary *)toDictionary;
@end

