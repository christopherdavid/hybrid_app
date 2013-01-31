#import "AtlasGridMetadata.h"
#import "LogHelper.h"
#import "NeatoConstants.h"

@implementation AtlasGridMetadata
@synthesize blobFileUrl = _blobFileUrl;
@synthesize gridId = _gridId;
@synthesize version = _version;
@synthesize gridCachePath = _gridCachePath;
@synthesize atlasId = _atlasId;

-(id) initWithDictionary:(NSDictionary *) gridData
{
    debugLog(@"");
    if ((self = [super init]))
    {
        self.gridId = [gridData valueForKey:NEATO_RESPONSE_GRID_ID];
        self.blobFileUrl = [gridData valueForKey:NEATO_RESPONSE_GRID_BLOB_FILE_URL];
        self.version = [gridData valueForKey:NEATO_RESPONSE_GRID_VERSION];
    }
    return self;
}

@end
