#import <Foundation/Foundation.h>
#import "NeatoUser.h"


@interface CreateUserListener2 : NSObject
@property (nonatomic, strong) NeatoUser *user;
- (id)initWithDelegate:(id)delegate;
- (void)start;
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion;

@end
