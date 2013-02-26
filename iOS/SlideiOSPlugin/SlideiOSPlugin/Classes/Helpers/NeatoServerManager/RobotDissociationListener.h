

#import <Foundation/Foundation.h>

@interface RobotDissociationListener : NSObject

@property(nonatomic, retain) NSString *userEmail;
@property(nonatomic, retain) NSString *robotId;

- (id)initWithDelegate:(id)delegate;
- (void)start;

@end
