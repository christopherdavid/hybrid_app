#import <Foundation/Foundation.h>

@class Schedule;

@interface GetScheduleDataPluginResult : NSObject
@property(nonatomic, strong) NSString *scheduleId;
@property(nonatomic, strong) NSString *scheduleType;
@property(nonatomic, strong) Schedule *schedule;
- (NSMutableDictionary *)toDictionary;
@end
