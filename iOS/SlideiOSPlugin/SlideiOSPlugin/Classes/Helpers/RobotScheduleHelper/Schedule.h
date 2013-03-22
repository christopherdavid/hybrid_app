#import <Foundation/Foundation.h>
#import "ScheduleEvent.h"

@interface Schedule : NSObject
@property(nonatomic, retain) NSString *scheduleId;
@property(nonatomic, retain) NSString *server_scheduleId;
@property(nonatomic, retain) NSString *scheduleType;
@property(nonatomic, retain) NSString *xml_data_version;
@property(nonatomic, retain) ScheduleEvent *scheduleEvent;

- (NSArray *)arrayOfScheduleEventIdsForType:(NSInteger)scheduleType;
@end
