#import "ChangePasswordListener.h"
#import "LogHelper.h"
#import "NeatoUserHelper.h"

@interface ChangePasswordListener()
@property(nonatomic, weak) id delegate;
@property(nonatomic, strong) ChangePasswordListener *retained_self;

@end
@implementation ChangePasswordListener

@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;


- (id)initWithDelegate:(id)delegate {
    debugLog(@"");
    if ((self = [super init])) {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

- (void)changePasswordSuccess {
    debugLog(@"");
    // Update the database.
    [NeatoUserHelper updatePassword:self.changedPassword];
    if ([self.delegate respondsToSelector:@selector(changePasswordSuccess)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(changePasswordSuccess)];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)failedToChangePasswordWithError:(NSError *)error {
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(failedToChangePasswordWithError:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(failedToChangePasswordWithError:) withObject:error];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

@end
