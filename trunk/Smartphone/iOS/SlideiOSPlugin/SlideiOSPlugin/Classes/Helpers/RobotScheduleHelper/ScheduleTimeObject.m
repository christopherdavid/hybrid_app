
#import "ScheduleTimeObject.h"
#import "LogHelper.h"

@interface ScheduleTimeObject()
-(void) extractTimeFromString:(NSString *)time;
@end
@implementation ScheduleTimeObject

@synthesize hrs = _hrs;
@synthesize mins = _mins;

-(id) initWithString:(NSString *)time
{
    @synchronized(self)
    {
        if((self = [super init]))
        {
            [self extractTimeFromString:time];
        }
    }
    return self;
}

-(id) initWithHrs:(NSString *)hrs andMins:(NSString *)mins
{
    @synchronized(self)
    {
        if((self = [super init]))
        {
            self.hrs = hrs;
            self.mins = mins;
        }
    }
    return self;
}

-(void) extractTimeFromString:(NSString *)time
{
    @try
    {
        NSRange range = [time rangeOfString:@":"];
        self.hrs = [NSString stringWithString:[time substringToIndex:range.location]];
        self.mins = [NSString stringWithString:[time substringFromIndex:(range.location+1)]];
    }
    @catch (NSException *exception)
    {
        debugLog(@"there is an exception while extracting time from string %@",[NSException description]);
    }
}

-(NSString *)toString {
    return [NSString stringWithFormat:@"%@:%@",self.hrs,self.mins];
}
@end
