#import <Foundation/Foundation.h>
#import "DelegateWrapper.h"

@interface DelegationTracker : NSObject

+(DelegationTracker *) getSharedDelegationTracker;

-(void) addDelegateToTracker:(DelegateWrapper *) delegate forUrl:(NSString *) url;
-(void) removeAllDelegatesFromTrackerForUrl:(NSString *) path;
-(NSArray *) getDelegatesForUrl:(NSString *) url;

@end
