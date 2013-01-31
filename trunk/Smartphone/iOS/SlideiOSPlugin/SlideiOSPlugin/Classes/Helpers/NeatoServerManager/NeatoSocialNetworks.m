#import "NeatoSocialNetworks.h"

@implementation NeatoSocialNetworks
@synthesize externalSocialId = _externalSocialId,provider = _provider;

-(id) initWithDictionary:(NSDictionary *) data
{
    if ((self = [super init]))
    {
        self.provider = [data valueForKey:@"provider"];
        self.externalSocialId = [data valueForKey:@"external_social_id"];
    }
    return self;
}

@end
