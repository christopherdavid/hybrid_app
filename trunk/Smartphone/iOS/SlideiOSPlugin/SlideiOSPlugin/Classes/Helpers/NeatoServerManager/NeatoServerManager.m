#import "NeatoServerManager.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"
#import "LoginListener.h"
#import "CreateUserListener.h"
#import "NeatoRobotHelper.h"
#import "RobotAssociationListener.h"
#import "SetRobotNameListener.h"
#import "RobotAssociationListener2.h"
#import "RobotDissociationListener.h"

@interface NeatoServerManager()

@property(nonatomic, retain) NeatoServerManager *retained_self;
@property(nonatomic, retain) LoginListener *loginListener;
@property(nonatomic, retain) CreateUserListener *createUserListener;
@property(nonatomic, retain) RobotAssociationListener *associationListener;
@property(nonatomic, retain) NSString *userEmail;

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error;
@end


@implementation NeatoServerManager
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize loginListener = _loginListener;
@synthesize createUserListener = _createUserListener;
@synthesize associationListener = _associationListener;
@synthesize userEmail = _userEmail;

-(void) loginNativeUser:(NSString *) email password:(NSString *)password
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper loginNativeUser:email password:password];
}

// Gets called from LoginListener
-(void) loginFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(loginFailedWithError:) withError:error];
}

// Gets called from LoginListener
-(void) loginSuccess:(NeatoUser *) user
{
    debugLog(@"");
    // Save the details to local storage
    [NeatoUserHelper saveNeatoUser:user];
    
    [self.delegate performSelectorOnMainThread:@selector(loginSuccess:) withObject:user waitUntilDone:NO];
    self.delegate = nil;
    self.retained_self = nil;
}

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.delegate performSelector:selector withObject:error];
        self.delegate = nil;
        self.retained_self = nil;
    });
}

// Gets called from NeatoServerHelper
-(void) failedToGetLoginHandle:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(loginFailedWithError:) withError:error];
}

// Gets called from NeatoServerHelper
-(void) gotUserHandleForLogin:(NSString *) userHandle;
{
    debugLog(@"");
    // save auth token to local storage
    [NeatoUserHelper saveUserAuthToken:userHandle];
    // Get user details from server
    
    self.loginListener = [[LoginListener alloc] initWithDelegate:self];
    NeatoServerHelper *helper = [[NeatoServerHelper alloc]init];
    helper.delegate = self.loginListener;
    
    [helper getUserAccountDetails:userHandle email:nil];
    
}


-(void) loginFacebookUser:(NSString *) externalSocialId
{
    debugLog(@"");
    self.retained_self = self;
}


- (void)getUserDetailsForEmail:(NSString *)email authToken:(NSString *)authToken
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper getUserAccountDetails:authToken email:email];
}

-(void) failedToGetUserDetailsWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetUserDetailsWithError:) withError:error];
}

-(void) gotUserDetails:(NeatoUser *)neatoUser
{
    debugLog(@"");
    [NeatoUserHelper saveNeatoUser:neatoUser];
    if ([self.delegate respondsToSelector:@selector(gotUserDetails:)])
    {
        [self.delegate performSelectorOnMainThread:@selector(gotUserDetails:) withObject:neatoUser waitUntilDone:NO];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

-(void) createUser:(NeatoUser *)neatoUser
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createUser:neatoUser];
}

-(void)userCreated:(NeatoUser *) user
{
    debugLog(@"");
    // Save the details to local storage
    [NeatoUserHelper saveNeatoUser:user];
    
    if ([self.delegate respondsToSelector:@selector(userCreated:)])
    {
        [self.delegate performSelectorOnMainThread:@selector(userCreated:) withObject:user waitUntilDone:NO];
        self.delegate = nil;
        self.retained_self = nil;
    }
}

-(void) failedToCreateUserWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUserWithError:) withError:error];
}

-(void) failedToGetCreateUserHandle:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToCreateUserWithError:) withError:error];
}

-(void) gotHandleForCreateUser:(NSString *) authToken
{
    debugLog(@"");
    // save users auth token to local storage
    [NeatoUserHelper saveUserAuthToken:authToken];
    
    self.createUserListener = [[CreateUserListener alloc] initWithDelegate:self];
    // Get the user details now
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.createUserListener;
    [helper getUserAccountDetails:authToken email:nil];
}

-(void) createRobot:(NeatoRobot *)neatoRobot
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper createRobot:neatoRobot];
}

-(void) robotCreationFailedWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotCreationFailedWithError:) withError:error];
}

-(void) robotCreated
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotCreated)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
           [self.delegate performSelector:@selector(robotCreated)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    
}

- (void)getRobotDetails:(NSString *)serialNumber
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper getRobotDetails:serialNumber];
}

-(void) gotRobotDetails:(NeatoRobot *)neatoRobot
{
    debugLog(@"");
    // Save the details to local storage
    [NeatoRobotHelper saveNeatoRobot:neatoRobot];
    if ([self.delegate respondsToSelector:@selector(gotRobotDetails:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(gotRobotDetails:) withObject:neatoRobot];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) failedToGetRobotDetailsWihError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotDetailsWihError:) withError:error];
}

-(void) setRobotUserEmail:(NSString *)email serialNumber:(NSString *)serial_number
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper setRobotUserEmail:email serialNumber:serial_number];
}

-(void) robotAssociatedWithUser:(NSString *)message robotId:(NSString *)robotId
{
    debugLog(@"");
    // Fetch all robots for user and update the DB
    // Then notify the caller
    self.associationListener = [[RobotAssociationListener alloc] initWithDelegate:self];
    self.associationListener.associatedRobotId = robotId;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self.associationListener;
    [helper associatedRobotsForUserWithEmail:[NeatoUserHelper getLoggedInUserEmail] authToken:[NeatoUserHelper getUsersAuthToken]];
}

-(void) robotAssociationCompletedSuccessfully:(NSString *) robotId
{
    // Notify the caller
    if ([self.delegate respondsToSelector:@selector(robotAssociatedWithUser:robotId:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotAssociatedWithUser:robotId:) withObject:[NeatoUserHelper getLoggedInUserEmail] withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) robotAssociationFailedWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotAssociationFailedWithError:) withError:error];
}

-(void) logoutUserEmail:(NSString *)email authToken:(NSString *)auth_token
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper logoutUserEmail:email authToken:auth_token];
}


-(void) logoutRequestFailedWithEror:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(logoutRequestFailedWithEror:) withError:error];
}

-(void) userLoggedOut
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userLoggedOut)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(userLoggedOut)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)associatedRobotsForUserWithEmail:(NSString *)email authToken:(NSString *)authToken {
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper associatedRobotsForUserWithEmail:email authToken:authToken];
}

-(void) failedToGetAssociatedRobotsWithError:(NSError *)error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetAssociatedRobotsWithError:) withError:error];
}

-(void) gotUserAssociatedRobots:(NSMutableArray *)robots
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(gotUserAssociatedRobots:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(gotUserAssociatedRobots:) withObject:robots];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) updateUserAuthToken:(NSString *) authToken
{
    debugLog(@"");
    self.retained_self = self;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper updateUserAuthToken:authToken];
}

- (void)setRobotName2:(NSString *)robotName forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    SetRobotNameListener *setNameListener = [[SetRobotNameListener alloc] initWithDelegate:self];
    setNameListener.robotId = robotId;
    setNameListener.robotName = robotName;
    [setNameListener start];
}

- (void)robotName:(NSString *)name updatedForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotName:updatedForRobotWithId:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotName:updatedForRobotWithId:) withObject:name withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToUpdateRobotNameWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToUpdateRobotNameWithError:) withError:error];
}

- (void)setRobotUserEmail2:(NSString *)userEmail forRobotId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    
    RobotAssociationListener2 *associationListener = [[RobotAssociationListener2 alloc]initWithDelegate:self];
    associationListener.userEmail = userEmail;
    associationListener.robotId = robotId;
    [associationListener start];
}

- (void)robotAssociation2FailedWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(robotAssociation2FailedWithError:) withError:error];
}

- (void)userAssociateWithRobot:(NeatoRobot *)neatoRobot {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(userAssociateWithRobot:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(userAssociateWithRobot:) withObject:neatoRobot];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)onlineStatusForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    self.retained_self = self;
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper onlineStatusForRobotWithId:robotId];
}

- (void)onlineStatus:(NSString *)status forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(onlineStatus:forRobotWithId:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(onlineStatus:forRobotWithId:) withObject:status withObject:robotId];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToGetRobotOnlineStatusWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToGetRobotOnlineStatusWithError:) withError:error];
}

- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email {
    debugLog(@"");
    self.retained_self = self;
    self.userEmail = email;
    
    NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
    helper.delegate = self;
    [helper dissociateAllRobotsForUserWithEmail:email];
}

- (void)dissociatedAllRobots:(NSString *)message {
    debugLog(@"");
    //Update the database.
    [NeatoUserHelper dissociateAllRobotsForUserWithEmail:self.userEmail];
    if ([self.delegate respondsToSelector:@selector(dissociatedAllRobots:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(dissociatedAllRobots:) withObject:message];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToDissociateAllRobots:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToDissociateAllRobots:) withError:error];
}

- (void)dissociateRobotWithId:(NSString *)robotId fromUserWithEmail:(NSString *)emailId {
    self.retained_self = self;
    RobotDissociationListener *dissociationlistener = [[RobotDissociationListener alloc] initWithDelegate:self];
    dissociationlistener.robotId = robotId;
    dissociationlistener.userEmail = emailId;
    [dissociationlistener start];   
}

- (void)robotDissociatedWithMessage:(NSString *)message {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(robotDissociatedWithMessage:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(robotDissociatedWithMessage:) withObject:message];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}
- (void)failedToDissociateRobotWithError:(NSError *)error {
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToDissociateRobotWithError:) withError:error];

}

- (void)registerPushNotificationForEmail:(NSString *)email deviceType:(NSInteger)deviceType deviceToken:(NSString *)deviceToken {
  debugLog(@"registerPushNotification called");
  self.retained_self = self;
  
  NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
  helper.delegate = self;
  [helper registerPushNotificationForEmail:email deviceType:deviceType deviceToken:deviceToken];
}

- (void)unregisterPushNotificationForDeviceToken:(NSString *)deviceToken {
  debugLog(@"unregisterPushNotification called");
  self.retained_self = self;
  
  NeatoServerHelper *helper = [[NeatoServerHelper alloc] init];
  helper.delegate = self;
  [helper unregisterPushNotificationForDeviceToken:deviceToken];
}


- (void)pushNotificationRegistrationFailedWithError:(NSError *) error {
  debugLog(@"pushNotificationRegistrationFailed called");
  [self notifyRequestFailed:@selector(pushNotificationRegistrationFailedWithError:) withError:error];
  self.delegate = nil;
  self.retained_self = nil;
}
- (void)pushNotificationRegisteredForDeviceToken:(NSString *)deviceToken {
  debugLog(@"pushNotificationRegisteredForDeviceToken called");
  if ([self.delegate respondsToSelector:@selector(pushNotificationRegisteredForDeviceToken:)])
  {
    dispatch_async(dispatch_get_main_queue(), ^{
      [self.delegate performSelector:@selector(pushNotificationRegisteredForDeviceToken:) withObject:deviceToken];
      self.delegate = nil;
      self.retained_self = nil;
    });
  }
}


- (void)pushNotificationUnregistrationFailedWithError:(NSError *)error {
  debugLog(@"pushNotificationUnregistrationFailedWithError called");
  [self notifyRequestFailed:@selector(pushNotificationUnregistrationFailedWithError:) withError:error];
  self.delegate = nil;
  self.retained_self = nil;
}
- (void)pushNotificationUnregistrationSuccess {
  debugLog(@"pushNotificationUnregistrationSuccess called");
  if ([self.delegate respondsToSelector:@selector(pushNotificationUnregistrationSuccess:)])
  {
    dispatch_async(dispatch_get_main_queue(), ^{
      [self.delegate performSelector:@selector(pushNotificationUnregistrationSuccess:)];
      self.delegate = nil;
      self.retained_self = nil;
    });
  }
 
}
@end
