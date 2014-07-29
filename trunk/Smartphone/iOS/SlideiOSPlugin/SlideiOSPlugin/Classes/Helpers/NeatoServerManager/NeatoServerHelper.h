#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NSURLConnectionHelper.h"

@class NeatoRobotCommand;
@class NeatoUserAttributes;

@protocol NeatoServerHelperProtocol <NSObject>

@optional
//-(void) requestFailed:(NSError *) error;
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken;
- (void)pushNotificationUnregistrationSuccess;
- (void)commandSentWithResult:(NSDictionary *)result;
- (void)setUserAttributesSucceeded;
- (void)notifyScheduleUpdatedSucceededWithResult:(NSDictionary *)result;

// Failure cases
- (void)pushNotificationRegistrationFailedWithError:(NSError *)error;
- (void)pushNotificationUnregistrationFailedWithError:(NSError *)error;
- (void)failedtoSendCommandWithError:(NSError *)error;
- (void)failedToSetUserAttributesWithError:(NSError *)error;
- (void)failedToNotifyScheduleUpdatedWithError:(NSError *)error;

@end

@interface NeatoServerHelper : NSObject

@property(nonatomic, weak) id delegate;
- (void)loginFacebookUser:(NSString *)externalSocialId;
- (void)updateUserAuthToken:(NSString *)authToken;
- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken notificationServerType:(NSString *)serverType applicationId:(NSString *)applicationId;
- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken;
- (void)sendCommand:(NeatoRobotCommand *)command withSourceEmailId:(NSString *)email;
- (void)setUserAttributes:(NeatoUserAttributes *)attributes forAuthToken:(NSString *)authToken;
- (void)notifyScheduleUpdatedForProfileDetails:(NeatoRobotCommand *)profileDetails forUserWithEmail:(NSString *)email;
- (void)dataForRequest:(NSURLRequest *)request completionBlock:(ServerHelperCompletionBlock)completionBlock;
@end
