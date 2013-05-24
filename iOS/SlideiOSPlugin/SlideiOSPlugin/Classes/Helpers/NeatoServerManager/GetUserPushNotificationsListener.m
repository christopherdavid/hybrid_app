#import "GetUserPushNotificationsListener.h"
#import "LogHelper.h"
#import "NeatoServerHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoNotification.h"
#import "AppHelper.h"

@interface GetUserPushNotificationsListener()
@property(nonatomic, weak) id delegate;
@property(nonatomic, strong) GetUserPushNotificationsListener *retained_self;

@end
@implementation GetUserPushNotificationsListener

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize email = _email;

- (void)start {
    debugLog(@"");
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper notificationSettingsForUserWithEmail:self.email];
}

- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)userNotificationSettingsData:(NSDictionary *)notificationJson {
    debugLog(@"");
    [NeatoUserHelper setNotificationsFromNotificationsArray:[self arrayFromNotificationJson:notificationJson] forEmail:self.email];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(userNotificationSettingsData:)])
        {
            [self.delegate performSelector:@selector(userNotificationSettingsData:) withObject:notificationJson];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)failedToGetUserPushNotificationSettingsWithError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(failedToGetUserPushNotificationSettingsWithError:)])
        {
            [self.delegate performSelector:@selector(failedToGetUserPushNotificationSettingsWithError:) withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (NSArray *)arrayFromNotificationJson:(NSDictionary *)notificationJson {
    debugLog(@"");
    NSArray *notificationJsonArray = [notificationJson objectForKey:KEY_NOTIFICATIONS];
    NSMutableArray *notificationsArray = [[NSMutableArray alloc] init];
    NeatoNotification *globalNotification = [[NeatoNotification alloc] init];
    globalNotification.notificationId = NOTIFICATION_ID_GLOBAL;
    globalNotification.notificationValue = [AppHelper stringFromBool:[[notificationJson valueForKey:NOTIFICATION_ID_GLOBAL] boolValue]];
    [notificationsArray addObject:globalNotification];
    for (int i = 0 ; i < TOTAL_NOTIFICATION_OPTIONS - 1 ; i++) {
        NeatoNotification *notification = [[NeatoNotification alloc] initWithDictionary:[notificationJsonArray objectAtIndex:i]];
        [notificationsArray addObject:notification];
    }
    return notificationsArray;
}
@end
