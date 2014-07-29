#import <Foundation/Foundation.h>
#import "NeatoUser.h"


@interface CreateUserManager : NSObject
@property (nonatomic, strong) NeatoUser *user;
- (id)init;
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion;

@end
