#import "CommandTracker.h"
#import "LogHelper.h"
#import "NeatoDataStore.h"

@interface CommandTracker()
{
    
}


@end
@implementation CommandTracker

-(id) init
{
    @synchronized(self)
    {
        if(self = [super init])
        {
            // Initialization code here.
        }
    }
    return self;
}

-(BOOL) addCommandToTracker:(NSString *) xmlCommand withRequestId:(NSString *) requestId
{
    debugLog(@"");
    @synchronized(self)
    {
        return [[NeatoDataStore sharedNeatoDataStore] addCommandToTracker:xmlCommand withRequestId:requestId];
    }
}


-(BOOL) removeCommandForRequestId:(NSString *) requestId
{
    debugLog(@"");
    @synchronized(self)
    {
        return [[NeatoDataStore sharedNeatoDataStore] removeCommandForRequestId:requestId];
    }
}


-(NSString *) getCommandForRequestId:(NSString *) requestId
{
    debugLog(@"");
    @synchronized(self)
    {
        return [[NeatoDataStore sharedNeatoDataStore] getCommandForRequestId:requestId];
    }
}

@end
