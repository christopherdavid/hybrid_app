#import "AppSettings.h"
#import "NeatoConstants.h"

static AppSettings *sharedInstance = nil;

@implementation AppSettings

+ (AppSettings *)appSettings {
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[AppSettings alloc] init];
    });
    return sharedInstance;
}

- (NSURL *)urlWithBasePathForMethod:(NSString *)method {
    NSString *urlString = [[NEATO_BASE_URL stringByAppendingString:@"?"] stringByAppendingString:method];
    NSURL *url = [NSURL URLWithString:urlString];
    return url;
}
@end
