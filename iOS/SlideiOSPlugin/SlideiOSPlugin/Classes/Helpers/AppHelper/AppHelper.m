#import "AppHelper.h"
#import "LogHelper.h"

@implementation AppHelper

+(NSDictionary *) parseJSON :(NSData *) jsonData;
{
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

// Gets the int value of apps signature
+(unsigned) getAppSignature
{
    unsigned result = 0;
    NSScanner *scanner = [NSScanner scannerWithString:@"0xcafebabe"];
    
    [scanner scanHexInt:&result];
    return result;
}


+(BOOL) isArchitectureLittleEndian
{
    int i = 1;
    int firstBitVal = *((unsigned char*)&i);
    return (firstBitVal == i);
}

// Converts a int value to its big endian equivalent
+(int) swapIntoBigEndian:(int)littleInt
{
    int32_t unswapped = littleInt;
    int32_t swapped = CFSwapInt32HostToBig(unswapped);
    return swapped;
}

+(NSString *) jsonStringFromNSDictionary:(id) data
{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    
    if (!jsonData) {
        debugLog(@"Count not parse data to create JSON object. Something won't work as expected!!");
        return nil;
    } else {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return jsonString;
    }
}

+(NSString *) jsonStringFromArrayofNSDictionaries:(NSArray *) arrayOfDictionaries;
{
    return [self jsonStringFromNSDictionary:arrayOfDictionaries];
}

@end
