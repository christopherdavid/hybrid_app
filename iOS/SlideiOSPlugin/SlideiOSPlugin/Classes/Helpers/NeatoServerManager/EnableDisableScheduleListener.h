#import <Foundation/Foundation.h>

@interface EnableDisableScheduleListener : NSObject

@property(nonatomic, retain) NSString *email;
@property(nonatomic, retain) NSString *robotId;
@property NSInteger scheduleType;
@property BOOL enable;

- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
