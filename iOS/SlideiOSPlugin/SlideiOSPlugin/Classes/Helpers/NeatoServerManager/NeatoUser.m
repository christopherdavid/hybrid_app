#import "NeatoUser.h"
#import "LogHelper.h"
#import "NeatoSocialNetworks.h"
#import "NeatoRobot.h"
#import "AppHelper.h"

// User validation status code on server.
#define USER_VALIDATION_STATUS_VALIDATED 0
#define USER_VALIDATION_STATUS_VALIDATION_IN_GRACEPERIOD -1
#define USER_VALIDATION_STATUS_NOT_VALIDATED -2


// We convert the user validation server status code to internal status code
#define VALIDATION_STATUS_UNKNOWN -99
#define VALIDATION_STATUS_VALIDATED 0
#define VALIDATION_STATUS_PENDING -1
#define VALIDATION_STATUS_NOT_VALIDATED -2

@implementation NeatoUser

- (id)initWithDictionary:(NSDictionary *)dictionary {
    debugLog(@"User dictionary from server = %@", dictionary);
    if ((self = [super init])) {
        self.chatId = [dictionary valueForKey:@"chat_id"];
        self.chatPassword = [dictionary valueForKey:@"chat_pwd"];
        self.email = [dictionary valueForKey:@"email"];
        self.userId = [dictionary valueForKey:@"id"];
        self.name = [dictionary valueForKey:@"name"];
        self.alternateEmail = [dictionary valueForKey:@"alternate_email"];
        NSNumber *validationStatus = [dictionary valueForKey:@"validation_status"];
        self.validationStatus = [NSString stringWithFormat:@"%d", validationStatus.integerValue];
        NSArray *robots = [dictionary valueForKey:@"robots"];
        NSArray *socialNetworks = [dictionary valueForKey:@"social_networks"];
        NSDictionary *extraParam = [dictionary objectForKey:@"extra_param"];
        if (extraParam) {
            self.optIn = [AppHelper boolValueFromString:[extraParam objectForKey:@"opt_in"]];
            self.userCountryCode = [extraParam objectForKey:@"country_code"];
        }
        
        debugLog(@"robots count = %d", [robots count]);
        debugLog(@"socialNetworks count = %d", [socialNetworks count]);
        
        for (NSDictionary *robotData in robots) {
            NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotData];
            if (!self.robots) {
                self.robots = [[NSMutableArray alloc] init];
            }
            [self.robots addObject:robot];
        }
        
        for (NSDictionary *sociaNetwork in socialNetworks) {
            NeatoSocialNetworks *network = [[NeatoSocialNetworks alloc] initWithDictionary:sociaNetwork];
            if (!self.socialNetworks) {
                self.socialNetworks = [[NSMutableArray alloc] init];
            }
            [self.socialNetworks addObject:network];
        }
    }
    return self;
}

- (NSNumber *)userValidationStatus {
    NSInteger serverStatusCodeInteger = [self.validationStatus integerValue];
    NSInteger validationStatusCode = VALIDATION_STATUS_UNKNOWN;
    switch (serverStatusCodeInteger) {
        case USER_VALIDATION_STATUS_VALIDATED:
            validationStatusCode = VALIDATION_STATUS_VALIDATED;
            break;
            
        case USER_VALIDATION_STATUS_VALIDATION_IN_GRACEPERIOD:
            validationStatusCode = VALIDATION_STATUS_PENDING;
            break;
            
        case USER_VALIDATION_STATUS_NOT_VALIDATED:
            validationStatusCode = VALIDATION_STATUS_NOT_VALIDATED;
            break;
            
        default:
            break;
    }
    return [NSNumber numberWithInteger:validationStatusCode];
}

// For now supports only, country code and value of optIn
- (NSString *)extraParam {
    NSMutableDictionary *extraParams = [[NSMutableDictionary alloc] init];
    [extraParams setValue:(self.userCountryCode ? self.userCountryCode : @"") forKey:@"country_code"];
    [extraParams setValue:[AppHelper stringFromBool:self.optIn] forKey:@"opt_in"];
    return [AppHelper jsonStringFromNSDictionary:extraParams];
}

@end
