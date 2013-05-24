#import <Foundation/Foundation.h>

@interface GetUserPushNotificationsListener : NSObject
- (id)initWithDelegate:(id)delegate;
- (void)start;

@property(nonatomic, strong) NSString *email;
@end
