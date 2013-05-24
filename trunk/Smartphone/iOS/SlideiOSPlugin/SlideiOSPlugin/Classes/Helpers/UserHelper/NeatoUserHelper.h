#import <Foundation/Foundation.h>

@class NeatoUser;
@class NeatoNotification;

@interface NeatoUserHelper : NSObject

+ (NeatoUser *)getNeatoUser;
+ (void)saveNeatoUser:(NeatoUser *)neatoUser;
+ (void)clearUserData;
+ (NSString *)getLoggedInUserEmail;
+ (NSString *)getUsersAuthToken;
+ (void)saveUserAuthToken:(NSString *)authToken;
+ (void)dissociateAllRobotsForUserWithEmail:(NSString *)email;
+ (void)deleteRobotWithRobotId:(NSString *)robotId forUser:(NSString *)userId;
+ (void)saveDevicePushAuthToken:(NSString *)authToken;
+ (NSString *)getDevicePushAuthToken;
+ (void)updatePassword:(NSString *)newPassword;
+ (void)insertOrUpdateNotificaton:(NeatoNotification *)notification forEmail:(NSString *)email;
+ (BOOL)notificationsExistForUserWithEmail:(NSString *)email;
+ (void)setNotificationsFromNotificationsArray:(NSArray *)notificationOptionsArray forEmail:(NSString *)email;
+ (NSArray *)notificationsForUserWithEmail:(NSString *)email;

@end
