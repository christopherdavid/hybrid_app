#import <Foundation/Foundation.h>

@interface SetRobotNameListener : NSObject

@property(nonatomic, retain) NSString *robotName;
@property(nonatomic, retain) NSString *robotId;

- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
