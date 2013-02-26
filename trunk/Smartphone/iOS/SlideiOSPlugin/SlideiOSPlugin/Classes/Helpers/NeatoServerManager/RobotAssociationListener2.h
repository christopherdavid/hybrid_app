#import <Foundation/Foundation.h>

@interface RobotAssociationListener2 : NSObject

@property(nonatomic, retain) NSString *userEmail;
@property(nonatomic, retain) NSString *robotId;

- (id)initWithDelegate:(id)delegate;
- (void)start;
@end
