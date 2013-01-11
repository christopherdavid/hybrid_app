#import <Foundation/Foundation.h>
#import "NeatoDBHelper.h"

@interface NeatoUserHelper : NSObject

+(NeatoUser *) getNeatoUser;
+(void) saveNeatoUser:(NeatoUser *) neatoUser;
+(void) clearUserData;
+(NSString *) getLoggedInUserEmail;
+(NSString *) getUsersAuthToken;
+(void) saveUserAuthToken:(NSString *) authToken;

@end
