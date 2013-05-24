#import <Foundation/Foundation.h>

@interface NeatoNotification : NSObject
@property(nonatomic, strong) NSString *notificationId;
@property(nonatomic, strong) NSString *notificationValue;
- initWithDictionary:(NSDictionary *)dictionary;
- (NSDictionary *)toDictionary;
@end
