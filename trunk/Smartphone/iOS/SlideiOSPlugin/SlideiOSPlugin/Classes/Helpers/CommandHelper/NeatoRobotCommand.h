#import <Foundation/Foundation.h>

@interface NeatoRobotCommand : NSObject

@property (nonatomic, strong) NSString *xmlCommand;
@property (nonatomic, strong) NSString *robotId;
@property (nonatomic, strong) NSString *commandId;
// Key for which we are setting robot profile details on server
// and its value.
@property (nonatomic, strong) NSDictionary *profileDict;

@end