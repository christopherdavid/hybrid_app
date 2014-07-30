#import <Foundation/Foundation.h>
#import "NeatoServerManager.h"
#import <Cordova/CDV.h>
#import "NeatoNotification.h"

@protocol UserManagerProtocol <NSObject>

@optional
//-(void) requestFailed:(NSError *) error callbackId:(NSString *)callbackId;
- (void)gotUserAssociatedRobots:(NSMutableArray *)robots callbackId:(NSString *)callbackId;
- (void)pushNotificationRegistrationFailedWithError:(NSError *) error;
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken;
- (void)pushNotificationUnregistrationSuccess;
- (void)pushNotificationUnregistrationFailedWithError:(NSError *) error;
- (void)validatedUserWithResult:(NSDictionary *)resultData callbackId:(NSString *)callbackId;
- (void)userValidationFailedWithError:(NSError *)error callbackId:(NSString *)callbackId;
@end

@interface UserManagerCallWrapper : CDVPlugin 

@property(nonatomic, weak) id<UserManagerProtocol> delegate;
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken;
@end
