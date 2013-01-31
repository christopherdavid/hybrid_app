#import "AtlasGridManager.h"
#import "LogHelper.h"
#import "FileDownloadManager.h"
#import "AtlasServerHelper.h"

@interface AtlasGridManager()
{

}

@property(nonatomic, retain) AtlasGridManager *retained_self;
@property(nonatomic, retain) NSString *robotId;
@property(nonatomic, retain) NSString *gridId;
@property(nonatomic, retain) NSString *atlasId;
@property(nonatomic, retain) AtlasGridMetadata *currentAtlasGridData;

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error;
@end

@implementation AtlasGridManager
@synthesize delegate = _delegate;
@synthesize retained_self = _retained_self;
@synthesize robotId = _robotId;
@synthesize gridId = _gridId;
@synthesize currentAtlasGridData = _currentAtlasGridData;
@synthesize atlasId = _atlasId;

-(void) getAtlasGridMetadata:(NSString *) robotId gridId:(NSString *) gridId
{
    debugLog(@"");
    self.retained_self = self;
    self.robotId = robotId;
    self.gridId = gridId;
    
    AtlasServerHelper *atlasServer = [[AtlasServerHelper alloc] init];
    atlasServer.delegate = self;
    [atlasServer getAtlasDataForRobotWithId:robotId];
}

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector])
        {
            [self.delegate performSelector:selector withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

-(void) getAtlasDataFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:error];
}

-(void) gotAtlasData:(NeatoRobotAtlas *) robotAtlas
{
    debugLog(@"");
    debugLog(@"Atlas Id = %@, Atlas xml url = %@", robotAtlas.atlasId, robotAtlas.xmlDataUrl);
    
    self.atlasId = robotAtlas.atlasId;
    
    AtlasServerHelper *atlasServer = [[AtlasServerHelper alloc] init];
    atlasServer.delegate = self;
    [atlasServer getAtlasGridMetadata:robotAtlas.atlasId];
}

-(void) gotAtlasGridMetadata:(NSArray *) gridMetadaArr
{
    debugLog(@"");
    debugLog(@"Grid arr count = %d", [gridMetadaArr count]);
    
    if (!gridMetadaArr || [gridMetadaArr count] == 0)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"No grids found." forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:[[NSNumber  numberWithInt:200] integerValue] userInfo:details];
        [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:error];
        return;
    }
    
    // TODO: Here we should match the incoming gridId with the one got from the server
    // and download the bitmap for that grid
    // Now just downloading the bitmap for first grid that we have got
    
    self.currentAtlasGridData = [gridMetadaArr objectAtIndex:0];
    
    debugLog(@"Will download grid from = %@", self.currentAtlasGridData.blobFileUrl);
    
    // TODO: Check local version before downloading again
    // For now we download the file each time a request comes
    FileDownloadManager *manager = [[FileDownloadManager alloc] init];
    [manager downloadFileFromURL:self.currentAtlasGridData.blobFileUrl getFromCache:NO delegate:self];
    
}

-(void) getAtlasGridMetadataFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:error];
}

-(void) fileDownloadedForURL:(NSString *) url atPath:(NSURL *)path
{
    debugLog(@"File download at path = %@", path);
    self.currentAtlasGridData.gridCachePath = [path path];
    self.currentAtlasGridData.atlasId = self.atlasId;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(gotAtlasGridMetadata:)])
        {
            [self.delegate performSelector:@selector(gotAtlasGridMetadata:) withObject:self.currentAtlasGridData];
        }
    });
}

-(void) fileDownloadFailedForURL:(NSString *) url withError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:error];
}

@end
