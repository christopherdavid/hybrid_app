#import "PushNotificationHelper.h"
#import "NeatoConstants.h"
#import "LogHelper.h"
#import "PluginConstants.h"

static PushNotificationHelper *sharedInstance = nil;

@interface PushNotificationHelper()
@property(strong, nonatomic) NSString *callbackId;
@property (strong, atomic) NSDictionary *pushNotificationData;
- (NSDictionary *)robotNotificationFromAPN:(NSDictionary *)notificationDictionary;
- (void)notifyDelegateWithNotification:(NSDictionary *)pushNotificationData;

@end

@implementation PushNotificationHelper

@synthesize callbackId = _callbackId;
@synthesize pushNotificationDelegate = _pushNotificationDelegate;
@synthesize pushNotificationData = _pushNotificationData;

+ (PushNotificationHelper *)sharedInstance {
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[PushNotificationHelper alloc] init];
    });
    return sharedInstance;
}

- (void)registerForPushNotificationsForCallbackId:(NSString *)callbackId {
    self.callbackId = callbackId;
    if (self.pushNotificationData) {
        [self notifyDelegateWithNotification:self.pushNotificationData];
    }
}

- (void)unregisterForPushNotification {
    self.pushNotificationDelegate = nil;
    self.callbackId = nil;
}

- (void)receivePushNotification:(NSNotification *) notification {
    debugLog(@"");
    if (!self.pushNotificationDelegate) {
        debugLog(@"pushNotificationDelegate is nil");
        return;
    }
    [self notifyDelegateWithNotification:notification.userInfo];
}



- (NSDictionary *)robotNotificationFromAPN:(NSDictionary *)notificationDictionary {
    NSMutableDictionary *pushNotificationJson = [[NSMutableDictionary alloc] init];
    NSString *robotId = [notificationDictionary objectForKey:KEY_ROBOT_ID];
    NSString *notificationId = [notificationDictionary objectForKey:KEY_ID];
    NSString *message = [notificationDictionary objectForKey:KEY_MESSAGE];
    
    if (robotId) {
        [pushNotificationJson setValue:robotId forKey:KEY_ROBOT_ID];
    }
    
    if (notificationId) {
        [pushNotificationJson setValue:notificationId forKey:KEY_NOTIFICATIONID];
    }
    
    if (message) {
        [pushNotificationJson setValue:message forKey:KEY_MESSAGE];
    }
    
    return pushNotificationJson;
    
}

- (void)setPushNotification:(NSDictionary *)pushNotificationData {
    self.pushNotificationData = pushNotificationData;
    if (self.pushNotificationDelegate) {
        [self notifyDelegateWithNotification:pushNotificationData];
    }
    else {
        debugLog(@"setPushNotification delegate is nil");
    }
}

- (void)notifyDelegateWithNotification:(NSDictionary *)pushNotificationData {
    debugLog(@"");
    NSDictionary *pushNotificationJson = [self robotNotificationFromAPN:pushNotificationData];
    [self.pushNotificationDelegate didReceivePushNotification:self.callbackId withNotification:pushNotificationJson];
    self.pushNotificationData = nil;
}
@end
