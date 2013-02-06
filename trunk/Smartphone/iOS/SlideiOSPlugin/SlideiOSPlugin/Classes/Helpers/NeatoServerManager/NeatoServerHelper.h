#import <Foundation/Foundation.h>
#import "NeatoUser.h"
#import "NeatoRobot.h"
#import "NSURLConnectionHelper.h"

@protocol NeatoServerHelperProtocol <NSObject>

@optional
//-(void) requestFailed:(NSError *) error;

-(void) gotUserDetails:(NeatoUser *)neatoUser;
-(void) gotRobotDetails:(NeatoRobot *)neatoRobot;
-(void) robotCreated;
-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId;
-(void) userLoggedOut;
-(void) gotUserAssociatedRobots:(NSMutableArray *) robots;
-(void) gotHandleForCreateUser:(NSString *) authToken;

// Failure cases
-(void) failedToGetCreateUserHandle:(NSError *) error;
-(void) loginFailedWithError:(NSError *) error;
-(void) failedToGetLoginHandle:(NSError *) error;
-(void) gotUserHandleForLogin:(NSString *) userHandle;
-(void) failedToGetUserDetailsWithError:(NSError *) error;
-(void) failedToGetRobotDetailsWihError:(NSError *) error;
-(void) robotCreationFailedWithError:(NSError *) error;
-(void) robotAssociationFailedWithError:(NSError *) error;
-(void) logoutRequestFailedWithEror:(NSError *) error;
-(void) failedToGetAssociatedRobotsWithError:(NSError *) error;


@end

@interface NeatoServerHelper : NSObject

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