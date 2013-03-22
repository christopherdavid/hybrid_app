#import <Foundation/Foundation.h>
#import "AtlasGridMetadata.h"

@protocol AltasGridManagerProtocol <NSObject>
-(void) gotAtlasGridMetadata:(AtlasGridMetadata *) atlasGridMetadata;
-(void) getAtlasGridMetadataFailed:(NSError *) error;
@end

@interface AtlasGridManager : NSObject
@property(nonatomic, weak) id delegate;
-(void) getAtlasGridMetadata:(NSString *) robotId gridId:(NSString *) gridId;
@end
