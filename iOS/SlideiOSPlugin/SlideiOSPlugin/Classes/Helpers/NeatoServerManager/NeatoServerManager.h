#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"

@protocol NeatoServerManagerProtocol <NSObject>

@optional
-(void) requestFailed:(NSError *) error;
-(void) gotUserDetails:(NeatoUser *)neatoUser;
-(void) gotRobotDetails:(NeatoRobot *)neatoRobot;
-(void) robotCreated;
-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId;
-(void) userLoggedOut;
-(void) gotUserAssociatedRobots:(NSMutableArray *) robots;



@end

@interface NeatoServerManager : NSObject

@property(nonatomic, weak) id<NeatoServerManagerProtocol> delegate;

-(void) loginNativeUser:(NSString *) email password:(NSString *)password;
-(void) loginFacebookUser:(NSString *) externalSocialId;
-(void) getUserAccountDetails:(NSString *) authToken email:(NSString *) email;
-(void) createUser:(NeatoUser *)neatoUser;
-(void) createRobot:(NeatoRobot *)neatoRobot;
-(void) getRobotDetails:(NSString *)serialNumber;
-(void) setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number;
-(void) logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token;
-(void) getAssociatedRobots:(NSString *)email authToken:(NSString *)authToken;
-(void) updateUserAuthToken:(NSString *) authToken;

@end
