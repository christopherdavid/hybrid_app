#import <Foundation/Foundation.h>

@interface NeatoRobotAtlas : NSObject

@property(nonatomic, retain) NSString *atlasId;
@property(nonatomic, retain) NSString *xmlDataUrl;
@property(nonatomic, retain) NSString *version;
@property(nonatomic, retain) NSString *robotId;
@property(nonatomic, retain) NSString *atlasMetadata;

-(id) initWithDictionary:(NSDictionary *) dictionary;
@end
