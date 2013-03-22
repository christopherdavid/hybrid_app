#import <Foundation/Foundation.h>
#import "ScheduleServerhelper.h"

@interface SetAdvancedScheduleListener : NSObject <ScheduleServerHelperProtocol>
@property(nonatomic, strong) NSString *robotId;
@property(nonatomic, strong) NSArray *scheduleGroup;

- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
