#import <Foundation/Foundation.h>

@interface RobotAssociationListener : NSObject

-(id) initWithDelegate:(id) delegate;

@property(nonatomic, retain) NSString *associatedRobotId;

@end
