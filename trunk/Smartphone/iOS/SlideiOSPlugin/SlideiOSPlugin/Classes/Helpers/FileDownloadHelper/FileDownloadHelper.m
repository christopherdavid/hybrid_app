#import "FileDownloadHelper.h"
#import "LogHelper.h"
#import "NSURLConnectionHelper.h"
#import "AppHelper.h"



@interface FileDownloadHelper()

@property(nonatomic, retain) FileDownloadHelper *retained_self;
@property(nonatomic, retain) NSString *postBody;
@property(nonatomic, retain) NSString *downloadUrl;


@end

@implementation FileDownloadHelper
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize postBody = _postBody;
@synthesize downloadUrl = _downloadUrl;

-(void) downloadFileFromURL:(NSString *) url saveAtPath:(NSURL *) saveAtPath;
{
    debugLog(@"");
    if (url == nil || saveAtPath == nil)
    {
        debugLog(@"URL or download path cannot be nil! Stopping");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"URL or download path cannot be nil! Stopping" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:200] integerValue] userInfo:details];
        if ([self.delegate respondsToSelector:@selector(fileDownloadFailedForURL:withError:)])
        {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.delegate performSelector:@selector(fileDownloadFailedForURL:withError:) withObject:url withObject:error];
                self.delegate = nil;
                self.retained_self = nil;
            });
        }
        return;
    }
    self.retained_self = self;
    self.downloadUrl = url;
    
    NSMutableURLRequest *request;
    if (self.postBody)
    {
        request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
        [request setHTTPMethod:@"POST"];
        [request setHTTPBody:[self.postBody dataUsingEncoding:NSUTF8StringEncoding]];
    }
    else
    {
        request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
    }
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper downloadDataForRequest:request andSaveAtPath:saveAtPath];
}




// Gets called when the download completes successfully. The file would be saved
// at filePath
-(void)connectionDidFinishLoading:(NSURLConnection *)connection filePath:(NSURL *)filePath
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(fileDownloadedForURL:atPath:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(fileDownloadedForURL:atPath:) withObject:self.downloadUrl withObject:filePath];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}


// This gets called when the connection fails for any reason.
-(void) requestFailedForConnection:(NSURLConnection *)connection error:(NSError *) error
{
    debugLog(@"");
    if ([self.delegate respondsToSelector:@selector(fileDownloadFailedForURL:withError:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(fileDownloadFailedForURL:withError:) withObject:self.downloadUrl withObject:error];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) downloadFileFromURL:(NSString *) url withPostParameters:(NSString *) postBody saveAtPath:(NSURL *) saveAtPath;
{
    debugLog(@"");
    self.postBody = postBody;
    [self downloadFileFromURL:url saveAtPath:saveAtPath];
}

@end
