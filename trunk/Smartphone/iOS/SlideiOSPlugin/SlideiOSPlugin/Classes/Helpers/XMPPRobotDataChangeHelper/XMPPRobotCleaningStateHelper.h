#import <Foundation/Foundation.h>

// This class contains utitlity methods to determine robot state.

@interface XMPPRobotCleaningStateHelper : NSObject

+ (NSInteger)robotCurrentStateFromRobotProfile:(NSDictionary *)robotProfile;
+ (NSInteger)robotActualStateFromRobotProfile:(NSDictionary *)robotProfile;
+ (NSInteger)robotVirtualStateFromRobotProfile:(NSDictionary *)robotProfile;
@end
