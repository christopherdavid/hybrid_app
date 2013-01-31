#import "GetAtlasDataListener.h"
#import "LogHelper.h"
#import "NeatoRobotAtlas.h"

@interface GetAtlasDataListener()

@property(nonatomic, retain) GetAtlasDataListener *retained_self;
@property(nonatomic, weak) id delegate;

@end
@implementation GetAtlasDataListener

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
    [self.delegate performSelector:@selector(failedToUpdateAtlasMetadataWithError:) withObject:error];
    self.retained_self = nil;
}


-(void) gotAtlasData:(NeatoRobotAtlas *) robotAtlas
{
    [self.delegate performSelector:@selector(gotLatestAtlasData:) withObject:robotAtlas];
    self.retained_self = nil;
}

@end
