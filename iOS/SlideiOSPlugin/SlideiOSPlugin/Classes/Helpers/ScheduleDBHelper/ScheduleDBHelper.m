#import "ScheduleDBHelper.h"
#import "NeatoDataStore.h"

@implementation ScheduleDBHelper

+ (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database createScheduleForRobotId:robotId forScheduleType:scheduleType withScheduleId:scheduleId];
}

+ (id)getScheduleTypeForScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database getScheduleTypeForScheduleId:scheduleId];
}

+ (id)addBasicScheduleEventData:(NSString *)xmlData withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database addBasicScheduleEventData:xmlData withScheduleEventId:scheduleEventId forScheduleId:scheduleId];
}

+ (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withXMLData:(NSString *)xmlData {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database updateBasicScheduleEventWithId:scheduleEventId withXMLData:xmlData];
}

+ (id)deleteBasicSchedleEventWithId:(NSString *)scheduleEventId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database deleteBasicScheduleEventWithId:scheduleEventId];
}

+ (id)getBasicScheduleEventDataWithId:(NSString *)scheduleEventId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database getBasicScheduleEventWithId:scheduleEventId];
}

+ (id)getBasicScheduleForScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database getBasicScheduleForScheduleId:scheduleId];
}

+ (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    [database saveSchedule:schedule ofType:scheduleType forRobotWithId:robotId];
}


+ (id)getRobotIdForScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database getRobotIdForScheduleId:scheduleId];
}

+ (id)updateScheduleWithScheduleId:(NSString *)scheduleId withServerScheduleId:(NSString *)server_scheduleId andXmlDataVersion:(NSString *)xml_data_version {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database updateScheduleWithScheduleId:scheduleId withServerScheduleId:server_scheduleId andXmlDataVersion:xml_data_version];
}

+ (id)updateScheduleWithScheduleId:(NSString *)scheduleId forXmlDataVersion:(NSString *)xml_data_version {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database updateScheduleWithScheduleId:scheduleId forXmlDataVersion:xml_data_version];
}

@end
