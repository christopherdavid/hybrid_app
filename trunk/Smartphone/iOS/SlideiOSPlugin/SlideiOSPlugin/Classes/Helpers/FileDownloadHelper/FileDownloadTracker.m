#import "FileDownloadTracker.h"
#import "LogHelper.h"

static FileDownloadTracker *sharedInstance;
@interface FileDownloadTracker()
{

}

@property(nonatomic, retain) NSMutableDictionary *downloadingUrlsMap;
@end
@implementation FileDownloadTracker
@synthesize downloadingUrlsMap = _downloadingUrlsMapl;


+(FileDownloadTracker *) getSharedFileDownloadTracker
{
    debugLog(@"");
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[FileDownloadTracker alloc] init];
    });
    return sharedInstance;
}

-(id) init
{
    @synchronized(self)
    {
        if(self = [super init])
        {
            // Initialization code here.
            self.downloadingUrlsMap = [[NSMutableDictionary alloc] init];
        }
    }
    return self;
}

-(void) addPathToDownloadTracker:(NSString *) path
{
    debugLog(@"");
    @synchronized(self)
    {
        [self.downloadingUrlsMap setObject:@"" forKey:path];
    }
}

-(void) removePathFromDownloadTracker:(NSString *) path
{
    debugLog(@"");
    @synchronized(self)
    {
        [self.downloadingUrlsMap removeObjectForKey:path];
    }
}


-(BOOL) isDownloadingFromPath:(NSString *) path
{
    debugLog(@"");
    @synchronized(self)
    {
        return [self.downloadingUrlsMap objectForKey:path] != nil;
    }
}



@end
