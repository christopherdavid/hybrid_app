#import <Foundation/Foundation.h>

@class BasicScheduleEvent;

@interface GetScheduleEventDataPluginResult : NSObject
@property(nonatomic, strong) NSString *scheduleId;
@property(nonatomic, strong) NSString *scheduleEventId;
@property(nonatomic, strong) BasicScheduleEvent *basicScheduleEvent;

- (NSMutableDictionary *)toDictionary;
@end
