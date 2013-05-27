#import "ScheduleDBHelper.h"
#import "NeatoDataStore.h"

@implementation ScheduleDBHelper

+ (id)createScheduleForRobotId:(NSString *)robotId ofScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database createScheduleForRobotId:robotId forScheduleType:scheduleType withScheduleId:scheduleId];
}

+ (id)scheduleTypeForScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database scheduleTypeForScheduleId:scheduleId];
}

+ (id)addBasicScheduleEventData:(NSString *)data withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database addBasicScheduleEventData:data withScheduleEventId:scheduleEventId forScheduleId:scheduleId];
}

+ (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withData:(NSString *)xmlData {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database updateBasicScheduleEventWithId:scheduleEventId withData:xmlData];
}

+ (id)deleteBasicSchedleEventWithId:(NSString *)scheduleEventId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database deleteBasicScheduleEventWithId:scheduleEventId];
}

+ (id)basicScheduleEventDataWithId:(NSString *)scheduleEventId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database basicScheduleEventWithId:scheduleEventId];
}

+ (id)basicScheduleForScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database basicScheduleForScheduleId:scheduleId];
}

+ (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    [database saveSchedule:schedule ofType:scheduleType forRobotWithId:robotId];
}


+ (id)robotIdForScheduleId:(NSString *)scheduleId {
    NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database robotIdForScheduleId:scheduleId];
}

+ (id)updateServerScheduleId:(NSString *)serverScheduleId andScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId {
     NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database updateServerScheduleId:serverScheduleId andScheduleVersion:scheduleVersion forScheduleWithScheduleId:scheduleId];
}

+ (id)updateScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId {
     NeatoDataStore *database = [NeatoDataStore sharedNeatoDataStore];
    return [database updateScheduleVersion:scheduleVersion forScheduleWithScheduleId:scheduleId];
}

@end
