#import <Foundation/Foundation.h>

@interface AppHelper : NSObject

+ (NSDictionary *)parseJSON:(NSData *)jsonData;
+ (BOOL)isArchitectureLittleEndian;
+ (int)swapIntoBigEndian:(int)littleInt;
+ (unsigned)getAppSignature;
+ (NSString *)jsonStringFromNSDictionary:(id)data;
+ (NSString *)jsonStringFromArrayofNSDictionaries:(NSArray *)arrayOfDictionaries;
+ (NSString *)sha1:(NSString *)decodedString;
+ (NSURL *)getLocalCachePath;
+ (NSString *)getUniqueIdFromString:(NSString *)baseString;
+ (bool)isStringNilOrEmpty:(NSString *)input;
+ (NSString *)generateUniqueString;
+ (void)traceAppInfo;
+ (NSString *)currentServer;
+ (NSString *)getMainAppVersion;
+ (NSDictionary *)getAppDebugInfo;
+ (NSTimeInterval)currentTimeStamp;
+ (NSError *)nserrorWithDescription:(NSString *)description code:(NSInteger)code;
+ (NSString *)getEmptyStringIfNil:(NSString *)input;
+ (BOOL)boolValueFromString:(NSString *)string;
+ (NSString *)stringFromBool:(BOOL)boolValue;
+ (NSString *)deviceSystemName;
+ (NSString *)deviceSystemVersion;
+ (NSString *)deviceModelName;
+ (BOOL)hasServerRequestFailedForResponse:(NSDictionary *)serverResponse;
+ (NSString *)applicationId;
+ (NSString *)notificationServerType;
+ (NSString *)appInfo;
+ (NSString *)crittercismAppId;
+ (void)saveLastUsedRobotId:(NSString *)robotId;
+ (NSString *)lastUsedRobotId;
+ (NSArray *)removeInternalKeysFromRobotProfileKeys:(NSArray *)profileKeys;
+ (void)saveDirectConnectionScretKey:(NSString *)secretKey;
+ (NSString *)directConnectionScretKey;
/*
 Clear all USER DEFAULTS data.
 */
+ (void)clearAppDefaultsData;
+ (BOOL)isValidJSONString:(id)value;
@end
