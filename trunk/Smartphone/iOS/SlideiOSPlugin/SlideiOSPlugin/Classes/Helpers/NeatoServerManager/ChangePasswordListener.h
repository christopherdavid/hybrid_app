#import <Foundation/Foundation.h>

@interface ChangePasswordListener : NSObject
@property (nonatomic, strong) NSString *changedPassword;
- (id)initWithDelegate:(id)delegate;
@end
