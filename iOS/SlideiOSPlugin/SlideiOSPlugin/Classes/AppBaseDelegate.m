#import "AppBaseDelegate.h"
#import "UserManagerCallWrapper.h"
#import "NeatoUserHelper.h"
#import "LogHelper.h"
#import "PushNotificationHelper.h"

#define DEVICE_TYPE_IPHONE      2

@interface AppBaseDelegate() <UserManagerProtocol>

@end

@implementation AppBaseDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    debugLog(@"didFinishLaunchingWithOptions called");
    bool loggedIn = [[[UserManagerCallWrapper alloc] init] isUserLoggedIn];
    if (loggedIn) {
        // Let the device know we want to receive push notifications
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
    }
    else {
        [[UIApplication sharedApplication]  unregisterForRemoteNotifications];
    }
    return YES;
}

- (void)application:(UIApplication*)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken {
	debugLog(@"Registration Id is: %@", deviceToken);
    NSString *token = [[deviceToken description] stringByTrimmingCharactersInSet:      [NSCharacterSet characterSetWithCharactersInString:@"<>"]];
    token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
    debugLog(@"content---%@", token);
    
    UserManagerCallWrapper *callWrapper = [[UserManagerCallWrapper alloc] init];
    callWrapper.delegate = self;
    debugLog(@"Registration Id  after parsing: %@", token);
    bool loggedIn = [[[UserManagerCallWrapper alloc] init] isUserLoggedIn];
    if (loggedIn) {
        NSString *email = [NeatoUserHelper getLoggedInUserEmail];
        debugLog(@"email in register push notification: %@", email);
        [callWrapper registerPushNotificationForEmail:email deviceType:DEVICE_TYPE_IPHONE deviceToken:token];
    } else {
        [callWrapper unregisterPushNotificationForDeviceToken:token];
    }
}

- (void)application:(UIApplication*)application didFailToRegisterForRemoteNotificationsWithError:(NSError*)error {
	debugLog(@"Failed to get token, error: %@", error);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    debugLog(@"");
    NSDictionary *customDataDictionary = [userInfo objectForKey:PUSH_NOTIFICATION_CUSTOM_DATA_KEY];
    [self performSelector:@selector(receivePushNotification:) withObject:customDataDictionary afterDelay:0.5];
}

- (void)pushNotificationRegistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationRegistrationFailedWithError called");
    [NeatoUserHelper saveDevicePushAuthToken:@""];
}

- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken {
    debugLog(@"pushNotificationRegisteredForDeviceToken called");
    [NeatoUserHelper saveDevicePushAuthToken:deviceToken];
}

- (void)pushNotificationUnregistrationSuccess {
    debugLog(@"pushNotificationUnregistrationSuccess called");
    [NeatoUserHelper saveDevicePushAuthToken:@""];
}

- (void)pushNotificationUnregistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationUnregistrationFailedWithError called");
    [NeatoUserHelper saveDevicePushAuthToken:@""];
}

- (void) receivePushNotification:(NSDictionary *) userInfo {
    debugLog(@"receivePushNotification called");
    [[PushNotificationHelper sharedInstance] setPushNotification:userInfo];
}


@end
