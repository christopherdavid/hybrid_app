#import "PostScheduleResult.h"
#import "NSDictionary+StringValueForKey.h"

#define SERVER_SCHEDULE_ID @"robot_schedule_id"
#define SERVER_SCHEDULE_ID_OTHER @"id"
#define XML_DATA_VERSION @"xml_data_version"
#define BLOB_DATA_VERSION @"blob_data_version"
#define SCHEDULE_TYPE @"schedule_type"

@implementation PostScheduleResult
@synthesize server_scheduleId = _server_scheduleId;
@synthesize xmlDataVersion = _xml_data_version;
@synthesize blobDataVersion = _blob_data_version;

- (id)initWithDictionary:(NSDictionary *)parameters {
    if ((self = [super init])) {
        if([parameters stringForKey:SERVER_SCHEDULE_ID]) {
            self.server_scheduleId = [parameters stringForKey:SERVER_SCHEDULE_ID];
        }
        if([parameters stringForKey:XML_DATA_VERSION]) {
            self.xmlDataVersion = [parameters stringForKey:XML_DATA_VERSION];
        }
        if([parameters stringForKey:BLOB_DATA_VERSION]) {
            self.blobDataVersion = [parameters stringForKey:BLOB_DATA_VERSION];
        }
    }
    return self;
}

@end
