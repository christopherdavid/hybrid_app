#import <Foundation/Foundation.h>

@interface NeatoRobotCommand : NSObject

@property (nonatomic, strong) NSString *xmlCommand;
@property (nonatomic, strong) NSString *robotId;
@property (nonatomic, strong) NSString *commandId;
@property (nonatomic, strong) NSString *causingAgentId;

@end