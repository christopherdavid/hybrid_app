#import <Foundation/Foundation.h>

@interface NeatoErrorCodesHelper : NSObject
+ (id)sharedErrorCodesHelper;
- (NSInteger)uiErrorCodeForServerErrorCode:(NSInteger)serverErrorCode;
@end
