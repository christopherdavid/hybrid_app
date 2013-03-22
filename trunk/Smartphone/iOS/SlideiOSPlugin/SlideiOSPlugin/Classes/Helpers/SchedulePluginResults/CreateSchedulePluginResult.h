#import <Foundation/Foundation.h>

@interface CreateSchedulePluginResult : NSObject
@property(nonatomic, strong) NSString *robotId;
@property(nonatomic, strong) NSString *scheduleId;
@property(nonatomic, strong) NSString *scheduleType;

- (NSMutableDictionary *)toDictionary;
@end
