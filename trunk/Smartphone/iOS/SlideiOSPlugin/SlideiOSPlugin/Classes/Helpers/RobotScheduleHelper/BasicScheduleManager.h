#import <Foundation/Foundation.h>

@interface BasicScheduleManager : NSObject

@property (nonatomic, strong) NSString *scheduleId;
- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
