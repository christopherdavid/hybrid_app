#import <Foundation/Foundation.h>

@interface AtlasGridMetadata : NSObject

@property(nonatomic, retain) NSString *gridId;
@property(nonatomic, retain) NSString *blobFileUrl;
@property(nonatomic, retain) NSString *version;
@property(nonatomic, retain) NSString *atlasId;
@property(nonatomic, retain) NSString *gridCachePath;

-(id) initWithDictionary:(NSDictionary *) gridData;

@end
