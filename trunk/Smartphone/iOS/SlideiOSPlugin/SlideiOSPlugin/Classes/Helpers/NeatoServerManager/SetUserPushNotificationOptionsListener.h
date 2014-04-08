#import <Foundation/Foundation.h>
#import "NeatoNotification.h"

@interface SetUserPushNotificationOptionsListener : NSObject

- (id)initWithDelegate:(id)delegate;
- (void)start;
- (void)startWithCompletion:(RequestCompletionBlockDictionary)completion;
@property(nonatomic, strong) NeatoNotification *notification;
@property(nonatomic, strong) NSString *email;

@end
