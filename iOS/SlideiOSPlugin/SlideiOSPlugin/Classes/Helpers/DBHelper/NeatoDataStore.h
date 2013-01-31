#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>

@interface NeatoDataStore : NSObject

+(NeatoDataStore *) sharedNeatoDataStore;


// Returns yes if save was successfull
-(BOOL) addCommandToTracker:(NSString *) xmlCommand withRequestId:(NSString *) requestId;
// Returns yes if deletion was successfull
-(BOOL) removeCommandForRequestId:(NSString *) requestId;
-(NSString *) getCommandForRequestId:(NSString *) requestId;
@end
