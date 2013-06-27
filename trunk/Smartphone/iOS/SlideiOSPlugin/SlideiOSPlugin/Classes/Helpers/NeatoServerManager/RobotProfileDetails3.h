#import <Foundation/Foundation.h>

// TODO : This class is poorly designed. We should move common values to helper
// functions.
@interface RobotProfileDetails3 : NSObject
@property (nonatomic, strong) NSString *robotId;
@property (nonatomic, strong) NSString *email;
@property (nonatomic, strong) NSString *causeAgentId;
@property (nonatomic, strong) NSNumber *notificationFlag;
// Key for which we are setting robot profile details on server
// and its value.
@property (nonatomic, strong) NSDictionary *profileDict;
// All key - value pairs to be sent in http header field.
@property (nonatomic, strong) NSDictionary *httpHeaderFields;

@end
