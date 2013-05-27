#import <Foundation/Foundation.h>

@class Schedule;

@interface GetScheduleDataPluginResult : NSObject
@property(nonatomic, strong) NSString *scheduleId;
@property(nonatomic, readwrite) NSInteger scheduleType;
@property(nonatomic, strong) Schedule *schedule;
- (NSMutableDictionary *)toDictionary;
@end
