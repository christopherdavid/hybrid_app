#import <Foundation/Foundation.h>

@interface CreateScheduleEventPluginResult : NSObject
@property(nonatomic, strong) NSString *scheduleId;
@property(nonatomic, strong) NSString *scheduleEventId;

- (NSMutableDictionary *)toDictionary;
@end
