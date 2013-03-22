#import <Foundation/Foundation.h>
#import "ScheduleServerhelper.h"

@interface GetAdvancedScheduleListener : NSObject <ScheduleServerHelperProtocol>
@property(nonatomic, strong) NSString *robotId;
- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
