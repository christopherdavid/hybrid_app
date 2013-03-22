#import <Foundation/Foundation.h>

@interface UpdateBasicScheduleListener : NSObject

@property(nonatomic, strong) NSString *scheduleId;
- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
