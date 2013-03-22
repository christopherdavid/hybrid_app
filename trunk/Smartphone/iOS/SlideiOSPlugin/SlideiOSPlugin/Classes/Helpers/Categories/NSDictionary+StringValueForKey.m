#import "NSDictionary+StringValueForKey.h"
#import "LogHelper.h"

@implementation NSDictionary (StringValueForKey)

- (NSString *)stringForKey:(NSString *)key {
    if([[self objectForKey:key] isKindOfClass:[NSNumber class]]) {
        NSNumber *number = [self objectForKey:key];
        return [number stringValue];
    }
    else if([[self objectForKey:key] isKindOfClass:[NSString class]]) {
        return [self objectForKey:key];
    }
    else {
        return nil;
    }
}

@end
