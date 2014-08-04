#import "AppHelper.h"
#import "LogHelper.h"
#import <CommonCrypto/CommonDigest.h>
#import "Build.h"
#import <UIKit/UIDevice.h>
#import "NeatoNotification.h"

// We need to identify the UID app and RSL test application
// because we need to tell server which certificate to use for the push notification

@implementation AppHelper

+ (NSDictionary *)parseJSON:(NSData *)jsonData {
    if (!jsonData) {
        return nil;
    }
    NSError* error = nil;
    NSDictionary *data = [NSJSONSerialization
                              JSONObjectWithData:jsonData
                              options:kNilOptions
                              error:&error];
    if (error != nil)
    {
        return nil;
    }
    return data;
}

+ (NSString *)sha1:(NSString *)decodedString {
    NSData *data = [decodedString dataUsingEncoding:NSUTF8StringEncoding];
    uint8_t digest[CC_SHA1_DIGEST_LENGTH];
    
    CC_SHA1(data.bytes, data.length, digest);
    
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    
    for (int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++)
    {
        [output appendFormat:@"%02x", digest[i]];
    }
    
    return output;
}

// Gets the int value of apps signature
+ (unsigned)getAppSignature {
    unsigned result = 0;
    NSScanner *scanner = [NSScanner scannerWithString:@"0xcafebabe"];
    
    [scanner scanHexInt:&result];
    return result;
}


+ (BOOL)isArchitectureLittleEndian {
    int i = 1;
    int firstBitVal = *((unsigned char*)&i);
    return (firstBitVal == i);
}

// Converts a int value to its big endian equivalent
+ (int)swapIntoBigEndian:(int)littleInt {
    int32_t unswapped = littleInt;
    int32_t swapped = CFSwapInt32HostToBig(unswapped);
    return swapped;
}

+ (NSString *)jsonStringFromNSDictionary:(id)data {
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data
                                                       options:0
                                                         error:&error];
    
    if (!jsonData) {
        debugLog(@"Count not parse data to create JSON object. Something won't work as expected!!");
        return nil;
    } else {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return jsonString;
    }
}

+ (NSString *)jsonStringFromArrayofNSDictionaries:(NSArray *)arrayOfDictionaries {
    return [self jsonStringFromNSDictionary:arrayOfDictionaries];
}

+ (NSURL *)getLocalCachePath {
    NSArray *directoriesArr;
    #ifdef ENABLE_DB_CREATION_IN_DOCUMENTS_DIR
        directoriesArr = [[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask];
    #else
        directoriesArr = [[NSFileManager defaultManager] URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask];
    #endif
    NSURL *baseDIR = [directoriesArr objectAtIndex:0];
    return baseDIR;
}

// Creates a unique Id for a string using SHA1 function
// Code picked up from http://stackoverflow.com/questions/7570377/creating-sha1-hash-from-nsstring
+ (NSString *)getUniqueIdFromString:(NSString *)baseString {
    NSData *data= [baseString dataUsingEncoding:NSUTF8StringEncoding];
    uint8_t digest[CC_SHA1_DIGEST_LENGTH];
    CC_SHA1(data.bytes, data.length, digest);
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++)
    {
        [output appendFormat:@"%02x", digest[i]];
    }
    return output;
}

+ (NSString *)generateUniqueString {
    CFUUIDRef uuidRef = CFUUIDCreate(NULL);
    CFStringRef uuidStringRef = CFUUIDCreateString(NULL, uuidRef);
    CFRelease(uuidRef);
    return (__bridge NSString *)uuidStringRef;
}

// Retruns true if a string is nil or empty
+ (bool)isStringNilOrEmpty:(NSString *)input {
    if (![input isKindOfClass:[NSString class]]) {
        return YES;
    }
    if (!input || [input length] == 0)
    {
        return YES;
    }
    return NO;
}

+ (NSString *)currentServer {
  return SERVER_TYPE;
}

+ (void)traceAppInfo {
    debugLog(@"==================App info=====================");
    debugLog(@"Plugin version = %@", SLIDE_IOS_PLUGIN_VERSION);
    debugLog(@"Server = %@", [self currentServer]);
    debugLog(@"====================End========================");
}

+ (NSString *)getMainAppVersion {
     NSString *strBuildVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:(NSString *)kCFBundleVersionKey];
    return strBuildVersion;
}

+ (NSDictionary *)getAppDebugInfo {
    debugLog(@"");
    NSMutableDictionary *appInfo = [[NSMutableDictionary alloc] init];
    [appInfo setValue:[self getMainAppVersion] forKey:NEATO_KEY_APP_VERSION];
    [appInfo setValue:[self currentServer] forKey:NEATO_KEY_SERVER_USED];
    [appInfo setValue:SLIDE_IOS_PLUGIN_VERSION forKey:NEATO_KEY_LIB_VERSION];
    return appInfo;
}

+ (NSTimeInterval)currentTimeStamp {
    NSTimeInterval timeStamp = [[NSDate date] timeIntervalSince1970];
    return timeStamp;
}

+ (NSError *)nserrorWithDescription:(NSString *)description code:(NSInteger)code {
    NSMutableDictionary* details = [NSMutableDictionary dictionary];
    [details setValue:description forKey:NSLocalizedDescriptionKey];
    
    NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:code] integerValue] userInfo:details];
    return error;
}

+ (NSString *)getEmptyStringIfNil:(NSString *)input {
    if(input) {
        return input;
    }
    return @"";
}

+ (BOOL)boolValueFromString:(NSString *)string {
    if (string && [string isKindOfClass:[NSString class]] && [string isEqualToString:STRING_TRUE]) {
        return YES;
    }
    else {
        return NO;
    }
}

+ (NSString *)stringFromBool:(BOOL)boolValue {
    if (boolValue) {
        return STRING_TRUE;
    }
    else {
        return STRING_FALSE;
    }
}

+ (NSString *)deviceSystemName {
    return [[UIDevice currentDevice] systemName];
}

+ (NSString *)deviceSystemVersion {
    return [[UIDevice currentDevice] systemVersion];
}

+ (NSString *)deviceModelName {
    return [[UIDevice currentDevice] model];
}

// Returns YES if the server has responded with an error code of -1.
// NO otherwise.
+ (BOOL)hasServerRequestFailedForResponse:(NSDictionary *)serverResponse {
    NSNumber *status = [NSNumber numberWithInt:[[serverResponse valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    NSDictionary *serverError = [serverResponse objectForKey:KEY_NEATO_SERVER_ERROR];
    return (([status intValue] == NEATO_STATUS_ERROR) && serverError);
}

+ (NSString *)applicationId {
  NSString *bundleIdentifier = [[NSBundle mainBundle] bundleIdentifier];
  debugLog(@"Bundle identifier = [%@]", bundleIdentifier);
  return bundleIdentifier;
}

+ (NSString *)notificationServerType {
   debugLog(@"NOTIFICATION_SERVER_TYPE = [%@]", NOTIFICATION_SERVER_TYPE);
  return NOTIFICATION_SERVER_TYPE;
}


+ (NSString *)appInfo {
    NSDictionary *appInfo = @{ @"locale" : [[NSLocale currentLocale] localeIdentifier],
                               @"device_name" : [AppHelper deviceModelName],
                               @"os_name" : [AppHelper deviceSystemName],
                               @"os_version" : [AppHelper deviceSystemVersion],
                               @"current_time_zone" :  [AppHelper rawTimeZoneOffset],
                               @"application_version" : [self getMainAppVersion]
                              };
    return [AppHelper jsonStringFromNSDictionary:appInfo];
}

+ (NSString *)rawTimeZoneOffset {
    NSInteger seconds = ([[NSTimeZone localTimeZone] secondsFromGMT] / (60 * 60));
    return [[NSNumber numberWithInteger:seconds] stringValue];
}

+ (NSString *)crittercismAppId {
#ifdef DEBUG
  return CRITTERCISM_DEBUG_APP_ID;
#else
  return CRITTERCISM_RELEASE_APP_ID;
#endif
}

+ (void)saveLastUsedRobotId:(NSString *)robotId {
    if (robotId) {
        [[NSUserDefaults standardUserDefaults] setObject:robotId forKey:KEY_LAST_USED_ROBOT_ID];
    }
}

+ (NSString *)lastUsedRobotId {
    return [[NSUserDefaults standardUserDefaults] objectForKey:KEY_LAST_USED_ROBOT_ID];
}

+ (NSArray *)removeInternalKeysFromRobotProfileKeys:(NSArray *)profileKeys {
    NSArray *keysToRemove = @[KEY_ROBOT_CLEANING_COMMAND,
                              KEY_INTEND_TO_DRIVE,
                              KEY_AVAILABLE_TO_DRIVE,
                              KEY_ROBOT_ONLINE_STATUS_DATA,
                              KEY_NAME,
                              KEY_SERIAL_NUMBER,
                              KEY_ROBOT_SCHEDULE_UPDATED];
    
    NSMutableArray *filteredKeys = [profileKeys mutableCopy];
    for (NSString *key in keysToRemove) {
        if ([filteredKeys containsObject:key]) {
            [filteredKeys removeObject:key];
        }
    }
    return filteredKeys;
}

+ (void)saveDirectConnectionScretKey:(NSString *)secretKey {
    if (secretKey) {
        [[NSUserDefaults standardUserDefaults] setObject:secretKey forKey:KEY_ROBOT_DIRECT_CONNECT_SCRET];
    }
}

+ (NSString *)directConnectionScretKey {
    return [[NSUserDefaults standardUserDefaults] objectForKey:KEY_ROBOT_DIRECT_CONNECT_SCRET];
}

+ (BOOL)isValidJSONString:(id)value {
  // Check if it is string and is parsable.
  if ([value isKindOfClass:[NSString class]]) {
    NSString *jsonString = (NSString *)value;
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    if (!jsonData) {
      return NO;
    }
    NSError* error = nil;
    [NSJSONSerialization JSONObjectWithData:jsonData
                                    options:kNilOptions
                                      error:&error];
    return error ? NO : YES;
  }
  else {
    return NO;
  }
}

+ (NSString *)jsonStringFromNotificationsArray:(NSArray *)notificationsArray {
    NSMutableDictionary *notificationData = [[NSMutableDictionary alloc] init];
    NSMutableArray *notifications = [[NSMutableArray alloc] init];
    for (int i = 0 ; i < [notificationsArray count] ; i++) {
        NeatoNotification *neatoNotifications = [notificationsArray objectAtIndex:i];
        if ([neatoNotifications.notificationId  isEqualToString:NOTIFICATION_ID_GLOBAL]) {
            [notificationData setValue:neatoNotifications.notificationValue forKey:neatoNotifications.notificationId];
        }
        else {
            [notifications addObject:[neatoNotifications toDictionary]];
        }
    }
    [notificationData setValue:notifications forKey:KEY_NOTIFICATIONS];
    NSString *notificationJson = [AppHelper jsonStringFromNSDictionary:notificationData];
    debugLog(@"NotificationJson %@", notificationJson);
    return notificationJson;
}

+ (BOOL)shouldSendCommandDirectlyViaXMPP:(NSString *)commandId {
    debugLog(@"");
    // Check if following commands are valid to be sent directly via XMPP.
    // As these commands should be sent via XMPP, if command category is not manual.
    BOOL isCommandValid = NO;
    switch ([commandId integerValue]) {
        case COMMAND_START_ROBOT:
        case COMMAND_STOP_ROBOT:
        case COMMAND_PAUSE_CLEANING:
        case COMMAND_RESUME_CLEANING:
        case COMMAND_SEND_TO_BASE:
            isCommandValid = YES;
            break;
        default:
            isCommandValid = NO;
            break;
    }
    return (isCommandValid && SHOULD_SEND_COMMAND_DIRECTLY_VIA_XMPP);
}

@end
