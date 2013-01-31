#import <Foundation/Foundation.h>
#import "AtlasServerHelper.h"

@protocol RobotAtlasManagerProtocol <NSObject>

-(void) atlasMetadataUpdated:(NeatoRobotAtlas *) robotAtlas;
-(void) failedToUpdateAtlasMetadataWithError:(NSError *) error;

@end

@interface RobotAtlasManager : NSObject 

-(void) getAtlasMetadataForRobotWithId:(NSString *) robotId delegate:(id) delegate;
-(void) updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas delegate:(id) delegate;


@end
