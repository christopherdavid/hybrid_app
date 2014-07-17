#import <Foundation/Foundation.h>


@protocol RobotDriveManagerProtocol <NSObject>
- (void)driveRobotFailedWithError:(NSError *)error;
- (void)cancelIntendToDriveSucceded;
- (void)cancelIntendToDriveFailedWithError:(NSError *)error;
- (void)stopRobotDriveSucceded;
- (void)stopRobotDriveFailedWithError:(NSError *)error;
@end

@interface RobotDriveManager : NSObject
@property(nonatomic, weak) id<RobotDriveManagerProtocol> delegate;
- (void)robotWithRobotId:(NSString *)robotId isReadyToDriveWithIP:(NSString *)robotIp;
- (void)robotWithRobotId:(NSString *)robotId isNotAvailableToDriveWithErrorCode:(NSInteger)errorCode;
- (void)driveRobotWithRobotId:(NSString *)robotId navigationControlId:(NSString *)navigationControlId;
- (void)cancelIntendToDriveForRobotId:(NSString *)robotId;
- (void)stopDriveRobotForRobotId:(NSString *)robotId;
- (id)isConnectedOverTCPWithRobotId:(NSString *)robotId;
+ (id)canRequestDirectConnectionWithRobotId:(NSString *)robotId;
- (void)connectOverTCPWithRobotId:(NSString *)robotId ipAddress:(NSString *)ipAddress;
@end
