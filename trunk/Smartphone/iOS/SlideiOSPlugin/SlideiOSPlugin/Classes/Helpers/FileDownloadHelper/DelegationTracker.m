#import "DelegationTracker.h"
#import "LogHelper.h"

static DelegationTracker *sharedInstance;
@interface DelegationTracker()
{

}

@property(nonatomic, retain) NSMutableDictionary *delegatesURlMap;


@end
@implementation DelegationTracker
@synthesize delegatesURlMap = _delegatesURlMap;

+(DelegationTracker *) getSharedDelegationTracker
{
    debugLog(@"");
    static dispatch_once_t pred = 0;
    dispatch_once(&pred, ^{
        sharedInstance = [[DelegationTracker alloc] init];
    });
    return sharedInstance;
}

-(id) init
{
    @synchronized(self)
    {
        if(self = [super init])
        {
            // Initialization code here.
            self.delegatesURlMap = [[NSMutableDictionary alloc] init];
        }
    }
    return self;
}

-(void) addDelegateToTracker:(DelegateWrapper *) delegate forUrl:(NSString *) url
{
    debugLog(@"");
    @synchronized(self)
    {
        NSMutableArray *delegateArr = [self.delegatesURlMap objectForKey:url];
        if (delegateArr == nil)
        {
            delegateArr = [[NSMutableArray alloc] init];
        }
        [delegateArr addObject:delegate];
        [self.delegatesURlMap setObject:delegateArr forKey:url];
    }
}


-(void) removeAllDelegatesFromTrackerForUrl:(NSString *) path;
{
    debugLog(@"");
    @synchronized(self)
    {
        [self.delegatesURlMap removeObjectForKey:path];
    }
}


-(NSArray *) getDelegatesForUrl:(NSString *) url
{
    debugLog(@"");
    @synchronized(self)
    {
        return [self.delegatesURlMap objectForKey:url];
    }
}


@end
