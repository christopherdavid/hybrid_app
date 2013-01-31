#import "UserManagerCallWrapper.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "NeatoRobotManager.h"
#import "NeatoRobotHelper.h"

@interface UserManagerCallWrapper()

@property(nonatomic, retain) UserManagerCallWrapper *retained_self;
@property(nonatomic, retain) NSString *callbackId;

-(void) notifyCallback:(SEL) action;
-(void) notifyCallback:(SEL) action object:(id) object;
@end
@implementation UserManagerCallWrapper
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize callbackId = _callbackId;

-(BOOL) isUserLoggedIn
{
    debugLog(@"");
    if ([NeatoUserHelper getNeatoUser])
    {
        // User is logged in, lets extend the auth key expiry
        // When the Auth key is extended we do not notify the caller, so we dont need
        // to retain 'self' here
        NeatoServerManager *manager = [[NeatoServerManager alloc] init];
        manager.delegate = self;
        [manager updateUserAuthToken:[NeatoUserHelper getUsersAuthToken]];
        return YES;
    }
    else
    {
        return NO;
    }
}

-(void) loginUserWithEmail:(NSString *) email password:(NSString *) password callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager loginNativeUser:email password:password];
}

-(void) loginSuccess:(NeatoUser *) user
{
    debugLog(@"");
    [self notifyCallback:@selector(loginSuccess:callbackId:) object:user];
}

-(void) failedToCreateUserWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyCallback:@selector(failedToCreateUserWithError:callbackId:) object:error];
}

-(void) userCreated:(NeatoUser *) neatoUser
{
    debugLog(@"");
    [self notifyCallback:@selector(userCreated:callbackId:) object:neatoUser];
}

-(void) loginFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(loginFailedWithError:callbackId:) object:error];
}

-(void) loginFacebookUser:(NSString *) externalSocialId callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager loginFacebookUser:externalSocialId];
    
}

-(void) getUserAccountDetails:(NSString *) authToken email:(NSString *) email callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager getUserAccountDetails:authToken email:email];
}


-(void) createUser:(NeatoUser *)neatoUser callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager createUser:neatoUser];
    
}

-(void) userCreationFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(userCreationFailedWithError:callbackId:) object:error];
}


-(void) createRobot:(NeatoRobot *)neatoRobot callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager createRobot:neatoRobot];
}


-(void) getRobotDetails:(NSString *)serialNumber callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager getRobotDetails:serialNumber];
}


-(void) setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager setRobotUserEmail:email serialNumber:serial_number];
}


-(void) logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token callbackID:(NSString *) callbackId
{
    debugLog(@"");
    self.retained_self = self;
    self.callbackId = callbackId;
    
    NeatoServerManager *manager = [[NeatoServerManager alloc] init];
    manager.delegate = self;
    [manager logoutUserEmail:email authToken:auth_token];
}

-(void) logoutRequestFailedWithEror:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(logoutRequestFailedWithEror:callbackId:) object:error];
}

-(void) notifyCallback:(SEL) action
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:action])
        {
            [self.delegate performSelector:action withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}
-(void) notifyCallback:(SEL) action object:(id) object
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:action])
        {
            [self.delegate performSelector:action withObject:object withObject:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}


-(void) failedToGetUserDetailsWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(failedToGetUserDetailsWithError:callbackId:) object:error];
}

/*-(void) requestFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyCallback:@selector(requestFailed:callbackId:) object:error];
}*/


-(void) gotUserDetails:(NeatoUser *)neatoUser
{
    debugLog(@"");
    // Saving the user details to shared prefs/db
    [self notifyCallback:@selector(gotUserDetails:callbackId:) object:neatoUser];
}

-(void) failedToGetRobotDetailsWihError:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(failedToGetRobotDetailsWihError:callbackId:) object:error];
}

-(void) gotRobotDetails:(NeatoRobot *)neatoRobot
{
    debugLog(@"");
    // Saving the robot details to db
    [self notifyCallback:@selector(gotRobotDetails:callbackId:) object:neatoRobot];
}

-(void) robotCreationFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(robotCreationFailedWithError:callbackId:) object:error];
}

-(void) robotCreated
{
    debugLog(@"");
    [self notifyCallback:@selector(robotCreated:)];
}

-(void) robotAssociationFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyCallback:@selector(robotAssociationFailedWithError:callbackId:) object:error];
}

-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(robotAssociatedWithUser:robotId:callbackId:)])
        {
            [self.delegate robotAssociatedWithUser:message robotId:robotId callbackId:self.callbackId];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
    //[self notifyCallback:@selector(robotAssociatedWithUser:robotId:callbackId:) object:messag];
}


-(void) userLoggedOut
{
    debugLog(@"");
    [NeatoRobotManager diconnectRobotFromTCP:@"" delegate:self];
    [NeatoRobotManager logoutFromXMPP:self];
    [NeatoUserHelper clearUserData];
    [self notifyCallback:@selector(userLoggedOut:)];
}


@end
