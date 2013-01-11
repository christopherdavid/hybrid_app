#import "NeatoUserHelper.h"
#import "LogHelper.h"


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

@implementation NeatoUserHelper


+(void) saveUserAuthToken:(NSString *)authToken
{
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setValue:authToken forKey:KEY_USER_AUTH_TOKEN];
    [userDefaults synchronize];

}

+(NSString *) getUsersAuthToken
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return [userDefaults valueForKey:KEY_USER_AUTH_TOKEN];
}

// TODO: should work on BG thread
+(void) clearUserData
{
    
     NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setBool:NO forKey:KEY_USER_LOOGED_IN];
    [userDefaults synchronize];
    
    [[NeatoDBHelper sharedNeatoDBHelper] deleteUserDetails];
}

// TODO: should work on BG thread
+(NeatoUser *) getNeatoUser
{
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    if(![userDefaults boolForKey:KEY_USER_LOOGED_IN])
    {
        return nil;
    }
    
    /*NeatoUser *user = [[NeatoUser alloc] init];
    user.userId = [userDefaults valueForKey:KEY_USER_ID];
    user.name = [userDefaults valueForKey:KEY_USER_NAME];
    user.email = [userDefaults valueForKey:KEY_EMAIL];
    user.chatId = [userDefaults valueForKey:KEY_USER_CHAT_ID];
    user.chatPassword = [userDefaults valueForKey:KEY_USER_CHAT_PASSWORD];
    user.account_type = [userDefaults valueForKey:KEY_ACCOUNT_TYPE];
    user.password = [userDefaults valueForKey:KEY_USER_PASSWORD];
    user.external_social_id = [userDefaults valueForKey:KEY_EXTERNAL_SOCIAL_ID];*/
    
    
    // Get details from DB
    return [[NeatoDBHelper sharedNeatoDBHelper] getNeatoUser];
    //return user;
}

+(void) saveNeatoUser:(NeatoUser *) neatoUser
{
    debugLog(@"");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setBool:YES forKey:KEY_USER_LOOGED_IN];
    /*[userDefaults setValue:neatoUser.userId forKey:KEY_USER_ID];
    [userDefaults setValue:neatoUser.name forKey:KEY_USER_NAME];
    [userDefaults setValue:neatoUser.email forKey:KEY_EMAIL];
    [userDefaults setValue:neatoUser.chatId forKey:KEY_USER_CHAT_ID];
    [userDefaults setValue:neatoUser.chatPassword forKey:KEY_USER_CHAT_PASSWORD];
    [userDefaults setValue:neatoUser.account_type forKey:KEY_ACCOUNT_TYPE];
    [userDefaults setValue:neatoUser.password forKey:KEY_USER_PASSWORD];
    [userDefaults setValue:neatoUser.external_social_id forKey:KEY_EXTERNAL_SOCIAL_ID];*/
    [userDefaults synchronize];
    
    // Save details to DB
    [[NeatoDBHelper sharedNeatoDBHelper] saveNeatoUser:neatoUser];
    
}

+(NSString *) getLoggedInUserEmail
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    if(![userDefaults boolForKey:KEY_USER_LOOGED_IN])
    {
        return nil;
    }
    NeatoUser *user = [[NeatoDBHelper sharedNeatoDBHelper] getNeatoUser];
    return user.email;
}

@end
