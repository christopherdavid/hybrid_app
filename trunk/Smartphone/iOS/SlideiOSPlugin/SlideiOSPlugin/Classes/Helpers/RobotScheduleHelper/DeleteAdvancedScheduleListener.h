#import <Foundation/Foundation.h>
#import "ScheduleServerhelper.h"

@interface DeleteAdvancedScheduleListener : NSObject
@property(nonatomic, strong) NSString *robotId;
- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
