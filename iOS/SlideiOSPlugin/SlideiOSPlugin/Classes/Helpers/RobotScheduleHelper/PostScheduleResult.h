#import <Foundation/Foundation.h>

@interface PostScheduleResult : NSObject
@property(nonatomic, retain) NSString *server_scheduleId;
@property(nonatomic, retain) NSString *xmlDataVersion;
@property(nonatomic, retain) NSString *blobDataVersion;

- (id)initWithDictionary:(NSDictionary *)parameters;
@end
