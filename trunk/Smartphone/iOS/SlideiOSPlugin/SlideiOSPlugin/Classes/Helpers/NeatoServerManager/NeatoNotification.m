#import "NeatoNotification.h"
#import "LogHelper.h"
#import "AppHelper.h"

@implementation NeatoNotification
@synthesize notificationId = _notificationId;
@synthesize notificationValue = notificationValue;

- (id)initWithDictionary:(NSDictionary *)dictionary {
    if ((self = [super init])) {
        self.notificationId = [dictionary valueForKey:KEY_NOTIFICATION_KEY];
        self.notificationValue = [AppHelper stringFromBool:[[dictionary valueForKey:KEY_NOTIFICATION_VALUE] boolValue]];
        debugLog(@"notificationValue : %@",self.notificationValue);
    }
    return self;
}

- (NSDictionary *)toDictionary {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    [dict setValue:self.notificationId forKey:KEY_NOTIFICATION_KEY];
    [dict setValue:self.notificationValue forKey:KEY_NOTIFICATION_VALUE];
    return dict;
}
@end
