#import <Foundation/Foundation.h>


@protocol PushNotificationDelegate <NSObject>
@required

- (void)didReceivePushNotification:(NSString *)callbackId withNotification:(NSDictionary*)notification;

@end

@interface PushNotificationHelper : NSObject
@property (weak, nonatomic)id <PushNotificationDelegate> pushNotificationDelegate;

+ (PushNotificationHelper *)sharedInstance;
- (void)registerForPushNotificationsForCallbackId:(NSString *)callbackId;
- (void)unregisterForPushNotification;
- (void)setPushNotification:(NSDictionary *)pushNotificationData;

@end
