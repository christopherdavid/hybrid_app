#import <Foundation/Foundation.h>

@interface CommandTracker : NSObject

-(BOOL) addCommandToTracker:(NSString *) xmlCommand withRequestId:(NSString *) requestId;
-(BOOL) removeCommandForRequestId:(NSString *) requestId;
-(NSString *) getCommandForRequestId:(NSString *) requestId;


@end
