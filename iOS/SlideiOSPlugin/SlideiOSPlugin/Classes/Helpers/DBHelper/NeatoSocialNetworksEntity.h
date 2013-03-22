#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoUserEntity;

@interface NeatoSocialNetworksEntity : NSManagedObject

@property (nonatomic, retain) NSString * externalSocialId;
@property (nonatomic, retain) NSString * provider;
@property (nonatomic, retain) NSString * userId;
@property (nonatomic, retain) NeatoUserEntity *socialNetworkOfUser;

@end
