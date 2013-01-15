#import <Foundation/Foundation.h>
#import "NeatoServerManager.h"
#import <Cordova/CDV.h>

@protocol UserManagerProtocol <NSObject>

-(void) requestFailed:(NSError *) error callbackId:(NSString *)callbackId;
-(void) gotUserDetails:(NeatoUser *)neatoUser callbackId:(NSString *)callbackId;
-(void) gotRobotDetails:(NeatoRobot *)neatoRobot callbackId:(NSString *)callbackId;
-(void) robotCreated:(NSString *)callbackId;
-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *) robotId callbackId:(NSString *)callbackId;
-(void) userLoggedOut:(NSString *)callbackId;

@end

@interface UserManagerCallWrapper : CDVPlugin <NeatoServerManagerProtocol>

@property(nonatomic, weak) id<UserManagerProtocol> delegate;

-(void) loginUserWithEmail:(NSString *) email password:(NSString *) password callbackID:(NSString *) callbackId;
-(void) loginFacebookUser:(NSString *) externalSocialId callbackID:(NSString *) callbackId;
-(void) getUserAccountDetails:(NSString *) authToken email:(NSString *) email callbackID:(NSString *) callbackId;
-(void) createUser:(NeatoUser *)neatoUser callbackID:(NSString *) callbackId;
-(void) createRobot:(NeatoRobot *)neatoRobot callbackID:(NSString *) callbackId;
-(void) getRobotDetails:(NSString *)serialNumber callbackID:(NSString *) callbackId;
-(void) setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number callbackID:(NSString *) callbackId;
-(void) logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token callbackID:(NSString *) callbackId;
-(BOOL) isUserLoggedIn;


@end