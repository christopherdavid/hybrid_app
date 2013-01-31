#import "UpdateAtlasMetadataListener.h"
#import "LogHelper.h"
#import "AtlasServerHelper.h"
#import "GetAtlasDataListener.h"

@interface UpdateAtlasMetadataListener()
{

}

@property(nonatomic, retain) UpdateAtlasMetadataListener *retained_self;
@property(nonatomic, retain)  GetAtlasDataListener *atlasDataListener;
@property(nonatomic, weak) id delegate;
@end
@implementation UpdateAtlasMetadataListener
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;
@synthesize updatedRobotAtlas = _updatedRobotAtlas;
@synthesize atlasDataListener = _atlasDataListener;

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
    if([self.delegate respondsToSelector:@selector(failedToUpdateAtlasMetadataWithError:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(failedToUpdateAtlasMetadataWithError:) withObject:error];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

-(void) gotAtlasData:(NeatoRobotAtlas *)robotAtlas
{
    debugLog(@"Atlas Id = %@, Atlas xml url = %@", robotAtlas.atlasId, robotAtlas.xmlDataUrl);
    
    // TODO: Get the current atlas version from DB and check if that matches the latest
    // we just got from server
    // If the two versions do not match, we should not call update API
    
    
    // Set the atlas Id to updated robotAtlas object
    self.updatedRobotAtlas.atlasId = robotAtlas.atlasId;
    
    // TODO: DUMMY code. This code is just a stop-gap arrangement till we implement a local DB
    // We should check if the two versions are same and let the user update the metadata only
    // if has the latest version.
    self.updatedRobotAtlas.version = robotAtlas.version;
    
    AtlasServerHelper *helper = [[AtlasServerHelper alloc] init];
    helper.delegate = self;
    [helper updateRobotAtlasData:self.updatedRobotAtlas];
}

-(void) atlasMetadataUpdated:(NSString *) message
{
    debugLog(@"");
    // When the data updates at the server, the response is a stupid message saying 'data updated'
    // It should have returned the latest data but as it doesnt we have to make one more get data request
   
    AtlasServerHelper *helper = [[AtlasServerHelper alloc] init];
    self.atlasDataListener = [[GetAtlasDataListener alloc] initWithDelegate:self];
    helper.delegate = self.atlasDataListener;
    [helper getAtlasDataForRobotWithId:self.updatedRobotAtlas.robotId];
}


-(void) failedToUpdateAtlasMetadataWithError:(NSError *) error
{
    debugLog(@"");
    [self notifyRequestFailedWithError:error];
}

-(void) gotLatestAtlasData:(NeatoRobotAtlas *)robotAtlas
{
    debugLog(@"");
    if([self.delegate respondsToSelector:@selector(atlasMetadataUpdated:)])
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate performSelector:@selector(atlasMetadataUpdated:) withObject:robotAtlas];
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
}

@end
