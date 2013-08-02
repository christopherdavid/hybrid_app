#import <Foundation/Foundation.h>

@protocol IntendToDriveProtocol <NSObject>
- (void)intentToDriveRequestSuccededWithResult:(NSDictionary *)result;
- (void)intentToDriveRequestFailedWithError:(NSError *)error;
@end

@interface IntendToDriveHelper : NSObject

@property(nonatomic, weak) id<IntendToDriveProtocol> delegate;
- (void)requestIntentToDriveForRobotWithId:(NSString *)robotId;
@end
