#import <Foundation/Foundation.h>

@interface FileDownloadTracker : NSObject

+(FileDownloadTracker *) getSharedFileDownloadTracker;

-(void) addPathToDownloadTracker:(NSString *) path;
-(void) removePathFromDownloadTracker:(NSString *) path;
-(BOOL) isDownloadingFromPath:(NSString *) path;


@end
