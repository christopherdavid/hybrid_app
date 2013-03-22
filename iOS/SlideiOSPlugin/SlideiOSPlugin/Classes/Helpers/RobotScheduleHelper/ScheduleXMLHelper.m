#import "ScheduleXMLHelper.h"
#import "BasicScheduleEvent.h"
#import "LogHelper.h"
#import "DDXMLDocument.h"
#import "SchedulerConstants.h"
#import "Schedule.h"
#import "BasicScheduleEvent.h"
#import "ScheduleEvent.h"

#define XML_BASIC_SCHEDULE_EVENT @"<Schedule><ScheduleEventId>%@</ScheduleEventId><StartTime>%@:%@</StartTime><Day>%d</Day></Schedule>"

#define XML_NEW_SCHEDULE_GROUP @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><ScheduleGroup><ScheduleUUID>%@</ScheduleUUID>%@</ScheduleGroup>"


#define XML_SCHEDULE_GROUP @"<?xml version=\"1.0\" encoding=\"UTF-8\"?><ScheduleGroup>%@</ScheduleGroup>"

#define XML_DAY_SCHEDULE @"<Day>%d</Day>"

#define XML_SCHEDULE @"<Schedule>%@<StartTime>%@</StartTime><EndTime>%@</EndTime><EventType>%@</EventType><Area>%@</Area></Schedule>"

@implementation ScheduleXMLHelper

+ (NSString *)getXMlfromBasicScheduleEvent:(BasicScheduleEvent *)basicScheduleEvent {
    NSString *scheduleEventXML = [NSString stringWithFormat:XML_BASIC_SCHEDULE_EVENT,basicScheduleEvent.scheduleEventId,basicScheduleEvent.startTime.hrs,basicScheduleEvent.startTime.mins,basicScheduleEvent.day];
    return scheduleEventXML;
}

// TODO: Needs cleanup
+ (BasicScheduleEvent *)basicScheduleEventFromDDXMLElement:(DDXMLElement *)scheduleNode {
    debugLog(@"");
    NSError *err;
    BasicScheduleEvent *scheduleEvent = [[BasicScheduleEvent alloc] init];
    
    NSArray *scheduleEventIdArray = [scheduleNode nodesForXPath:@"ScheduleEventId" error:&err];
    scheduleEvent.scheduleEventId = [[scheduleEventIdArray lastObject] stringValue];
    
    NSArray *daysNodeArray = [scheduleNode nodesForXPath:@"Day" error:&err];
    NSString *day = [[daysNodeArray lastObject] stringValue];
    scheduleEvent.day = [day integerValue];
    
    NSArray *startTimeArray = [scheduleNode nodesForXPath:@"StartTime" error:&err];
    NSString *startTime = [[startTimeArray lastObject] stringValue];
    scheduleEvent.startTime = [[ScheduleTimeObject alloc] initWithString:startTime];
    
    scheduleEvent.xmlData = [self getXMlfromBasicScheduleEvent:scheduleEvent];
    return scheduleEvent;
}

+ (BasicScheduleEvent *)basicScheduleEventFromString:(NSString *)xmlData {
    NSError *err = nil;
    BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] init];
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithXMLString:xmlData options:0 error:&err];
    NSArray *scheduleEventIdArr = [document.rootElement nodesForXPath:@"//ScheduleEventId" error:&err];
    if([scheduleEventIdArr count] == 0) {
        debugLog(@"ERROR!!");
    }
    else {
        basicScheduleEvent.scheduleEventId = [[scheduleEventIdArr lastObject] stringValue];
    }
    NSArray *startTimeArr = [document.rootElement nodesForXPath:@"//StartTime" error:&err];
    if([startTimeArr count] == 0) {
        debugLog(@"ERROR!!");
    }
    else {
        NSString *startTime = [[startTimeArr lastObject] stringValue];
        basicScheduleEvent.startTime = [[ScheduleTimeObject alloc] initWithString:startTime];
    }
    NSArray *daysNodeArray = [document.rootElement nodesForXPath:@"Day" error:&err];
    if([daysNodeArray count] == 0) {
        debugLog(@"ERROR!!");
    }
    else {
        NSString *day = [[daysNodeArray lastObject] stringValue];
        basicScheduleEvent.day = [day integerValue];
    }
    return basicScheduleEvent;
}

+ (Schedule *)basicScheduleFromString:(NSString *)xmlData {
    NSError *error;
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithData:[xmlData dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
    if (error) {
        return nil;
    }
    Schedule *schedule = [[Schedule alloc] init];
    
    NSArray *scheduleIdArray = [document.rootElement nodesForXPath:@"//ScheduleUUID" error:&error];
    if([scheduleIdArray count] == 0) {
        return nil;
    }
    schedule.scheduleId = [[scheduleIdArray lastObject] stringValue];
    schedule.scheduleEvent = [[ScheduleEvent alloc] init];
    NSArray *basicScheduleEventArr = [document.rootElement nodesForXPath:@"//Schedule" error:&error];
    for (DDXMLElement *basicSchedulEvent in basicScheduleEventArr) {
        BasicScheduleEvent *basicEvent = [self basicScheduleEventFromDDXMLElement:basicSchedulEvent];
        [schedule.scheduleEvent addBasicScheduleEvent:basicEvent];
    }
    
    return schedule;
}

+ (NSDictionary *)advanceScheduleGroupFromXMLFile:(NSString *)filePath forScheduleId:(NSString *)scheduleId {
    NSError *err;
    NSMutableDictionary *dictObj = [[NSMutableDictionary alloc] init];
    
    NSMutableArray *scheduleGroup =[[NSMutableArray alloc] init];
    DDXMLDocument *document = [[DDXMLDocument alloc] initWithData:[NSData dataWithContentsOfFile:filePath] options:0 error:&err];
    NSArray *scheduleNodeArray = [document.rootElement nodesForXPath:@"//Schedule" error:&err];
    for(int i=0 ; i < [scheduleNodeArray count] ; i++)
    {
        DDXMLElement *scheduleNode = [scheduleNodeArray objectAtIndex:i];
        NSMutableDictionary *schedule = [[NSMutableDictionary alloc] init];
        NSMutableArray *days = [[NSMutableArray alloc] init];
        NSArray *daysNodeArray = [scheduleNode nodesForXPath:@"Day" error:&err];
        for(int j=0;j<[daysNodeArray count]; j++)
        {
            NSString *day = [[daysNodeArray objectAtIndex:j] stringValue];
            [days addObject:[NSNumber numberWithInteger:[day integerValue]]];
        }
        [schedule setObject:days forKey:KEY_DAY];
        NSArray *startTimeArray = [scheduleNode nodesForXPath:@"StartTime" error:&err];
        NSString *startTime = [[startTimeArray objectAtIndex:0] stringValue];
        [schedule setObject:startTime forKey:KEY_START_TIME];
        
        NSArray *endTimeArray = [scheduleNode nodesForXPath:@"EndTime" error:&err];
        NSString *endTime = [[endTimeArray objectAtIndex:0] stringValue];
        [schedule setObject:endTime forKey:KEY_END_TIME];
        
        NSArray *eventTypeArray = [scheduleNode nodesForXPath:@"EventType" error:&err];
        NSString *eventType = [[eventTypeArray objectAtIndex:0] stringValue];
        
        [schedule setObject:[NSNumber numberWithInteger:[eventType integerValue]] forKey:KEY_EVENT_TYPE];
        
        NSArray *areaArray = [scheduleNode nodesForXPath:@"Area" error:&err];
        NSString *area = [[areaArray objectAtIndex:0] stringValue];
        [schedule setObject:area forKey:KEY_AREA];
        [scheduleGroup addObject:schedule];
    }
    [dictObj setObject:scheduleId forKey:@"scheduleId"];
    [dictObj setObject:scheduleGroup forKey:@"schedules"];
    
    return dictObj;
}

+ (NSString *)xmlFromScheduleGroup:(NSArray *)scheduleGroup {
    debugLog(@"");
    NSMutableString *scheduleString = [[NSMutableString alloc] init];
    debugLog(@"Schedule group count is %d",[scheduleGroup count]);
    for(int i=0;i<[scheduleGroup count];i++) {
        NSDictionary *schedule = [scheduleGroup objectAtIndex:i];
        NSMutableString *dayString = [[NSMutableString alloc] init];
        NSArray *daysArray = [schedule objectForKey:KEY_DAY];
        for(int j=0;j<[daysArray count];j++)
        {
            [dayString appendString:[NSString stringWithFormat:XML_DAY_SCHEDULE,[[daysArray objectAtIndex:j] integerValue]]];
        }
        [scheduleString appendString:[NSString stringWithFormat:XML_SCHEDULE,dayString,[schedule objectForKey:KEY_START_TIME],[schedule objectForKey:KEY_END_TIME],[schedule objectForKey:KEY_EVENT_TYPE],[schedule objectForKey:KEY_AREA]]];
    }
    NSString *xmlScheduleGroup = [NSString stringWithFormat:XML_SCHEDULE_GROUP,scheduleString];
    debugLog(@"Generated xml is %@",xmlScheduleGroup);
    return xmlScheduleGroup;
}

+ (NSString *)getXmlDataFromSchedule:(Schedule *)schedule {
    NSMutableString *scheduleString = [[NSMutableString alloc] init];
    scheduleString = [NSString stringWithFormat:XML_NEW_SCHEDULE_GROUP, schedule.scheduleId, [ScheduleXMLHelper geScheduleEventsXMLFromSchedule:schedule]];
    
    return scheduleString;
}

+ (NSString *)geScheduleEventsXMLFromSchedule:(Schedule *)schedule {
    NSMutableString *scheduleEventString = [[NSMutableString alloc] init];
    for(int i=0 ; i < [schedule.scheduleEvent.basicScheduleEvents count] ; i++) {
        BasicScheduleEvent *scheduleEvent = [schedule.scheduleEvent.basicScheduleEvents objectAtIndex:i];
        [scheduleEventString appendString:scheduleEvent.xmlData];
    }
    return scheduleEventString;
}

@end
