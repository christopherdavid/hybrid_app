#import <Foundation/Foundation.h>


@interface NeatoUser : NSObject

@property (nonatomic, retain) NSString *userId;
@property (nonatomic, retain) NSString *name;
@property (nonatomic, retain) NSString *email;
@property (nonatomic, retain) NSString *chatId;
@property (nonatomic, retain) NSString *chatPassword;
@property (nonatomic, retain) NSMutableArray *socialNetworks;
@property (nonatomic, retain) NSMutableArray *robots;
@property (nonatomic, retain) NSString *account_type;
@property (nonatomic, retain) NSString *password;
@property (nonatomic, retain) NSString *external_social_id;
@property (nonatomic, strong) NSString *alternateEmail;
@property (nonatomic, strong) NSString *validationStatus;
@property (nonatomic, strong) NSString *countryCode;
@property (nonatomic) BOOL optIn;
@property (nonatomic, strong) NSString *userCountryCode;

- (id)initWithDictionary: (NSDictionary *)dictionary;
- (NSNumber *)userValidationStatus;
- (NSString *)extraParam;

@end
