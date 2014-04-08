#import <Foundation/Foundation.h>
#import "NeatoUser.h"

@interface LoginListener2 : NSObject
@property (nonatomic, strong) NSString *email;
@property (nonatomic, strong) NSString *password;

- (id)initWithDelegate:(id) delegate;
- (void)start;
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion;

@end
