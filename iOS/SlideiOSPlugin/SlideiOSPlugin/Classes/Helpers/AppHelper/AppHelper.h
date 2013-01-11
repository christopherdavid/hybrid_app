#import <Foundation/Foundation.h>

@interface AppHelper : NSObject

+(NSDictionary *) parseJSON :(NSData *) jsonData;
+(BOOL) isArchitectureLittleEndian;
+(int) swapIntoBigEndian:(int) littleInt;
+(unsigned) getAppSignature;
+(NSString *) jsonStringFromNSDictionary:(id) data;
+(NSString *) jsonStringFromArrayofNSDictionaries:(NSArray *) arrayOfDictionaries;

@end
