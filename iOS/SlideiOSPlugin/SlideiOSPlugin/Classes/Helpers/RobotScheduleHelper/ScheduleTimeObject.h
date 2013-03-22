#import <Foundation/Foundation.h>

@interface ScheduleTimeObject : NSObject
@property(nonatomic, strong) NSString *hrs;
@property(nonatomic, strong) NSString *mins;
- (id)initWithString:(NSString *)time;
- (id)initWithHrs:(NSString *)hrs andMins:(NSString *)mins;
- (NSString *)toString;
@end
