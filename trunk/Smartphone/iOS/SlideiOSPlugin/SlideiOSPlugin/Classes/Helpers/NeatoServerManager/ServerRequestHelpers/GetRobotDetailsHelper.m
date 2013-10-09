#import "GetRobotDetailsHelper.h"
#import "AppSettings.h"
#import "NeatoConstants.h"

#define GET_ROBOT_DETAILS_POST_STRING @"api_key=%@&serial_number=%@"

@interface GetRobotDetailsHelper()

@property (nonatomic , strong) NSString *robotId;

@end

@implementation GetRobotDetailsHelper

- (id)initWithRobotId:(NSString *)robotId {
    self = [super init];
    if (self) {
        self.robotId = robotId;
    }
    return self;
}

- (NSURLRequest *)request {
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[AppSettings appSettings] urlWithBasePathForMethod:NEATO_GET_ROBOT_DETAILS_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ROBOT_DETAILS_POST_STRING, API_KEY, self.robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    return request;
}


@end
