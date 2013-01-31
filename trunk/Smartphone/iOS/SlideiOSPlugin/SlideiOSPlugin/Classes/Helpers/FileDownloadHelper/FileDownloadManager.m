#import "FileDownloadManager.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "FileDownloadTracker.h"
#import "DelegationTracker.h"

#define DESTINATION_FOLDER @"DownloadedFiles/"

@interface FileDownloadManager()
{

}
@property(nonatomic, retain) FileDownloadManager *retained_self;
@property(nonatomic, retain) NSString *postBody;
@property(nonatomic, weak) id delegate;

-(void) notifyDownloadFailureForURL:(NSString *) url error:(NSError *) error;
-(NSURL *) getDestinationPath:(NSString *) originalURL;
-(BOOL) isDownloadInProgressForUrl:(NSString *) url;

@end
@implementation FileDownloadManager
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize postBody = _postBody;

-(void) downloadFileFromURL:(NSString *) path getFromCache:(BOOL) fromCache delegate:(id)delegate
{
    debugLog(@"");
    if (!path)
    {
        debugLog(@"Download URL cannot be nil. Quiting!");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"Download URL cannot be nil. Quiting!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:200] integerValue] userInfo:details];
        [self notifyDownloadFailureForURL:path error:error];
        return;
    }

    NSURL *filePath = [self getDestinationPath:path];
    
    if (fromCache)
    {
        // Check if the file exists in the cache
        BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:[filePath path]];
        if (fileExists)
        {
            debugLog(@"Requested file is avaialable locally. Not downloading again");
            if ([delegate respondsToSelector:@selector(fileDownloadedForURL:atPath:)])
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [delegate performSelector:@selector(fileDownloadedForURL:atPath:) withObject:path withObject:filePath];
                    self.retained_self = nil;
                });
            }
            return;
        }
        else
        {
            // file does not exist in the cache. Let ud start a new download now
            debugLog(@"File does not exist in the cache. Will download now!");
        }
    }
    
    // Add the delegate to DelegationTracker
    DelegateWrapper *delegateWrapper = [[DelegateWrapper alloc] init];
    delegateWrapper.delegate = delegate;
    [[DelegationTracker getSharedDelegationTracker] addDelegateToTracker:delegateWrapper forUrl:path];
    
    // Check if we are downloading for the same URL currently
    if (![self isDownloadInProgressForUrl:path])
    {
        self.retained_self = self;
        FileDownloadHelper *helper = [[FileDownloadHelper alloc] init];
        helper.delegate = self;
        
        // Add path to download tracker
        [[FileDownloadTracker getSharedFileDownloadTracker] addPathToDownloadTracker:path];
        
        if (self.postBody)
        {
            [helper downloadFileFromURL:path withPostParameters:self.postBody saveAtPath:filePath];
        }
        else
        {
            [helper downloadFileFromURL:path saveAtPath:filePath];
        }
    }
    else
    {
        debugLog(@"Download already in progress for URL = %@. Will notify on completion", path);
    }
}

-(BOOL) isDownloadInProgressForUrl:(NSString *) url
{
    return [[FileDownloadTracker getSharedFileDownloadTracker] isDownloadingFromPath:url];
}

-(NSURL *) getDestinationPath:(NSString *) originalURL
{
    debugLog(@"");
    NSURL *basePath = [AppHelper getLocalCachePath];
    NSURL *path = [basePath URLByAppendingPathComponent:DESTINATION_FOLDER];
    // Create the folder if it doesnt exist
    if([[NSFileManager defaultManager] createDirectoryAtURL:path withIntermediateDirectories:YES attributes:Nil error:Nil])
    {
        // Just append the file name
        path = [path URLByAppendingPathComponent:[AppHelper getUniqueIdFromString:originalURL]];
        return path;
    }
    else
    {
        debugLog(@"couldnt create destination directory");
        return nil;
    }
}

-(void) downloadFileFromURL:(NSString *) path withPostParameters:(NSString *) postBody getFromCache:(BOOL) fromCache delegate:(id)delegate
{
    debugLog(@"");
    self.postBody = postBody;
    [self downloadFileFromURL:path getFromCache:fromCache delegate:delegate];
}

// TODO: Needs implementation
// Deletes all local cache data
-(void) purgeLocalCache
{
    debugLog(@"");
}

-(void) fileDownloadedForURL:(NSString *) url atPath:(NSURL *)path
{
    debugLog(@"");
    // Remove path from download tracker
    @synchronized(self)
    {
        [[FileDownloadTracker getSharedFileDownloadTracker] removePathFromDownloadTracker:url];
        NSArray *delegateArr = [[DelegationTracker getSharedDelegationTracker] getDelegatesForUrl:url];
        [[DelegationTracker getSharedDelegationTracker] removeAllDelegatesFromTrackerForUrl:url];
        for (DelegateWrapper *delegateWrapper in delegateArr) {
            if ([delegateWrapper.delegate respondsToSelector:@selector(fileDownloadedForURL:atPath:)])
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [delegateWrapper.delegate performSelector:@selector(fileDownloadedForURL:atPath:) withObject:url withObject:path];
                    self.retained_self = nil;
                });
            }
        }
    }
}

-(void) fileDownloadFailedForURL:(NSString *) url withError:(NSError *) error;
{
    debugLog(@"");
    // Remove path from download tracker
    [self notifyDownloadFailureForURL:url error:error];
}

-(void) notifyDownloadFailureForURL:(NSString *) url error:(NSError *) error
{
    debugLog(@"");
    @synchronized(self)
    {
        [[FileDownloadTracker getSharedFileDownloadTracker] removePathFromDownloadTracker:url];
        NSArray *delegateArr = [[DelegationTracker getSharedDelegationTracker] getDelegatesForUrl:url];
        [[DelegationTracker getSharedDelegationTracker] removeAllDelegatesFromTrackerForUrl:url];
        for (DelegateWrapper *delegateWrapper in delegateArr) {
            if ([delegateWrapper.delegate respondsToSelector:@selector(fileDownloadFailedForURL:withError:)])
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [delegateWrapper.delegate performSelector:@selector(fileDownloadFailedForURL:withError:) withObject:url withObject:error];
                    self.retained_self = nil;
                });
            }
        }
    }
}

@end
