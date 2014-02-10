#import "SetUserPushNotificationOptionsListener.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoServerHelper.h"
#import "AppHelper.h"

@interface SetUserPushNotificationOptionsListener()
@property(nonatomic, weak) id delegate;
@property(nonatomic, strong) SetUserPushNotificationOptionsListener *retained_self;

@end
@implementation SetUserPushNotificationOptionsListener

@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize notification = _notification;
@synthesize email = _email;

- (void)start {
    debugLog(@"");
    // When first time user tries to update default values are set
    // and then database is updated with user value.
    if (![NeatoUserHelper notificationsExistForUserWithEmail:self.email]) {
        // No notification exists in database. Set a default one.
        [self setDefaultNotificationOptions];
    }
    [NeatoUserHelper insertOrUpdateNotificaton:self.notification forEmail:self.email];
    // Get all notificationOptions from database and form JSON.
    NSArray *notificationsArray = [NeatoUserHelper notificationsForUserWithEmail:self.email];
    NSString *notificationJson = [self jsonStringFromNotificationsArray:notificationsArray];
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper setUserPushNotificationOptions:notificationJson forUserWithEmail:self.email];
}

- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)setDefaultNotificationOptions {
    NSMutableArray *notificationsArray = [[NSMutableArray alloc] init];
    for (int i = 0 ; i < TOTAL_NOTIFICATION_OPTIONS ; i++ ) {
        NeatoNotification *notification = [[NeatoNotification alloc] init];
        switch (i) {
            case 0:
                notification.notificationId = NOTIFICATION_ID_GLOBAL;
                break;
            case 1:
                notification.notificationId = NOTIFICATION_ID_ROBOT_STUCK;
                break;
            case 2:
                notification.notificationId = NOTIFICATION_ID_NEEDS_CLEAN;
                break;
            case 3:
                notification.notificationId = NOTIFICATON_ID_CLEANING_DONE;
                break;
            case 4:
                notification.notificationId = NOTIFICATION_ID_PLUG_CABLE;
                break;
            case 5:
                notification.notificationId = NOTIFICATION_ID_DUSTBIN_MISSING;
                break;
            case 6:
                notification.notificationId = NOTIFICATION_ID_ROBOT_CANCEL;
                
            default:
                break;
        }
        notification.notificationValue = STRING_FALSE;
        [notificationsArray addObject:notification];
    }
    [NeatoUserHelper setNotificationsFromNotificationsArray:notificationsArray forEmail:self.email];
}

- (NSString *)jsonStringFromNotificationsArray:(NSArray *)notificationsArray {
    NSMutableDictionary *notificationData = [[NSMutableDictionary alloc] init];
    NSMutableArray *notifications = [[NSMutableArray alloc] init];
    for (int i = 0 ; i < [notificationsArray count] ; i++) {
        NeatoNotification *neatoNotifications = [notificationsArray objectAtIndex:i];
        if ([neatoNotifications.notificationId  isEqualToString:NOTIFICATION_ID_GLOBAL]) {
            [notificationData setValue:neatoNotifications.notificationValue forKey:neatoNotifications.notificationId];
        }
        else {
            [notifications addObject:[neatoNotifications toDictionary]];
        }
    }
    [notificationData setValue:notifications forKey:KEY_NOTIFICATIONS];
    NSString *notificationJson = [AppHelper jsonStringFromNSDictionary:notificationData];
    debugLog(@"NotificationJson %@", notificationJson);
    return notificationJson;
}

- (void)setUserPushNotificationOptionsSuccess {
    debugLog(@"");
    NSMutableDictionary *notificationJson = [[NSMutableDictionary alloc] init];
    [notificationJson setValue:self.notification.notificationId forKey:KEY_NOTIFICATION_KEY];
    [notificationJson setValue:[NSNumber numberWithBool:[AppHelper boolValueFromString:self.notification.notificationValue]] forKey:KEY_NOTIFICATION_VALUE];
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(notificationsTurnedOnOffWithResult:)])
        {
            [self.delegate performSelector:@selector(notificationsTurnedOnOffWithResult:) withObject:notificationJson];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
    
}

- (void)failedToSetUserPushNotificationOptionsWithError:(NSError *)error {
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(failedToSetUserPushNotificationOptionsWithError:)])
        {
            [self.delegate performSelector:@selector(failedToSetUserPushNotificationOptionsWithError:) withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

@end
