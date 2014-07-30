#import <Foundation/Foundation.h>
#import "NeatoUser.h"


@interface CreateUserManager : NSObject
@property (nonatomic, strong) NeatoUser *user;
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion;
@end
