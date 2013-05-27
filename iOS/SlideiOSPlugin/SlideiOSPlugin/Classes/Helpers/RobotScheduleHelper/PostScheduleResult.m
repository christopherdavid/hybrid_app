#import "PostScheduleResult.h"
#import "NSDictionary+StringValueForKey.h"

#define SERVER_SCHEDULE_ID @"robot_schedule_id"
#define SERVER_SCHEDULE_ID_OTHER @"id"
#define SCHEDULE_VERSION @"schedule_version"
#define BLOB_DATA_VERSION @"blob_data_version"
#define SCHEDULE_TYPE @"schedule_type"

@implementation PostScheduleResult
@synthesize serverScheduleId = _serverScheduleId;
@synthesize scheduleVersion = _scheduleVersion;
@synthesize blobDataVersion = _blobDataVersion;

- (id)initWithDictionary:(NSDictionary *)parameters {
    if ((self = [super init])) {
        if([parameters stringForKey:SERVER_SCHEDULE_ID]) {
            self.serverScheduleId = [parameters stringForKey:SERVER_SCHEDULE_ID];
        }
        if([parameters stringForKey:SCHEDULE_VERSION]) {
            self.scheduleVersion = [parameters stringForKey:SCHEDULE_VERSION];
        }
        if([parameters stringForKey:BLOB_DATA_VERSION]) {
            self.blobDataVersion = [parameters stringForKey:BLOB_DATA_VERSION];
        }
    }
    return self;
}

@end
