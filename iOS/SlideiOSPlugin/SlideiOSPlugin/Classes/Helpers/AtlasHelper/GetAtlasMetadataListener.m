#import "GetAtlasMetadataListener.h"
#import "LogHelper.h"
#import "NeatoRobotAtlas.h"
#import "FileDownloadManager.h"

@interface GetAtlasMetadataListener()

@property(nonatomic, retain) NeatoRobotAtlas *robotAtlas;
@property(nonatomic, retain) GetAtlasMetadataListener *retained_self;
@property(nonatomic, weak) id delegate;

-(void) notifyRequestFailedWithError:(NSError *) error;

@end

@implementation GetAtlasMetadataListener
@synthesize retained_self = _retained_self;
@synthesize robotAtlas = _robotAtlas;
@synthesize delegate = _delegate;

-(id) initWithDelegate:(id) delegate
{
    if ((self = [super init]))
    {
        self.delegate = delegate;
        self.retained_self = self;
    }
    return self;
}

-(void) getAtlasDataFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailedWithError:error];
}

-(void) notifyRequestFailedWithError:(NSError *) error
{
    debugLog(@"");
    if([self.delegate respondsToSelector:@selector(getAtlasDataFailed:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(getAtlasDataFailed:) withObject:error];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) gotAtlasData:(NeatoRobotAtlas *)robotAtlas
{
    debugLog(@"Atlas Id = %@, Atlas xml url = %@", robotAtlas.atlasId, robotAtlas.xmlDataUrl);
    self.robotAtlas = robotAtlas;
    
    // TODO: Check local version before downloading again
    // For now we download the file each time a request comes
    
    // Download the xml
    FileDownloadManager *manager = [[FileDownloadManager alloc] init];
    [manager downloadFileFromURL:robotAtlas.xmlDataUrl getFromCache:NO delegate:self];
}

-(void) fileDownloadedForURL:(NSString *) url atPath:(NSURL *)path
{
    debugLog(@"File download at path = %@", path);
    NSError *error = nil;
    // Read the file
    NSString *jsonString = [NSString stringWithContentsOfFile:[path path] encoding:NSUTF8StringEncoding error:&error];
    if (error)
    {
        debugLog(@"Could not parse the file at location = %@", path);
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[NSString stringWithFormat:@"Could not parse the file at location = %@", path] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:200] integerValue] userInfo:details];
        [self notifyRequestFailedWithError:error];
        self.retained_self = nil;
        return;
    }
    self.robotAtlas.atlasMetadata = jsonString;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(gotAtlasData:)])
        {
            [self.delegate performSelector:@selector(gotAtlasData:) withObject:self.robotAtlas];
        }
        self.retained_self = nil;
        self.delegate = nil;
    });
}


-(void) fileDownloadFailedForURL:(NSString *) url withError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailedWithError:error];
}

@end
