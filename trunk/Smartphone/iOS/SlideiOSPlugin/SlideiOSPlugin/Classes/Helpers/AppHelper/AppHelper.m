#import "AppHelper.h"
#import "LogHelper.h"
#import <CommonCrypto/CommonDigest.h>
#import "Build.h"
#import <UIKit/UIDevice.h>

@implementation AppHelper

+ (NSDictionary *)parseJSON:(NSData *)jsonData {
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

+ (NSString *)jsonStringFromObject:(id)object {
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:object
                                                       options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
                                                         error:&error];
    
    if (! jsonData) {
        debugLog(@"Got an error: %@", error);
        return nil;
    } else {
      return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
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
    if (!input || [input length] == 0)
    {
        return YES;
    }
    return NO;
}

+ (NSString *)getCurrentServer {
    #ifdef NEATO_ROBOT_SERVER_PROD
        return NEATO_PROD_SERVER;
    #elif SWITCH_TO_DEV_SERVER
        return NEATO_DEV_SERVER;
    #else
        return NEATO_STAGING_SERVER;
    #endif
}

+ (void)traceAppInfo {
    debugLog(@"==================App info=====================");
    debugLog(@"Plugin version = %@", SLIDE_IOS_PLUGIN_VERSION);
    debugLog(@"Server = %@", [self getCurrentServer]);
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
    [appInfo setValue:[self getCurrentServer] forKey:NEATO_KEY_SERVER_USED];
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
    if ([string isEqualToString:STRING_TRUE]) {
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
@end
