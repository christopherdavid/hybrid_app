#import "UserManagerCallWrapper.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotHelper.h"
#import "XMPPConnectionHelper.h"
#import "TCPConnectionHelper.h"

@interface UserManagerCallWrapper()
@property(nonatomic, retain) UserManagerCallWrapper *retained_self;
@property(nonatomic, retain) NSString *callbackId;

-(void) notifyCallback:(SEL) action;
-(void) notifyCallback:(SEL) action object:(id) object;
@end

@implementation UserManagerCallWrapper

- (void)notifyCallback:(SEL)action {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:action])
        {
            [self.delegate performSelector:action withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

- (void)notifyCallback:(SEL)action object:(id) object {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:action])
        {
            [self.delegate performSelector:action withObject:object withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

/*-(void) requestFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyCallback:@selector(requestFailed:callbackId:) object:error];
}*/

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken {
    debugLog(@"registerPushNotification called");
    self.retained_self = self;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager registerPushNotificationForEmail:email deviceType:deviceType deviceToken:deviceToken];
}

- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken {
    debugLog(@"unregisterPushNotificationForRegistrationId called");
    self.retained_self = self;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager unregisterPushNotificationForDeviceToken:deviceToken];
    
}

- (void)pushNotificationRegistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationRegistrationFailedWithError called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    [NeatoUserHelper saveDevicePushAuthToken:@""];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken {
    debugLog(@"pushNotificationRegisteredForDeviceToken called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    [NeatoUserHelper saveDevicePushAuthToken:deviceToken];
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)pushNotificationUnregistrationFailedWithError:(NSError *) error {
    debugLog(@"pushNotificationUnregistrationFailedWithError called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    self.delegate = nil;
    self.retained_self = nil;
}

- (void)pushNotificationUnregistrationSuccess {
    debugLog(@"pushNotificationUnregistrationSuccess called");
    // Not sending the notification to the outer layer because registratin/unregistration is internal call
    [NeatoUserHelper saveDevicePushAuthToken:@""];
    self.delegate = nil;
    self.retained_self = nil;
}

@end
