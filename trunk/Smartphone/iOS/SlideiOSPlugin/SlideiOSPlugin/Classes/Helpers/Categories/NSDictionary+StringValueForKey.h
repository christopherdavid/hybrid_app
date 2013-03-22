#import <Foundation/Foundation.h>

@interface NSDictionary (StringValueForKey)

// This method checks if object for 'key' is of type number or string and returns a 
// string value for that object or nil.
- (NSString *)stringForKey:(NSString *)key;
@end
