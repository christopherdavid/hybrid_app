#import <Foundation/Foundation.h>
#import "FileDownloadHelper.h"

// A wrapper around FileDownloadHelper class
// User should use this class rather than using FileDownloadHelper directly

// Adds capability to check if the file for the same URL is already downloaded.
@interface FileDownloadManager : NSObject

// Makes a GET request.
// Send 'YES' for parameter 'getFromCache', to get already downloaded file for the URL
// If the file was not downloaded previously, it would be downloaded now and saved in the cache
-(void) downloadFileFromURL:(NSString *) path getFromCache:(BOOL) fromCache delegate:(id) delegate;

// Will make a POST request with 'withPostParameters' as the HttpBody
// 'withPostParameters' should be nil if the caller just wants to make a post request
// Send 'YES' for parameter 'getFromCache', to get already downloaded file for the URL
// If the file was not downloaded previously, it would be downloaded now and saved in the cache
-(void) downloadFileFromURL:(NSString *) path withPostParameters:(NSString *) postBody getFromCache:(BOOL) fromCache delegate:(id) delegate;

// Deletes all files stored locally for this app.
-(void) purgeLocalCache;

@end
