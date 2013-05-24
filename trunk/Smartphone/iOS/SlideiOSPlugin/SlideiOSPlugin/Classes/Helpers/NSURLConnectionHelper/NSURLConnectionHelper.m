#import "NSURLConnectionHelper.h"
#import "LogHelper.h"
#import "AppHelper.h"

#define SMART_APP_ERROR_DOMAIN @"NeatoSmartApp"

@interface NSURLConnectionHelper()
{
    
}

@property(nonatomic, retain) NSNumber *expectedSize;
@property(nonatomic, retain) NSNumber *downloadedSize;

@property(nonatomic, readwrite) int responseCode;
@property(nonatomic, retain) NSURLConnectionHelper *retained_self;
@property(nonatomic, retain) NSMutableData *responseData;
@property(nonatomic, retain) NSURLConnection *connection;
@property(readwrite) bool connectionFinished;
@property(nonatomic, retain) NSURL *destinationPath;

- (void) writeToFileHelper:(NSString *) fileNameWithPath data:(NSData*) dataToWrite;
@end

@implementation NSURLConnectionHelper
@synthesize retained_self = _retained_self;
@synthesize responseCode = _responseCode;
@synthesize responseData = _responseData;
@synthesize delegate = _delegate;
@synthesize connection = _connection;
@synthesize connectionFinished = _connectionFinished;
@synthesize destinationPath = _destinationPath;
@synthesize expectedSize = _expectedSize;
@synthesize downloadedSize = _downloadedSize;

-(NSURLConnection *) getDataForRequest:(NSURLRequest *) request
{
    debugLog(@"");
    if (request == nil)
    {
        debugLog(@"URLRequest cannot be nil. Stopping!");
        return nil;
    }
    self.connectionFinished = NO;
    self.retained_self = self;
    self.responseData = [[NSMutableData alloc] init];
    
    // Trace app info
    [AppHelper traceAppInfo];
    
    self.connection = [[NSURLConnection alloc] initWithRequest:request delegate:self startImmediately:NO];
    
    // As we want all download requests to work concurrently, using global queue
    // Apart from being concurrent, as the queue is global, we dont have to
    // manage its memory allocation
    // Getting a queue with high priority, this shoudnt have any negative impact
    // on apps performance.
    dispatch_queue_t connectionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0);
    dispatch_async(connectionQueue, ^{
        
        NSRunLoop* runloop = [NSRunLoop currentRunLoop]; // Get the runloop
        [self.connection start];
        
        // We want to keep the 'run loop' running till NSURLConnection is doing its job.
        // If we use the 'run' method of runLoop, as we did before, the loop will continue
        // running even after  NSURLConnection has done downloading content.
        // Now we keep the loop running using 'runMode' method till we have finished our
        // task. This way after the download is complete we release the quaue
        while (!self.connectionFinished && [runloop runMode:NSDefaultRunLoopMode beforeDate:[NSDate distantFuture]]);
    });
    
    return self.connection;
}

-(NSURLConnection *) downloadDataForRequest:(NSURLRequest *) request andSaveAtPath:(NSURL *) path
{
    debugLog(@"");
    self.destinationPath = path;
    return [self getDataForRequest:request];
}

-(void) notifyRequestFailed:(NSError *) error
{
    debugLog(@"");
    
    // Delele the downloaded file if it exsts
    NSFileManager *filemgr = [NSFileManager defaultManager];
    if ([filemgr fileExistsAtPath: [self.destinationPath path]] == YES)
    {
        if([filemgr removeItemAtPath:[self.destinationPath path] error:nil])
        {
            debugLog(@"Partial\\Corrupt downloaded file deleted.");
        }
        else
        {
            debugLog(@"Could NOT delete partila\\corrupt downloaded file. Terrible things may happen!");
        }
    }
    
    if ([self.delegate respondsToSelector:@selector(requestFailedForConnection:error:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(requestFailedForConnection:error:) withObject:self.connection withObject:error];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    debugLog(@"Error = %@", error);
    self.connectionFinished = YES;
    [self notifyRequestFailed:error];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    debugLog(@"");
    if (self.destinationPath)
    {
        NSNumber *num = [[NSNumber alloc] initWithLongLong:[self.downloadedSize longLongValue] + [data length]];
        self.downloadedSize = num;
        [self writeToFileHelper:[self.destinationPath path] data:data];
    }
    else
    {
        [self.responseData appendData:data];
    }
}

- (void) writeToFileHelper:(NSString *) fileNameWithPath data:(NSData*) dataToWrite
{
    NSOutputStream *stream =[[NSOutputStream alloc] initToFileAtPath:fileNameWithPath append:YES];
    [stream open];
    
    NSInteger       dataLength;
    const uint8_t * dataBytes;
    NSInteger       bytesWritten;
    NSInteger       bytesWrittenSoFar;
    
    dataLength = [dataToWrite length];
    dataBytes  = [dataToWrite bytes];
    
    bytesWrittenSoFar = 0;
    do
    {
        bytesWritten = [stream write:&dataBytes[bytesWrittenSoFar] maxLength:dataLength - bytesWrittenSoFar];
        
        if (-1 == bytesWritten) {
            break;
        }
        
        bytesWrittenSoFar += bytesWritten;
    } while (bytesWrittenSoFar != dataLength);
    
    [stream close];
}


- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    debugLog(@"");
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*) response;
    self.responseCode = [httpResponse statusCode];
    debugLog(@"Request responseCode = %d", self.responseCode);
    if (self.responseCode == 200)
    {
        
        if (self.destinationPath)
        {
            self.expectedSize =  [[NSNumber alloc] initWithLongLong:[httpResponse expectedContentLength]] ;
            debugLog(@"expectedSize = %lld", [self.expectedSize longLongValue] );
            
            NSFileManager *filemgr = [NSFileManager defaultManager];
            // Deleting the file if it already exists
            if ([filemgr fileExistsAtPath: [self.destinationPath path]] == YES)
            {
                [filemgr removeItemAtPath:[self.destinationPath path] error:nil];
            }
            
            // File creation
            if (![filemgr createFileAtPath:[self.destinationPath path] contents:nil attributes:nil])
            {
                [self.connection cancel];
                debugLog(@"Could not create file at path = %@. Stopping download!", self.destinationPath);
                NSMutableDictionary* details = [NSMutableDictionary dictionary];
                [details setValue:@"Could not create file at path = %@. Stopping download!" forKey:NSLocalizedDescriptionKey];
                
                NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:self.responseCode] integerValue] userInfo:details];
                [self notifyRequestFailed:error];
            }
        }

    }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    debugLog(@"");
    self.connectionFinished = YES;
    if (self.responseCode == 200)
    {
        if (!self.destinationPath)
        {
            if ([self.delegate respondsToSelector:@selector(connectionDidFinishLoading:responseData:)])
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate performSelector:@selector(connectionDidFinishLoading:responseData:) withObject:connection withObject:self.responseData];
                    self.delegate = nil;
                    self.retained_self = nil;
                });
            }
            
        }
        else
        {
            // Check if file data is corrupt
            if ([self.expectedSize longLongValue] == [self.downloadedSize longLongValue])
            {
                debugLog(@"File download SUCCESS!");
                if ([self.delegate respondsToSelector:@selector(connectionDidFinishLoading:filePath:)])
                {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.delegate performSelector:@selector(connectionDidFinishLoading:filePath:) withObject:connection withObject:self.destinationPath];
                        self.delegate = nil;
                        self.retained_self = nil;
                    });
                }
            }
            else
            {
                debugLog(@"File data corrupt. Download failed!!");
                NSMutableDictionary* details = [NSMutableDictionary dictionary];
                [details setValue:@"File data corrupt. Download failed!!" forKey:NSLocalizedDescriptionKey];
                
                NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:self.responseCode] integerValue] userInfo:details];
                [self notifyRequestFailed:error];
            }
        }
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:NETWORK_CONNECTION_FAILURE_MSG forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:self.responseCode] integerValue] userInfo:details];
        [self notifyRequestFailed:error];
    }
}

@end