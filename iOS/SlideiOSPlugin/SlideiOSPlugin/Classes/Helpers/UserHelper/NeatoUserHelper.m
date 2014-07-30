#import "NeatoUserHelper.h"
#import "LogHelper.h"
#import "NeatoDataStore.h"
#import "NeatoUser.h"
#import "AppHelper.h"

#define KEY_USER_LOOGED_IN @"neato_has_user_logged_in"
#define KEY_USER_AUTH_TOKEN @"neato_user_current_auth_token"
#define KEY_USER_ID @"neato_user_id"
#define KEY_USER_NAME @"neato_user_name"
#define KEY_EMAIL @"neato_user_email"
#define KEY_USER_CHAT_ID @"neato_user_chat_id"
#define KEY_USER_CHAT_PASSWORD @"neato_user_chat_password"
#define KEY_ACCOUNT_TYPE @"neato_user_account_type"
#define KEY_USER_PASSWORD @"neato_user_password"
#define KEY_EXTERNAL_SOCIAL_ID @"neato_user_external_social_id"
#define KEY_DEVICE_PUSH_AUTH_TOKEN  @"device_push_auth_token"
#define KEY_UNIQUE_DEVICE_ID_FOR_USER @"unique_device_id_for_user"

@implementation NeatoUserHelper


+ (void)saveUserAuthToken:(NSString *)authToken
{
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setValue:authToken forKey:KEY_USER_AUTH_TOKEN];
    [userDefaults synchronize];

}

+ (NSString *)getUsersAuthToken
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return [userDefaults valueForKey:KEY_USER_AUTH_TOKEN];
}

+ (void)saveDevicePushAuthToken:(NSString *)authToken {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setValue:authToken forKey:KEY_DEVICE_PUSH_AUTH_TOKEN];
    [userDefaults synchronize];
}

+ (NSString *)getDevicePushAuthToken {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return [userDefaults valueForKey:KEY_DEVICE_PUSH_AUTH_TOKEN];
}

// TODO: should work on BG thread
+ (void)clearUserData {
    
     NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setBool:NO forKey:KEY_USER_LOOGED_IN];
    [userDefaults synchronize];
    [[NeatoDataStore sharedNeatoDataStore] deleteUserDetails];
    [NeatoUserHelper resetUserDefaults];
}

// TODO: should work on BG thread
+ (NeatoUser *)getNeatoUser {
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    if(![userDefaults boolForKey:KEY_USER_LOOGED_IN])
    {
        return nil;
    }
    
    // Get details from DB
    return [[NeatoDataStore sharedNeatoDataStore] getNeatoUser];
}

+ (void)saveNeatoUser:(NeatoUser *)neatoUser {
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setBool:YES forKey:KEY_USER_LOOGED_IN];
    [userDefaults synchronize];
    
    // Save details to DB
   [[NeatoDataStore sharedNeatoDataStore] saveNeatoUser:neatoUser];
}

+ (NSString *)getLoggedInUserEmail {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    if(![userDefaults boolForKey:KEY_USER_LOOGED_IN])
    {
        return nil;
    }
    NeatoUser *user = [[NeatoDataStore sharedNeatoDataStore] getNeatoUser];
    return user.email;
}

+ (void)dissociateAllRobotsForUserWithEmail:(NSString *)email {
    [[NeatoDataStore sharedNeatoDataStore] dissociateAllRobotsForUserWithEmail:email];
}

+ (void)deleteRobotWithRobotId:(NSString *)robotId forUser:(NSString *)userId {
    [[NeatoDataStore sharedNeatoDataStore] deleteRobotForSerialNumber:robotId forUserId:userId];
}

+ (void)updatePassword:(NSString *)newPassword {
    [[NeatoDataStore sharedNeatoDataStore] updatePassword:newPassword];
}

+ (void)insertOrUpdateNotificaton:(NeatoNotification *)notification forEmail:(NSString *)email {
    [[NeatoDataStore sharedNeatoDataStore] insertOrUpdateNotificaton:notification forEmail:email];
}

+ (BOOL)notificationsExistForUserWithEmail:(NSString *)email {
    return [[NeatoDataStore sharedNeatoDataStore] notificationsExistForUserWithEmail:email];
}

+ (void)setNotificationsFromNotificationsArray:(NSArray *)notificationOptionsArray forEmail:(NSString *)email {
    [[NeatoDataStore sharedNeatoDataStore] setNotificationsFromNotificationsArray:notificationOptionsArray forEmail:email];
}

+ (NSArray *)notificationsForUserWithEmail:(NSString *)email {
   return [[NeatoDataStore sharedNeatoDataStore] notificationsForUserWithEmail:email];
}

+ (NSString *)uniqueDeviceIdForUser {
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *uniqueId = [userDefaults valueForKey:KEY_UNIQUE_DEVICE_ID_FOR_USER];
    if (!uniqueId) {
        uniqueId = [AppHelper generateUniqueString];
        [userDefaults setValue:uniqueId forKey:KEY_UNIQUE_DEVICE_ID_FOR_USER];
        [userDefaults synchronize];
    }
    
    return uniqueId;
}

+ (void)deleteUniqueDeviceIdForUser {
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults removeObjectForKey:KEY_UNIQUE_DEVICE_ID_FOR_USER];
    [userDefaults synchronize];
}

+ (void)resetUserDefaults {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSDictionary *dict = [userDefaults dictionaryRepresentation];
    for (id key in [dict allKeys]) {
        [userDefaults removeObjectForKey:key];
    }
    [userDefaults synchronize];
}

+ (BOOL)isUserLoggedIn {
    debugLog(@"");
    // Logic for considering user as logged-in(For internal purposes),
    // Check if user email, authtoken and validation status are VALID.
    NeatoUser *user = [NeatoUserHelper getNeatoUser];
    NSString *authToken = [NeatoUserHelper getUsersAuthToken];
    return (user.email.length > 0
        && ([[user userValidationStatus] integerValue] == VALIDATION_STATUS_VALIDATED)
            && authToken.length > 0);
}

@end
