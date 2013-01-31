#import "NeatoDataStore.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "CommandTrackerEntity.h"

#define ENTITY_COMMAND_TRACKER @"CommandTrackerEntity"
#define NEATO_DATA_STORE_NAME @"NeatoDatastore.sqlite"
#define DATA_STORE_BUNDLE @"NeatoDataStore"

#define TIME_BEFORE_AUTO_REMOVAL 1800


#define PREDICATE_COMMAND_FOR_REQUEST @"(requestId = %@)"
#define PREDICATE_COMMAND_OLDER_THAN @"(creationTime < %@)"
static NeatoDataStore *sharedInstance;
@interface NeatoDataStore()
{

}

@property (nonatomic, retain) NSManagedObjectModel *managedObjectModel;
@property (nonatomic, retain) NSManagedObjectContext *managedObjectContext;
@property (nonatomic, retain) NSPersistentStoreCoordinator *persistentStoreCoordinator;

-(void) createManagedContext;
-(NSManagedObjectModel *) createManagedObjectModel;
-(NSPersistentStoreCoordinator *) createPersistenceStoreCoordinator;
-(CommandTrackerEntity *) getCommandTrackerEntityForRequestId:(NSString *) requestId;
-(void) deleteOlderCommands;
-(NSArray *) getCommandsOlderThanMins:(int) limit;

@end

@implementation NeatoDataStore
@synthesize managedObjectModel = _managedObjectModel;
@synthesize managedObjectContext = _managedObjectContext;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;



+(NeatoDataStore *) sharedNeatoDataStore
{
    debugLog(@"");
    @synchronized(self)
    {
        if(sharedInstance == nil) {
            sharedInstance =  [[self alloc] init];
        }
    }
    
    return sharedInstance;
}

-(id) init
{
    @synchronized(self)
    {
        if(self = [super init])
        {
            // Initialization code here.
            [self createManagedContext];
        }
    }
    
    return self;
}

-(NSManagedObjectModel *) createManagedObjectModel
{
    debugLog(@"");
    if (self.managedObjectModel != nil) {
        return self.managedObjectModel;
    }
    NSBundle *bundle = [NSBundle bundleWithURL:[[NSBundle mainBundle] URLForResource:DATA_STORE_BUNDLE withExtension:@"bundle"]];
    
    self.managedObjectModel = [NSManagedObjectModel mergedModelFromBundles:[[NSArray alloc] initWithObjects:bundle, nil]];
    return self.managedObjectModel;
}

-(NSPersistentStoreCoordinator *) createPersistenceStoreCoordinator
{
    debugLog(@"");
    if (self.persistentStoreCoordinator != nil) {
        return self.persistentStoreCoordinator;
    }
	
    NSURL *storeUrl = [[AppHelper getLocalCachePath] URLByAppendingPathComponent: NEATO_DATA_STORE_NAME];
	
	NSError *error;
    self.persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel: [self createManagedObjectModel]];
    if (![self.persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeUrl options:nil error:&error]) {
        return nil;
    }
	
    return self.persistentStoreCoordinator;
}

-(void) createManagedContext
{
    debugLog(@"");
    if (self.managedObjectContext != nil) {
        return;
    }
	
    NSPersistentStoreCoordinator *coordinator = [self createPersistenceStoreCoordinator];
    if (coordinator != nil) {
        self.managedObjectContext = [[NSManagedObjectContext alloc] init];
        [self.managedObjectContext setPersistentStoreCoordinator: coordinator];
    }
    else
    {
        debugLog(@"Could not create persistent store coordinator!!");
        self.managedObjectContext = nil;
    }
}

-(NSArray *) getCommandsOlderThanMins:(int) limit
{
    debugLog(@"");
    if (self.managedObjectContext)
    {
        NSEntityDescription *entityDesc = [NSEntityDescription entityForName:ENTITY_COMMAND_TRACKER
                                                      inManagedObjectContext:self.managedObjectContext];
        
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setEntity:entityDesc];
        
        NSPredicate *pred = [NSPredicate predicateWithFormat:PREDICATE_COMMAND_OLDER_THAN,
                             [NSDate dateWithTimeIntervalSinceNow:(-1 * limit)]];
        [request setPredicate:pred];
        
        NSError *error = nil;
        NSArray *objects = [self.managedObjectContext executeFetchRequest:request
                                                                    error:&error];
        if (error)
        {
            debugLog(@"deleteOlderCommands failed with error = %@", error);
            return nil;
        }
        return objects;
    }
    else
    {
        return nil;
    }
}

// Deletes commands that are older than 30 mins
// This is to prevent the DB from getting excessively big, as normally we remove the commands
// only when the robot responds to them.
-(void) deleteOlderCommands
{
    debugLog(@"");
    if (self.managedObjectContext)
    {
       
        NSArray *objects = [self getCommandsOlderThanMins:TIME_BEFORE_AUTO_REMOVAL];
        debugLog(@"Older commands to delete : %d", [objects count]);
        for (NSManagedObject *entity in objects) {
            [self.managedObjectContext deleteObject:entity];
        }
        NSError *saveError = nil;
        [self.managedObjectContext save:&saveError];
        if (saveError)
        {
            debugLog(@"Could not delete older commands. Error = %@", saveError);
        }
        
    }
}

-(BOOL) addCommandToTracker:(NSString *) xmlCommand withRequestId:(NSString *) requestId
{
    @synchronized(self)
    {
        if ([AppHelper isStringNilOrEmpty:xmlCommand] || [AppHelper isStringNilOrEmpty:requestId])
        {
            debugLog(@"xmlCommand or requestId cannot be nil!. Will not add command to tracker.");
            return NO;
        }
        
        if (self.managedObjectContext)
        {
            // We should remove older commands for which we never receved any response from
            // robot. Calling the method to remove older commands from here for now.
            // This may change later. This seems logical place for now, otherwise we would
            // have to write a timer and repeat it.
            [self deleteOlderCommands];
            
            /*
             //Debuging code:
             NSLog(@"Context: %@",self.managedObjectContext);
             NSLog(@"PS Coord : %@",self.persistentStoreCoordinator);
             NSLog(@"MOM : %@", self.managedObjectModel);
             NSLog(@"Entities : %@",[[self.managedObjectModel entities] valueForKey:@"name"]);
             */
            
            CommandTrackerEntity *entity = [self getCommandTrackerEntityForRequestId:requestId];
            if (entity == nil)
            {
                entity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_COMMAND_TRACKER inManagedObjectContext:self.managedObjectContext];
                entity.requestId = requestId;
            }
            else
            {
                 debugLog(@"Command entity exists for requestId = %@. Will overwrite command data!", requestId);
            }
            entity.xmlCommand = xmlCommand;
            entity.creationTime = [NSDate date];
            
            NSError *saveError = nil;
            [self.managedObjectContext save:&saveError];
            if (saveError)
            {
                debugLog(@"Could not persist CommandTrackerEntity for requestId = %@!!. Error = %@", requestId, saveError);
                return NO;
            }
            return YES;
        }
        else
        {
            debugLog(@"managedObjectContext not created. addCommandToTracker failed!");
            return NO;
        }
    }
}

-(BOOL) removeCommandForRequestId:(NSString *) requestId
{
    @synchronized(self)
    {
        if ([AppHelper isStringNilOrEmpty:requestId])
        {
            debugLog(@"requestId cannot be nil! Will not remove command!");
            return NO;
        }
        
        if (self.managedObjectContext)
        {
            CommandTrackerEntity *entity = [self getCommandTrackerEntityForRequestId:requestId];
            if (entity == nil)
            {
                return NO;
            }
            [self.managedObjectContext deleteObject:entity];
            NSError *saveError = nil;
            [self.managedObjectContext save:&saveError];
            if (saveError)
            {
                debugLog(@"Could not delete CommandTrackerEntity for requestId = %@!!. Error = %@", requestId, saveError);
                return NO;
            }
            debugLog(@"Command entity for requestId = [%@] DELETED from data store!", requestId);
            return YES;
        }
        else
        {
            debugLog(@"managedObjectContext not created. removeCommandForRequestId failed!");
            return NO;
        }
    }
}

-(CommandTrackerEntity *) getCommandTrackerEntityForRequestId:(NSString *) requestId
{
    debugLog(@"");
    if (self.managedObjectContext)
    {
        NSEntityDescription *entityDesc = [NSEntityDescription entityForName:ENTITY_COMMAND_TRACKER
                                                      inManagedObjectContext:self.managedObjectContext];
        
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setEntity:entityDesc];
        
        NSPredicate *pred = [NSPredicate predicateWithFormat:PREDICATE_COMMAND_FOR_REQUEST,
                             requestId];
        [request setPredicate:pred];
        
        NSError *error = nil;
        NSArray *objects = [self.managedObjectContext executeFetchRequest:request
                                                                    error:&error];
        if (error)
        {
            debugLog(@"Error in getCommandForRequestId: %@", error);
            return nil;
        }
        if ([objects count] == 0)
        {
            debugLog(@"No commands found matching requestId = %@", requestId);
            return nil;
        }
        CommandTrackerEntity *tracker = (CommandTrackerEntity *) [objects objectAtIndex:0];
        return tracker;
    }
    else
    {
        return nil;
    }
}

-(NSString *) getCommandForRequestId:(NSString *) requestId
{
    @synchronized(self)
    {
        if ([AppHelper isStringNilOrEmpty:requestId])
        {
            debugLog(@"requestId cannot be nil!");
            return nil;
        }
        
        if (self.managedObjectContext)
        {
            CommandTrackerEntity *tracker = [self getCommandTrackerEntityForRequestId:requestId];
            if (tracker == nil)
            {
                return nil;
            }
            debugLog(@"Found command = %@", tracker.xmlCommand);
            return tracker.xmlCommand;
        }
        else
        {
            debugLog(@"managedObjectContext not created. DB operation failed!");
            return nil;
        }
    }
}

@end
