#import "LinkRobotHelper.h"
#import "NeatoConstants.h"
#import "AppSettings.h"

#define LINK_ROBOT_POST_STRING @"api_key=%@&email=%@&linking_code=%@"

@interface LinkRobotHelper ()

@property (nonatomic, strong) NSString *userEmail;
@property (nonatomic, strong) NSString *linkCode;

@end

@implementation LinkRobotHelper

- (id)initWithEmail:(NSString *)email linkCode:(NSString *)linkCode {
    self = [super init];
    if (self) {
        self.userEmail = email;
        self.linkCode = linkCode;
    }
    return self;
}

- (NSURLRequest *)request {
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_LINK_ROBOT_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:LINK_ROBOT_POST_STRING, API_KEY, self.userEmail, self.linkCode] dataUsingEncoding:NSUTF8StringEncoding]];
    return request;
}

@end
