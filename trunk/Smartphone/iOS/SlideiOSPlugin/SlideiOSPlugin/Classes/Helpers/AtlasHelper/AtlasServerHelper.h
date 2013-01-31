#import <Foundation/Foundation.h>
#import "NeatoRobotAtlas.h"

@protocol AtlasServerHelperProtocol <NSObject>

-(void) getAtlasDataFailed:(NSError *) error;
-(void) gotAtlasData:(NeatoRobotAtlas *) robotAtlas;

-(void) gotAtlasGridMetadata:(NSArray *) gridMetadaArr;
-(void) getAtlasGridMetadataFailed:(NSError *) error;

-(void) atlasMetadataUpdated:(NSString *) message;
-(void) failedToUpdateAtlasMetadataWithError:(NSError *) error;

-(void) atlasGridsDeleted:(NSString *) message;
-(void) failedToDeleteAtlasGridsWithError:(NSError *) error;

@end

@interface AtlasServerHelper : NSObject

@property(nonatomic, weak) id delegate;
-(void) getAtlasDataForRobotWithId:(NSString *) robotId;
-(void) getAtlasGridMetadata:(NSString *) atlasId;
-(void) updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas;
-(void) deleteRobotAtlasData:(NSString *) atalsId;

@end
