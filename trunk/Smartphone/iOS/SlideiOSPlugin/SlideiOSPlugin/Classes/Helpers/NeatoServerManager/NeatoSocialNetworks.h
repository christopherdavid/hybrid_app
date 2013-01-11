#import <Foundation/Foundation.h>

@interface NeatoSocialNetworks : NSObject

@property(nonatomic, retain) NSString *provider;
@property(nonatomic, retain) NSString *externalSocialId;

-(id) initWithDictionary:(NSDictionary *) data;

@end
