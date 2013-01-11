#import "NeatoSocialNetworks.h"

@implementation NeatoSocialNetworks
@synthesize externalSocialId = _externalSocialId,provider = _provider;

-(id) initWithDictionary:(NSDictionary *) data
{
    id obj = [super init];
    if (obj)
    {
        self.provider = [data valueForKey:@"provider"];
        self.externalSocialId = [data valueForKey:@"external_social_id"];
        return obj;
    }
    return nil;
}

@end
