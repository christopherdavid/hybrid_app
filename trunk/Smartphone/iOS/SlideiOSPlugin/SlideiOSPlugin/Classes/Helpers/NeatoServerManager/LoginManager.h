#import <Foundation/Foundation.h>
#import "NeatoUser.h"

@interface LoginManager : NSObject
@property (nonatomic, strong) NSString *email;
@property (nonatomic, strong) NSString *password;

- (id)init;
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion;
@end
