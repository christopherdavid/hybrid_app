#import <Foundation/Foundation.h>

@interface AppSettings : NSObject

+ (AppSettings *)appSettings;
- (NSURL *)urlWithBasePathForMethod:(NSString *)method;
@end
