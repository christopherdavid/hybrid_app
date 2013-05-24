#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoUserEntity;

@interface NeatoNotificationEntity : NSManagedObject

@property (nonatomic, retain) NSString * notificationId;
@property (nonatomic, retain) NSString * notificationValue;
@property (nonatomic, retain) NeatoUserEntity *ofUser;

@end
