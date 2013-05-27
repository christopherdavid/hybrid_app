#import <Foundation/Foundation.h>

@interface PostScheduleResult : NSObject
@property(nonatomic, retain) NSString *serverScheduleId;
@property(nonatomic, retain) NSString *scheduleVersion;
@property(nonatomic, retain) NSString *blobDataVersion;

- (id)initWithDictionary:(NSDictionary *)parameters;
@end
