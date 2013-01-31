#import "RobotAtlasManager.h"
#import "LogHelper.h"
#import "FileDownloadManager.h"
#import "AppHelper.h"
#import "GetAtlasMetadataListener.h"
#import "UpdateAtlasMetadataListener.h"

@interface RobotAtlasManager()
{

}

@property(nonatomic, retain) RobotAtlasManager *retained_self;
@property(nonatomic, weak) id delegate;
@property(nonatomic, retain) GetAtlasMetadataListener *getAtlasMetadataListener;
@property(nonatomic, retain) UpdateAtlasMetadataListener *updateAtlasListener;

-(void) notifyRequestFailed:(SEL) selector WithError :(NSError *) error;

@end

@implementation RobotAtlasManager
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize getAtlasMetadataListener = _getAtlasMetadataListener;
@synthesize updateAtlasListener = _updateAtlasListener;

-(void) getAtlasMetadataForRobotWithId:(NSString *) robotId delegate:(id) delegate
{
    debugLog(@"");
    self.retained_self = self;
    self.delegate = delegate;
    
    AtlasServerHelper *atlasHelper = [[AtlasServerHelper alloc] init];
    self.getAtlasMetadataListener = [[GetAtlasMetadataListener alloc] initWithDelegate:self];
    atlasHelper.delegate = self.getAtlasMetadataListener;
    [atlasHelper getAtlasDataForRobotWithId:robotId];
}

-(void) getAtlasDataFailed:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(getAtlasDataFailed:) WithError:error];
}


-(void) gotAtlasData:(NeatoRobotAtlas *)robotAtlas
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(gotAtlasData:)])
        {
            [self.delegate performSelector:@selector(gotAtlasData:) withObject:robotAtlas];
        }
        self.retained_self = nil;
        self.delegate = nil;
    });
}

-(void) notifyRequestFailed:(SEL) selector WithError :(NSError *) error
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector])
        {
            [self.delegate performSelector:selector withObject:error];
        }
        self.retained_self = nil;
        self.delegate = nil;
    });
}

-(void) updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas delegate:(id) delegate
{
    debugLog(@"");
    self.retained_self = self;
    self.delegate = delegate;
    
    // input robotAtlas contains robotId, updated metadata
   
    // Get the latest atlas data
    // Check if the current version matches with the version we have
    // If it matches, call update
    
    AtlasServerHelper *atlasHelper = [[AtlasServerHelper alloc] init];
    self.updateAtlasListener = [[UpdateAtlasMetadataListener alloc] initWithDelegate:self];
    self.updateAtlasListener.updatedRobotAtlas = robotAtlas;
    atlasHelper.delegate = self.updateAtlasListener;
    [atlasHelper getAtlasDataForRobotWithId:robotAtlas.robotId];
}

-(void) atlasMetadataUpdated:(NeatoRobotAtlas *) robotAtlas
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(atlasMetadataUpdated:)])
        {
            [self.delegate performSelector:@selector(atlasMetadataUpdated:) withObject:robotAtlas];
        }
        self.retained_self = nil;
        self.delegate = nil;
    });
}


-(void) failedToUpdateAtlasMetadataWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailed:@selector(failedToUpdateAtlasMetadataWithError:) WithError:error];
}


@end
