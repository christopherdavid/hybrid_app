#import <Foundation/Foundation.h>
#import "NeatoRobotAtlas.h"

@interface UpdateAtlasMetadataListener : NSObject

@property(nonatomic, retain) NeatoRobotAtlas *updatedRobotAtlas;

-(id) initWithDelegate:(id) delegate;
@end
