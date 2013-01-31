#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NSURLConnectionHelper.h"
#import "NeatoServerHelper.h"

@protocol NeatoServerManagerProtocol <NSObject>

-(void) loginSuccess:(NeatoUser *) user;

-(void) failedToCreateUserWithError:(NSError *) error;
-(void) userCreated:(NeatoUser *) neatoUser;


@end

@interface NeatoServerManager : NSObject <NeatoServerHelperProtocol>

@property(nonatomic, weak) id delegate;

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
