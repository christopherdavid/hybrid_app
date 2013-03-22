#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class NeatoRobotEntity, NeatoSocialNetworksEntity;

@interface NeatoUserEntity : NSManagedObject

@property (nonatomic, retain) NSString * account_type;
@property (nonatomic, retain) NSString * chatId;
@property (nonatomic, retain) NSString * chatPassword;
@property (nonatomic, retain) NSString * email;
@property (nonatomic, retain) NSString * external_social_id;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * password;
@property (nonatomic, retain) NSString * userId;
@property (nonatomic, retain) NSSet *hasRobots;
@property (nonatomic, retain) NSSet *hasSocialNetowrks;
@end

@interface NeatoUserEntity (CoreDataGeneratedAccessors)

- (void)addHasRobotsObject:(NeatoRobotEntity *)value;
- (void)removeHasRobotsObject:(NeatoRobotEntity *)value;
- (void)addHasRobots:(NSSet *)values;
- (void)removeHasRobots:(NSSet *)values;

- (void)addHasSocialNetowrksObject:(NeatoSocialNetworksEntity *)value;
- (void)removeHasSocialNetowrksObject:(NeatoSocialNetworksEntity *)value;
- (void)addHasSocialNetowrks:(NSSet *)values;
- (void)removeHasSocialNetowrks:(NSSet *)values;

@end
