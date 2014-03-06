#import "NeatoErrorCodesHelper.h"
#import "NeatoErrorCodes.h"

static NeatoErrorCodesHelper *sharedInstance  = nil;

@interface NeatoErrorCodesHelper()
@property (nonatomic, strong) NSMutableDictionary *errorMap;

- (void)populateErrorMap;
@end

@implementation NeatoErrorCodesHelper

+ (id)sharedErrorCodesHelper {
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[NeatoErrorCodesHelper alloc] init];
    });
    return sharedInstance;
}

- (id)init {
    if (self = [super init]) {
        [self populateErrorMap];
    }
    return self;
}

// Mapping of server error codes to UI error codes.
- (void)populateErrorMap {
    
    self.errorMap = [[NSMutableDictionary alloc] init];
    
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_AUTHENTICATION_FAILED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_AUTHENTICATION_FAILED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_INVALID_EMAIL_ID] forKey:[NSNumber numberWithInteger:SERVER_ERROR_INVALID_EMAIL_ID]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_EMAIL_ALREADY_REGISTERED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_EMAIL_ALREADY_REGISTERED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_CREATE_USER_FAILED_TRY_AGAIN] forKey:[NSNumber numberWithInteger:SERVER_ERROR_CREATE_USER_FAILED_TRY_AGAIN]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_OLD_PASSWORD_MISMATCH] forKey:[NSNumber numberWithInteger:SERVER_ERROR_OLD_PASSWORD_MISMATCH]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_INVALID_ROBOT_ACCOUNT_DETAIL] forKey:[NSNumber numberWithInteger:SERVER_ERROR_INVALID_ROBOT_ACCOUNT_DETAIL]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_EMAIL_NOT_REGISTERED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_EMAIL_NOT_REGISTERED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_ROBOT_NOT_REGISTERED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_ROBOT_NOT_REGISTERED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_INVALID_ALTERNATE_EMAIL_ID] forKey:[NSNumber numberWithInteger:SERVER_ERROR_INVALID_ALTERNATE_EMAIL_ID]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_EMAIL_ALREADY_VALIDATED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_EMAIL_ALREADY_VALIDATED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_SCHEDULE_VERSION_MISMATCH] forKey:[NSNumber numberWithInteger:SERVER_ERROR_SCHEDULE_VERSION_MISMATCH]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_INVALID_SCHEDULE_TYPE] forKey:[NSNumber numberWithInteger:SERVER_ERROR_INVALID_SCHEDULE_TYPE]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT] forKey:[NSNumber numberWithInteger:SERVER_ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS] forKey:[NSNumber numberWithInteger:SERVER_ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_ROBOT_HAS_ASSOCIATED_USER] forKey:[NSNumber numberWithInteger:SERVER_ERROR_ROBOT_HAS_ASSOCIATED_USER]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_INVALID_LINKING_CODE] forKey:[NSNumber numberWithInteger:SERVER_ERROR_INVALID_LINKING_CODE]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_LINKING_CODE_EXPIRED] forKey:[NSNumber numberWithInteger:SERVER_ERROR_LINKING_CODE_EXPIRED]];
    [self.errorMap setObject:[NSNumber numberWithInteger:UI_ERROR_LINKING_CODE_IN_USE] forKey:[NSNumber numberWithInteger:SERVER_ERROR_LINKING_CODE_IN_USE]];

}

// Returns UI error code for given server code
// If not found, returns error type unknown.
- (NSInteger)uiErrorCodeForServerErrorCode:(NSInteger)serverErrorCode {
    if ([self.errorMap objectForKey:[NSNumber numberWithInteger:serverErrorCode]]) {
        return [[self.errorMap objectForKey:[NSNumber numberWithInteger:serverErrorCode]] integerValue];
    }
    return UI_ERROR_TYPE_UNKNOWN;
}

@end
