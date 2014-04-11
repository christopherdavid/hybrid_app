#import "AppBaseDelegate.h"
#import "UserManagerCallWrapper.h"
#import "NeatoUserHelper.h"
#import "LogHelper.h"
#import "PushNotificationHelper.h"
#import "XMPPConnectionHelper.h"
#import "NeatoDataStore.h"

// Frameworks
#import "Crittercism.h"

// Helper
#import "AppHelper.h"

#define DEVICE_TYPE_IPHONE      2

@interface AppBaseDelegate() <UserManagerProtocol, CrittercismDelegate>
@property(nonatomic, strong) UserManagerCallWrapper *userManager;
@property(nonatomic, strong)XMPPConnectionHelper *xmppHelper;
@end

@implementation AppBaseDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    debugLog(@"didFinishLaunchingWithOptions called");
    // Create core data stack from main thread
    [NeatoDataStore sharedNeatoDataStore];
    [self enableCrittercism];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(connectOverXMPPIfRequired) name:UIApplicationWillEnterForegroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(disconnectXMPPConnection) name:UIApplicationDidEnterBackgroundNotification object:nil];

  [self connectOverXMPPIfRequired];
  
    bool loggedIn = [self.userManager isUserLoggedIn];
    if (loggedIn) {
        // Let the device know we want to receive push notifications
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
    }
    else {
        [[UIApplication sharedApplication]  unregisterForRemoteNotifications];
    }
    if ([launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey]) {
        [self application:application didReceiveRemoteNotification:[launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey]];
    }
    return YES;
}

- (void)application:(UIApplication*)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken {
	debugLog(@"Registration Id is: %@", deviceToken);
    NSString *token = [[deviceToken description] stringByTrimmingCharactersInSet:      [NSCharacterSet characterSetWithCharactersInString:@"<>"]];
    token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
    debugLog(@"content---%@", token);
    
    self.userManager.delegate = self;
    debugLog(@"Registration Id  after parsing: %@", token);
    bool loggedIn = [self.userManager isUserLoggedIn];
    if (loggedIn) {
        NSString *email = [NeatoUserHelper getLoggedInUserEmail];
        debugLog(@"email in register push notification: %@", email);
        [self.userManager registerPushNotificationForEmail:email deviceType:DEVICE_TYPE_IPHONE deviceToken:token];
    } else {
        [self.userManager unregisterPushNotificationForDeviceToken:token];
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

- (UserManagerCallWrapper *)userManager {
  if (!_userManager) {
    _userManager = [[UserManagerCallWrapper alloc] init];
  }
  return _userManager;
}

- (XMPPConnectionHelper *)xmppHelper {
  if (!_xmppHelper) {
    _xmppHelper = [[XMPPConnectionHelper alloc] init];
  }
  return _xmppHelper;
}


- (void)connectOverXMPPIfRequired {
    debugLog(@"connectOverXMPPIfRequired called.");
    bool loggedIn = [self.userManager isUserLoggedIn];
    if (loggedIn) {
        NeatoUser *user = [NeatoUserHelper getNeatoUser];
        if (!user) {
            debugLog(@"Couldn't find the logged-in user. Will not connect over XMPP!");
            return;
        }
        if ([self.xmppHelper isConnected]) {
            debugLog(@"Already connected over XMPP!");
            return;
        }
        self.xmppHelper.delegate = self;
        [self.xmppHelper connectJID:user.chatId password:user.chatPassword host:XMPP_SERVER_ADDRESS];
        
    }
    else {
        debugLog(@"User not logged-in. Will not connect over XMPP!");
    }
}

- (void)disconnectXMPPConnection {
    debugLog(@"disconnectXMPPConnection called");
    self.xmppHelper.delegate = self;
    [self.xmppHelper disconnectFromRobot];
}

- (void)didConnectOverXMPP {
    debugLog(@"didConnectOverXMPP called");
}

- (void)xmppLoginfailedWithError:(NSError *)error; {
    debugLog(@"xmppLoginfailedWithError called. error = %@", error);
}

- (void)didDisConnectFromXMPP {
    debugLog(@"didDisConnectFromXMPP called");
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationDidEnterBackgroundNotification object:nil];
}

#pragma mark - Private helpers

// Initializes crittercism for this run. Username would be set if the app is configured
- (void)enableCrittercism {
  debugLog(@"Enabling Crittercism");
  [Crittercism enableWithAppID:[AppHelper crittercismAppId] andDelegate:self];
}

#pragma mark - CrittercismDelegate

- (void)crittercismDidCrashOnLastLoad {
  debugLog(@"App did crash on last launch.");
}

@end
