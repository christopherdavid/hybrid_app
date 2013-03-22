#import "NeatoRobotAtlas.h"
#import "NeatoConstants.h"

@implementation NeatoRobotAtlas
@synthesize atlasId = _atlasId, version = _version, xmlDataUrl = _xmlDataUrl,atlasMetadata = _atlasMetadata, robotId = _robotId;

-(id) initWithDictionary:(NSDictionary *) dictionary
{
    if ((self = [super init]))
    {
        self.atlasId = [dictionary valueForKey:NEATO_RESPONSE_ATLAS_ID];
        self.xmlDataUrl = [dictionary valueForKey:NEATO_RESPONSE_XML_DATA_URL];
        self.version = [dictionary valueForKey:NEATO_RESPONSE_ATLAS_VERSION];
    }
    return self;
}

@end
