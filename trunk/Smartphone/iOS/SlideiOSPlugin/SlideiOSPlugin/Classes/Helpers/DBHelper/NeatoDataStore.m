#import "NeatoDataStore.h"
#import "LogHelper.h"
#import "AppHelper.h"
#import "CommandTrackerEntity.h"
#import "NeatoUserEntity.h"
#import "NeatoRobotEntity.h"
#import "NeatoSocialNetworksEntity.h"
#import "LogHelper.h"
#import "NeatoRobot.h"
#import "NeatoConstants.h"
#import "ScheduleEntity.h"
#import "ScheduleEventsEntity.h"
#import "BasicScheduleEventEntity.h"
#import "BasicScheduleEvent.h"
#import "Schedule.h"
#import "ScheduleEvent.h"
#import "AppHelper.h"
#import "BasicScheduleEvent.h"
#import "NeatoNotificationEntity.h"
#import "CleaningAreaEntity.h"
#import "XMPPCallbackEntity.h"

#define ENTITY_COMMAND_TRACKER @"CommandTrackerEntity"
#define NEATO_DATA_STORE_NAME @"NeatoDatastore.sqlite"
#define DATA_STORE_BUNDLE @"NeatoDataStore"
#define ENTITY_NEATO_USER @"NeatoUserEntity"
#define ENTITY_NEATO_ROBOT @"NeatoRobotEntity"
#define ENTITY_NEATO_SOCIALNETWORKS @"NeatoSocialNetworksEntity"
#define ENTITY_NEATO_NOTIFICATION @"NeatoNotificationEntity"
#define ENTITY_CLEANING_AREA @"CleaningAreaEntity"
#define ENTITY_NEATO_CALLBACKID @"XMPPCallbackEntity"

//Schedule Entities
#define ENTITY_SCHEDULE @"ScheduleEntity"
#define ENTITY_SCHEDULE_EVENTS @"ScheduleEventsEntity"
#define ENTITY_BASIC_SCHEDULE_EVENT @"BasicScheduleEventEntity"


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
- (void)insertOrUpdateXMPPCallbackId:(NSString *)xmppCallbackId;

@end

@implementation NeatoDataStore
@synthesize managedObjectModel = _managedObjectModel;
@synthesize managedObjectContext = _managedObjectContext;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;



+ (NeatoDataStore *)sharedNeatoDataStore {
    debugLog(@"");
    @synchronized(self) {
        if(sharedInstance == nil) {
            sharedInstance =  [[self alloc] init];
        }
    }
    
    return sharedInstance;
}

- (id)init {
    @synchronized(self) {
        if(self = [super init]) {
            // Initialization code here.
            [self createManagedContext];
        }
    }
    
    return self;
}

- (NSManagedObjectModel *)createManagedObjectModel {
    debugLog(@"");
    if (self.managedObjectModel != nil) {
        return self.managedObjectModel;
    }
    NSBundle *bundle = [NSBundle bundleWithURL:[[NSBundle mainBundle] URLForResource:DATA_STORE_BUNDLE withExtension:@"bundle"]];
    
    self.managedObjectModel = [NSManagedObjectModel mergedModelFromBundles:[[NSArray alloc] initWithObjects:bundle, nil]];
    return self.managedObjectModel;
}

- (NSPersistentStoreCoordinator *)createPersistenceStoreCoordinator {
    debugLog(@"");
    if (self.persistentStoreCoordinator != nil) {
        return self.persistentStoreCoordinator;
    }
	
    NSURL *storeUrl = [[AppHelper getLocalCachePath] URLByAppendingPathComponent: NEATO_DATA_STORE_NAME];
	
	NSError *error;
    self.persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel: [self createManagedObjectModel]];
    
    // TODO: Migrating older entities to newer MOM file.
    // http://stackoverflow.com/questions/1091228/i-keep-on-getting-save-operation-failure-after-any-change-on-my-xcode-data-mod 
    
    NSDictionary *options = [NSDictionary dictionaryWithObjectsAndKeys:
    						 [NSNumber numberWithBool:YES], NSMigratePersistentStoresAutomaticallyOption,
    						 [NSNumber numberWithBool:YES], NSInferMappingModelAutomaticallyOption, nil];
    if (![self.persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeUrl options:options error:&error]) {
        debugLog(@"Failed to create PersistentStoreCoordinator!! %@", [error description]);
        return nil;
    }
	
    return self.persistentStoreCoordinator;
}

- (void)createManagedContext {
    debugLog(@"");
    if (self.managedObjectContext != nil) {
        return;
    }
	
    NSPersistentStoreCoordinator *coordinator = [self createPersistenceStoreCoordinator];
    if (coordinator != nil) {
        self.managedObjectContext = [[NSManagedObjectContext alloc] init];
        [self.managedObjectContext setPersistentStoreCoordinator: coordinator];
    }
    else {
        debugLog(@"Could not create persistent store coordinator!!");
        self.managedObjectContext = nil;
    }
}

- (NSArray *)getCommandsOlderThanMins:(int)limit {
    debugLog(@"");
    if (self.managedObjectContext) {
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
        if (error) {
            debugLog(@"deleteOlderCommands failed with error = %@", error);
            return nil;
        }
        return objects;
    }
    else {
        return nil;
    }
}

// Deletes commands that are older than 30 mins
// This is to prevent the DB from getting excessively big, as normally we remove the commands
// only when the robot responds to them.
- (void)deleteOlderCommands {
    debugLog(@"");
    if (self.managedObjectContext) {
        
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

- (BOOL)addCommandToTracker:(NSString *)xmlCommand withRequestId:(NSString *)requestId {
    @synchronized(self)  {
        if ([AppHelper isStringNilOrEmpty:xmlCommand] || [AppHelper isStringNilOrEmpty:requestId]) {
            debugLog(@"xmlCommand or requestId cannot be nil!. Will not add command to tracker.");
            return NO;
        }
        
        if (self.managedObjectContext) {
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
            if (entity == nil) {
                entity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_COMMAND_TRACKER inManagedObjectContext:self.managedObjectContext];
                entity.requestId = requestId;
            }
            else {
                debugLog(@"Command entity exists for requestId = %@. Will overwrite command data!", requestId);
            }
            entity.xmlCommand = xmlCommand;
            entity.creationTime = [NSDate date];
            
            NSError *saveError = nil;
            [self.managedObjectContext save:&saveError];
            if (saveError) {
                debugLog(@"Could not persist CommandTrackerEntity for requestId = %@!!. Error = %@", requestId, saveError);
                return NO;
            }
            return YES;
        }
        else {
            debugLog(@"managedObjectContext not created. addCommandToTracker failed!");
            return NO;
        }
    }
}

- (BOOL)removeCommandForRequestId:(NSString *)requestId {
    @synchronized(self) {
        if ([AppHelper isStringNilOrEmpty:requestId]) {
            debugLog(@"requestId cannot be nil! Will not remove command!");
            return NO;
        }
        
        if (self.managedObjectContext) {
            CommandTrackerEntity *entity = [self getCommandTrackerEntityForRequestId:requestId];
            if (entity == nil) {
                return NO;
            }
            [self.managedObjectContext deleteObject:entity];
            NSError *saveError = nil;
            [self.managedObjectContext save:&saveError];
            if (saveError) {
                debugLog(@"Could not delete CommandTrackerEntity for requestId = %@!!. Error = %@", requestId, saveError);
                return NO;
            }
            debugLog(@"Command entity for requestId = [%@] DELETED from data store!", requestId);
            return YES;
        }
        else {
            debugLog(@"managedObjectContext not created. removeCommandForRequestId failed!");
            return NO;
        }
    }
}

- (CommandTrackerEntity *)getCommandTrackerEntityForRequestId:(NSString *)requestId {
    debugLog(@"");
    if (self.managedObjectContext) {
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
        if (error) {
            debugLog(@"Error in getCommandForRequestId: %@", error);
            return nil;
        }
        if ([objects count] == 0) {
            debugLog(@"No commands found matching requestId = %@", requestId);
            return nil;
        }
        CommandTrackerEntity *tracker = (CommandTrackerEntity *) [objects objectAtIndex:0];
        return tracker;
    }
    else {
        return nil;
    }
}

- (NSString *)getCommandForRequestId:(NSString *)requestId {
    @synchronized(self) {
        if ([AppHelper isStringNilOrEmpty:requestId]) {
            debugLog(@"requestId cannot be nil!");
            return nil;
        }
        
        if (self.managedObjectContext) {
            CommandTrackerEntity *tracker = [self getCommandTrackerEntityForRequestId:requestId];
            if (tracker == nil) {
                return nil;
            }
            debugLog(@"Found command = %@", tracker.xmlCommand);
            return tracker.xmlCommand;
        }
        else {
            debugLog(@"managedObjectContext not created. DB operation failed!");
            return nil;
        }
    }
}

- (void)saveNeatoUser:(NeatoUser *)neatoUser {
    debugLog(@"");
    if(!neatoUser) {
        debugLog(@"NeatoUser object is nil won't insert");
        return;
    }
    [self insertOrUpdateNeatoUser:neatoUser];
}

- (NeatoUser *)getNeatoUser {
    debugLog(@"");
    if(self.managedObjectContext) {
        NeatoUserEntity *userEntity;
        NeatoUser *user;
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
        request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
        
        NSError *error = nil;
        NSArray *userEntityArray = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"There is an error while retrieving users");
            return nil;
        }
        if ([userEntityArray count] > 1) {
            debugLog(@"!!!ERROR!! there cannot be more than one user in our database(According to our schema)");
            return nil;
        }
        userEntity = [userEntityArray lastObject];
        user = [[NeatoUser alloc] init];
        user.userId = userEntity.userId;
        user.name = userEntity.name;
        user.email = userEntity.email;
        user.chatId = userEntity.chatId;
        user.chatPassword = userEntity.chatPassword;
        user.account_type = userEntity.account_type;
        user.password = userEntity.password;
        user.external_social_id = userEntity.external_social_id;
        user.validationStatus = userEntity.validationStatus;
        user.alternateEmail = userEntity.alternateEmail;
        
        NSArray *robots = [userEntity.hasRobots allObjects];
        user.robots = [[NSMutableArray alloc] init];
        for(int i=0; i<[robots count]; i++) {
            [user.robots addObject:[robots objectAtIndex:i]];
        }
        NSArray *socialNetowrks = [userEntity.hasSocialNetowrks allObjects];
        user.socialNetworks = [[NSMutableArray alloc]init];
        for(int i=0; i<[socialNetowrks count]; i++) {
            [user.socialNetworks addObject:[socialNetowrks objectAtIndex:i]];
        }
        return user;
    }
    else {
        debugLog(@"Managed object context is nil!");
        return nil;
    }
}

- (void)saveNeatoRobot:(NeatoRobot * )robot forUser:(NSString *)userId {
    @synchronized(self) {
        if(self.managedObjectContext) {
            NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
            request.predicate = [NSPredicate predicateWithFormat:@"userId= [c]%@",userId];
            NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
            request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
            
            NSError *error = nil;
            NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
            if(error) {
                debugLog(@"Error while fetching users");
                return;
            }
            if([users count] > 1) {
                debugLog(@"!!!ERROR!!! there cant be more than one user with same userId");
                return;
            }
            if([users count] == 0) {
                debugLog(@"user with this userId doesnt exist");
                return;
            }
            else {
                NeatoUserEntity *neatoUserEntity=[users lastObject];
                NeatoRobotEntity *neatoRobotEntity = [self insertOrUpdateRobot:robot];
                if(neatoRobotEntity) {
                    [neatoUserEntity addHasRobotsObject:neatoRobotEntity];
                }
                
            }
            [self saveDatabase];
        }
        else {
            debugLog(@"Managed object context is nil");
        }
    }
}

- (void)saveSocialNetwork:(NeatoSocialNetworks *)network forUser:(NSString *)userId {
    @synchronized(self) {
        if(self.managedObjectContext) {
            NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
            request.predicate = [NSPredicate predicateWithFormat:@"userId= [c]%@",userId];
            NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
            request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
            
            NSError *error = nil;
            NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
            if(error) {
                debugLog(@"Error while fetching users");
                return;
            }
            if([users count] > 1) {
                debugLog(@"!!!ERROR!!! there cant be more than one user with same userId");
                return;
            }
            if([users count] == 0) {
                debugLog(@"User with this userId doesnt exist");
                return;
            }
            else {
                NeatoUserEntity *neatoUserEntity = [users lastObject];
                NeatoSocialNetworksEntity *socialNetworkEntity = [self insertOrUpdateSocialNetworks:network];
                if(socialNetworkEntity) {
                    [neatoUserEntity addHasSocialNetowrksObject:socialNetworkEntity];
                }
                
            }
            [self saveDatabase];     
        }
        else {
            debugLog(@"Managed object context is nil");
        }
    }
}

- (NSMutableArray *)getAllRobotsForUser:(NSString *)userId {
    if(self.managedObjectContext) {
        NSMutableArray *robots = [[NSMutableArray alloc]init];
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
        request.predicate = [NSPredicate predicateWithFormat:@"userId= [c]%@",userId];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
        request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
        
        NSError *error = nil;
        NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"Error while fetching users");
            return nil;
        }
        if([users count] > 1) {
            debugLog(@"!!!ERROR!!! there cant be more than one user with same userId");
            return nil;
        }
        if([users count] == 0) {
            debugLog(@"User with this userId doesnt exist");
            return nil;
        }
        else {
            NSArray *robotEntities = [[[users lastObject] hasRobots] allObjects];
            for(int i = 0; i<robotEntities.count; i++) {
                NeatoRobot *robot = [[NeatoRobot alloc]init];
                robot.robotId = [[robotEntities objectAtIndex:i] robotId];
                robot.name = [[robotEntities objectAtIndex:i] name];
                robot.chatId = [[robotEntities objectAtIndex:i] chatId];
                robot.serialNumber = [[robotEntities objectAtIndex:i] serialNumber];
                robot.ipAddress = [[robotEntities objectAtIndex:i] ipAddress];
                robot.port = [[(NeatoRobotEntity *)[robotEntities objectAtIndex:i] port] intValue];
                [robots addObject:robot];
            }
            return robots;
        }
    }
    else {
        debugLog(@"Managed object context is nil");
        return nil;
    }
}

- (NSMutableArray *)getAllSocialNetworksForUser:(NSString *)userId {
    if(self.managedObjectContext) {
        NSMutableArray *socialNetworks = [[NSMutableArray alloc]init];
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
        request.predicate = [NSPredicate predicateWithFormat:@"userId= [c]%@",userId];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
        request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
        
        NSError *error = nil;
        NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"Error while fetching users");
            return nil;
        }
        if([users count] > 1) {
            debugLog(@"!!!ERROR!!! there cant be more than one user with same userId");
            return nil;
        }
        if([users count] == 0) {
            debugLog(@"User with this userId doesnt exist");
            return nil;
        }
        else {
            NSArray *socialNetworksEntities = [[[users lastObject] hasSocialNetowrks] allObjects];
            for(int i = 0;i<socialNetworksEntities.count;i++)
            {
                NeatoSocialNetworks *socialNetwork = [[NeatoSocialNetworks alloc] init];
                socialNetwork.externalSocialId = [[socialNetworksEntities objectAtIndex:i] externalSocialId];
                socialNetwork.provider = [[socialNetworksEntities objectAtIndex:i] provider];
                [socialNetworks addObject:socialNetwork];
            }
            return socialNetworks;
        }
        
    }
    else {
        debugLog(@"Managed object context is nil");
        return nil;
        
    }
}

- (NeatoRobot *)getRobotForId:(NSString *)serialNumber {
    if(self.managedObjectContext) {
        NeatoRobot *robot = [[NeatoRobot alloc] init];
        NeatoRobotEntity *robotEntity = [self getRobotEntityForSerialNumber:serialNumber];
        if(robotEntity) {
            robot.robotId = [robotEntity robotId];
            robot.name = [robotEntity name];
            robot.chatId = [robotEntity chatId];
            robot.serialNumber = [robotEntity serialNumber];
            robot.ipAddress = [robotEntity ipAddress];
            robot.port = [[robotEntity port] intValue];
            return robot;
        }
        else {
            debugLog(@"Couldn't fetch robotEntity with this serialNumber %@",serialNumber);
            return nil;
        }
    }
    else {
        debugLog(@"Managed object context is nil");
        return nil;
    }
}

- (NeatoRobotEntity *)getRobotEntityForSerialNumber:(NSString *)serialNumber {
    if(self.managedObjectContext) {
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_ROBOT];
        request.predicate = [NSPredicate predicateWithFormat:@"serialNumber= [c]%@", serialNumber];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
        request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
        
        NSError *error = nil;
        NSArray *robotEntities = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"There is an error %@", [error description]);
            return nil;
        }
        if([robotEntities count] > 1) {
            debugLog(@"!!ERROR!!! there cannot be more than one robot for a serialNumber");
            return nil;
        }
        if([robotEntities count] == 0) {
            debugLog(@"There is no robot with this robotId(or serialNumber)");
            return nil;
        }
        else {
            return [robotEntities lastObject];
        }
    }
    else {
        debugLog(@"Managed object context is nil");
        return nil;
    }  
}

- (void)updateRobotForRobotId:(NSString *)serialNumber andForName:(NSString *)robotName {
    if(self.managedObjectContext) {
        NeatoRobotEntity *robotEntity;
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_ROBOT];
        request.predicate = [NSPredicate predicateWithFormat:@"serialNumber= [c]%@", serialNumber];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
        request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
        
        NSError *error = nil;
        NSArray *robots = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"Social networks fetch request failed");
            return ;
        }
        if([robots count] > 1) {
            debugLog(@"!!!!ERROR!!!!! There cannot be more than one robotEntity with same serialNumber");
            return;
        }
        if([robots count] == 1) {
            robotEntity = [robots lastObject];
            robotEntity.name = robotName;
        }
        else {
            debugLog(@"Robot does not Exist");
            return ;
        }  
        [self saveDatabase];
    }
    else {
        debugLog(@"ManagedObjectContext is nil!!");
    }
}

- (void)deleteUserDetails {
    if(self.managedObjectContext) {
        NSError * error;
        // First retrieve store URL
        NSURL * storeURL = [[self.managedObjectContext persistentStoreCoordinator] URLForPersistentStore:[[[self.managedObjectContext persistentStoreCoordinator] persistentStores] lastObject]];
        // Locking the current context
        [self.managedObjectContext lock];
        // Droping pending changes
        [self.managedObjectContext reset];
        // Delete the store from the current managedObjectContext
        if ([[self.managedObjectContext persistentStoreCoordinator] removePersistentStore:[[[self.managedObjectContext persistentStoreCoordinator] persistentStores] lastObject] error:&error]) {
            // Remove the file containing the data
            [[NSFileManager defaultManager] removeItemAtURL:storeURL error:&error];
            // Recreate the store
            [[self.managedObjectContext persistentStoreCoordinator] addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error];
        }
        [self.managedObjectContext unlock];
    }
    else {
        debugLog(@"Managed object context is nil");
    }
}
- (void)deleteAllRobots:(NSSet *)robotSet {
    NSArray *robots = [robotSet allObjects];
    for(int i=0 ; i < [robots count] ; i++) {
        NeatoRobotEntity *robotEntity = [robots objectAtIndex:i];
        [self.managedObjectContext deleteObject:robotEntity];
    }
    [self saveDatabase];
}

- (void)dissociateAllRobotsForUserWithEmail:(NSString *)email {
    if(self.managedObjectContext) {
        NeatoUserEntity *userEntity;
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
        request.predicate = [NSPredicate predicateWithFormat:@"email= [c]%@", email];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
        request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
        
        NSError *error = nil;
        NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"Error while fetching users");
            return;
        }
        if([users count] > 1) {
            debugLog(@"!!!!ERROR!!!!! there cannot be more than one userEntity with same email");
            return;
        }
        if([users count] == 1) {
            userEntity = [users lastObject];
            [self deleteAllRobots:userEntity.hasRobots];
            if([userEntity.hasRobots count] == 0) {
                // Checking that there is no associated robot.
                debugLog(@"All robots removed from database.");
            }
            [self saveDatabase];
        }
        else {
            debugLog(@"There is no user with this email");
        }
    }
    else {
        debugLog(@"ManagedObjectContext is nil");
    }
}

- (void)insertOrUpdateNeatoUser:(NeatoUser *)user {
    @synchronized(self) {
        if(self.managedObjectContext) {
            // Find or create the user
            NeatoUserEntity *userEntity;
            NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
            request.predicate = [NSPredicate predicateWithFormat:@"userId= %@", user.userId];
            NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
            request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
            
            NSError *error = nil;
            NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
            if(error) {
                debugLog(@"Error while fetching users.");
                return;
            }
            if([users count] > 1) {
                debugLog(@"!!!!ERROR!!!!! there cannot be more than one userEntity with same userId");
                return;
            }
	    
            if([users count] == 1) {
                userEntity = [users lastObject];
            }
            else {
                userEntity=[NSEntityDescription insertNewObjectForEntityForName:ENTITY_NEATO_USER inManagedObjectContext:self.managedObjectContext];
                userEntity.userId = user.userId;
            }
            
            userEntity.name = user.name;
            userEntity.email = user.email;
            userEntity.chatId = user.chatId;
            userEntity.chatPassword = user.chatPassword;
            userEntity.account_type = user.account_type;
            userEntity.password = user.password;
            userEntity.external_social_id = user.external_social_id;
            userEntity.alternateEmail = user.alternateEmail;
            userEntity.validationStatus = user.validationStatus;
            
            for(int i = 0; i<[user.robots count]; i++) {
                [userEntity addHasRobotsObject:[self insertOrUpdateRobot:[user.robots objectAtIndex:i]]];
            }
            for(int i=0;i<[user.socialNetworks count];i++) {
                [userEntity addHasSocialNetowrksObject:[self insertOrUpdateSocialNetworks:[user.socialNetworks objectAtIndex:i]]];
            }
            [self saveDatabase];
        }
        else {
            debugLog(@"Managed object context is nil");
        }
    }
}



- (void)saveDatabase {
    NSError *saveError = nil;
    if(self.managedObjectContext) {
        [self.managedObjectContext save:&saveError];
        if (saveError) {
            debugLog(@"Could not save the neatoUser in the database");
        }
        else {
            debugLog(@"Successfully saved");
        }
    }
    else {
        debugLog(@"managed object context is nil");
    }
    
}

- (NeatoRobotEntity *)insertOrUpdateRobot:(NeatoRobot *)robot {
    NeatoRobotEntity *robotEntity;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_ROBOT];
    request.predicate = [NSPredicate predicateWithFormat:@"serialNumber= [c]%@", robot.serialNumber];
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"name" ascending:YES];
    request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    
    NSError *error = nil;
    NSArray *robots = [self.managedObjectContext executeFetchRequest:request error:&error];
    if(error) {
        debugLog(@"Social networks fetch request failed");
        return nil;
    }
    if([robots count] > 1) {
        debugLog(@"!!!!ERROR!!!!! there cannot be more than one robotEntity with same serialNumber");
        return nil;
    }
    if([robots count] == 1) {
        robotEntity = [robots lastObject];
    }
    else {
        robotEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_NEATO_ROBOT inManagedObjectContext:self.managedObjectContext];
        robotEntity.serialNumber = robot.serialNumber;
    }
    robotEntity.robotId = robot.robotId;
    robotEntity.name = robot.name;
    robotEntity.userId = robot.robotId;
    robotEntity.chatId = robot.chatId;
    
    robotEntity.ipAddress = robot.ipAddress;
    robotEntity.port = [NSNumber numberWithInt:robot.port];
    return robotEntity;
    
}

- (NeatoSocialNetworksEntity *)insertOrUpdateSocialNetworks:(NeatoSocialNetworks *)socialNetwork {
    NeatoSocialNetworksEntity *socialNetworksEntity;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_SOCIALNETWORKS];
    request.predicate = [NSPredicate predicateWithFormat:@"externalSocialId= %@", socialNetwork.externalSocialId];
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"provider" ascending:YES];
    request.sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    
    NSError *error = nil;
    NSArray *socialNetworks = [self.managedObjectContext executeFetchRequest:request error:&error];
    if(error) {
        debugLog(@"Social networks fetch request failed");
        return nil;
    }
    if([socialNetworks count] > 1) {
        debugLog(@"!!!!ERROR!!!!! there cannot be more than one socialNetworkEntity with same externalSocialId");
        return nil;
    }
    if([socialNetworks count] == 1) {
        socialNetworksEntity = [socialNetworks lastObject];
    }
    else {
        socialNetworksEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_NEATO_SOCIALNETWORKS inManagedObjectContext:self.managedObjectContext];
        socialNetworksEntity.externalSocialId = socialNetwork.externalSocialId;
    }
    socialNetworksEntity.provider = socialNetwork.provider;
    return socialNetworksEntity;
}

- (void)deleteRobotForSerialNumber:(NSString *)serialNumber forUserId:(NSString *)userId {
  if(self.managedObjectContext) {
    NeatoRobotEntity *robotEntity = [self getRobotEntityForSerialNumber:serialNumber];
    [self.managedObjectContext deleteObject:robotEntity];
    [self saveDatabase];
  }
  else {
    debugLog(@"ManagedObjectContext is nil");
  }
}

- (id)createScheduleForRobotId:(NSString *)serialNumber forScheduleType:(NSString *)scheduleType withScheduleId:(NSString *)scheduleId {
    if(self.managedObjectContext) {
        NeatoRobotEntity *robotEntity = [self getRobotEntityForSerialNumber:serialNumber];
        if(robotEntity) {
            ScheduleEntity *scheduleEntity = [self findScheduleEntityForRobotEntiy:robotEntity ofScheduleType:scheduleType];
            if(scheduleEntity) {
                [self deleteScheduleEntity:scheduleEntity];
            }
            ScheduleEntity *insertedScheduleEntity = [self insertNewScheduleWithScheduleId:scheduleId ofType:scheduleType];
            if(insertedScheduleEntity) {
                // Entity inserted.
                [robotEntity addHasScheduleObject:insertedScheduleEntity];
                [self saveDatabase];
                return [NSNumber numberWithBool:YES];
            }
            else {
                debugLog(@"robotEntity is not inserted.");
                // Create NSError
                return [AppHelper nserrorWithDescription:@"Schedule not inserted in database" code:ERROR_DB_ERROR];
            }
        }
        else {
            debugLog(@"robot with robotId %@ does not exist.", serialNumber);
            // create NSError
            return [AppHelper nserrorWithDescription:@"Robot does not exist" code:ERROR_DB_ERROR];
        }
    }
    else {
        debugLog(@"Managed object context is nil");
        // Create NSError
        return [AppHelper nserrorWithDescription:@"Database did not start properly" code:ERROR_DB_ERROR];
    }
}

- (ScheduleEntity *)findScheduleEntityForRobotEntiy:(NeatoRobotEntity *)robotEntity ofScheduleType:(NSString *)scheduleType { 
    NSArray *scheduleEntities = [robotEntity.hasSchedule allObjects];
    for(int i=0 ; i < [scheduleEntities count] ; i++) {
        ScheduleEntity *scheduleEntity = [scheduleEntities objectAtIndex:i];
        if([scheduleEntity.scheduleType isEqualToString:scheduleType]) {
            return scheduleEntity;
        }
    }
    return nil;
}

- (void)deleteScheduleEntity:(ScheduleEntity *)scheduleEntity {
    [self.managedObjectContext deleteObject:scheduleEntity];
}

- (ScheduleEntity *)insertNewScheduleWithScheduleId:(NSString *)scheduleId ofType:(NSString *)scheduleType {
    ScheduleEntity *scheduleEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SCHEDULE inManagedObjectContext:self.managedObjectContext];
    scheduleEntity.scheduleId = scheduleId;
    scheduleEntity.scheduleType = scheduleType;
    ScheduleEventsEntity *scheduleEventsEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SCHEDULE_EVENTS inManagedObjectContext:self.managedObjectContext];
    scheduleEntity.hasScheduleEvent = scheduleEventsEntity;
    return scheduleEntity;
}

- (ScheduleEntity *)getScheduleEntityForScheduleId:(NSString *)scheduleId {
  NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SCHEDULE];
  request.predicate = [NSPredicate predicateWithFormat:@"scheduleId= %@",scheduleId];
  NSError *error = nil;
  NSArray *scheduleEntities = [self.managedObjectContext executeFetchRequest:request error:&error];
  if(error) {
    debugLog(@"Failed to retrive schedule entity with error : %@",[error description]);
    return nil;
  }
  if([scheduleEntities count] > 1) {
    debugLog(@"!!ERROR!!! there cannot be more than one scheduleEntity for a scheduleId");
    return nil;
  }
  if([scheduleEntities count] == 0) {
    debugLog(@"There is no schedule with this scheduleId");
    return nil;
  }
  else {
    return [scheduleEntities lastObject];
  }
}

- (id)scheduleTypeForScheduleId:(NSString *)scheduleId {
  if(self.managedObjectContext) {
    ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:scheduleId];
    if(scheduleEntity) {
      return scheduleEntity.scheduleType;
    }
    else {
      return [AppHelper nserrorWithDescription:@"Could not get scheduleEventType" code:ERROR_DB_ERROR];
    }
  }
  else {
    debugLog(@"Managed object context is nil");
    // Create NSError
    return [AppHelper nserrorWithDescription:@"Database did not start properly" code:ERROR_DB_ERROR];
  }
}

- (id)addBasicScheduleEventData:(NSString *)data withScheduleEventId:(NSString *)scheduleEventId forScheduleId:(NSString *)scheduleId {
  if(self.managedObjectContext) {
    ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:scheduleId];
    if(scheduleEntity) {
      BasicScheduleEventEntity *basicScheduleEventEntity = [self createBasicScheduleEventWithData:data withScheduleEventId:scheduleEventId];
      ScheduleEventsEntity *scheduleEvent = scheduleEntity.hasScheduleEvent;
      [scheduleEvent addHasBasicScheduleEventsObject:basicScheduleEventEntity];
      [self saveDatabase];
      return [NSNumber numberWithBool:YES];
    }
    else {
      return [AppHelper nserrorWithDescription:@"Could not get ScheduleEvent for this ScheduleId" code:ERROR_DB_ERROR];
    }
  }
  else {
    return [AppHelper nserrorWithDescription:@"Database did not start properly" code:ERROR_DB_ERROR];
  }
}

- (BasicScheduleEventEntity *)createBasicScheduleEventWithData:(NSString *)data withScheduleEventId:(NSString *)scheduleEventId {
  BasicScheduleEventEntity *basicScheduleEventsEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_BASIC_SCHEDULE_EVENT inManagedObjectContext:self.managedObjectContext];
  basicScheduleEventsEntity.scheduleEventId = scheduleEventId;
  basicScheduleEventsEntity.parameterStr = data;
  return basicScheduleEventsEntity;
}

- (id)basicScheduleEventEntityWithId:(NSString *)scheduleEventId {
  NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_BASIC_SCHEDULE_EVENT];
  request.predicate = [NSPredicate predicateWithFormat:@"scheduleEventId= %@",scheduleEventId];
  NSError *error = nil;
  NSArray *basicScheduleEventEntities = [self.managedObjectContext executeFetchRequest:request error:&error];
  if(error) {
    debugLog(@"Failed to retrive basic schedule with error: %@",[error description]);
    return nil;
  }
  if([basicScheduleEventEntities count] > 1) {
    debugLog(@"!!ERROR!!! there cannot be more than one scheduleEntity for a scheduleId");
    return nil;
  }
  if([basicScheduleEventEntities count] == 0) {
    return [AppHelper nserrorWithDescription:@"No BasicScheduleEventEntity with this id" code:ERROR_DB_ERROR];
  }
  else {
    return [basicScheduleEventEntities lastObject];
  }
}

- (id)updateBasicScheduleEventWithId:(NSString *)scheduleEventId withData:(NSString *)data {
  if(self.managedObjectContext) {
    id result = [self basicScheduleEventEntityWithId:scheduleEventId];
    if([result isKindOfClass:[NSError class]]) {
      return result;
    }
    if(result) {
      BasicScheduleEventEntity *basicScheduleEventEntity = (BasicScheduleEventEntity *)result;
      basicScheduleEventEntity.parameterStr = data;
      [self saveDatabase];
      return [NSNumber numberWithBool:YES];
    }
    return [AppHelper nserrorWithDescription:@"Could not update schedule." code:ERROR_DB_ERROR];    
  }
  else {
   return [AppHelper nserrorWithDescription:@"Database did not start properly" code:ERROR_DB_ERROR];
  }
}

- (id)deleteBasicScheduleEventWithId:(NSString *)scheduleEventId {
  if(self.managedObjectContext) {
    id result = [self basicScheduleEventEntityWithId:scheduleEventId];
    if([result isKindOfClass:[NSError class]]) {
      return result;
    }
    if(result) {
      [self.managedObjectContext deleteObject:(BasicScheduleEventEntity *)result];
      [self saveDatabase];
      return [NSNumber numberWithBool:YES];
    }
  }
  else {
    return [AppHelper nserrorWithDescription:@"Database did not start properly" code:ERROR_DB_ERROR];
  }
  return [AppHelper nserrorWithDescription:@"Error in Database" code:ERROR_DB_ERROR];
}

- (id)basicScheduleEventWithId:(NSString *)scheduleEventId {
  if(self.managedObjectContext) {
    id result = [self basicScheduleEventEntityWithId:scheduleEventId];
    if([result isKindOfClass:[NSError class]]) {
      return result;
    }if(result) {
      BasicScheduleEventEntity *basicScheduleEventEntity = (BasicScheduleEventEntity *)result;
      BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] init];
      basicScheduleEvent.scheduleEventId = basicScheduleEventEntity.scheduleEventId;
      basicScheduleEvent.parameterStr = basicScheduleEventEntity.parameterStr;
      return basicScheduleEvent;
    }
  }
  else {
    return [AppHelper nserrorWithDescription:@"Database did not start properly" code:ERROR_DB_ERROR];
  }
  return [AppHelper nserrorWithDescription:@"Error in Database" code:ERROR_DB_ERROR];
}

- (id)basicScheduleForScheduleId:(NSString *)scheduleId {
  debugLog(@"");
  if(self.managedObjectContext) {
    Schedule *schedule = [[Schedule alloc] init];
    schedule.scheduleEvent = [[ScheduleEvent alloc] init];
    ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:scheduleId];
    if(scheduleEntity) {
      schedule.scheduleId = scheduleEntity.scheduleId;
      schedule.serverScheduleId = scheduleEntity.server_scheduleId;
      schedule.scheduleType = scheduleEntity.scheduleType;
      schedule.scheduleVersion = scheduleEntity.schedule_version;
      NSArray *basicScheduleEventsEntities = [scheduleEntity.hasScheduleEvent.hasBasicScheduleEvents allObjects];
      NSMutableArray *basicScheduleEvents = [[NSMutableArray alloc] init];
      for(int i=0; i< [basicScheduleEventsEntities count]; i++) {
        BasicScheduleEvent *basicScheduleEvent = [[BasicScheduleEvent alloc] init];
        basicScheduleEvent.scheduleEventId = [[basicScheduleEventsEntities objectAtIndex:i] scheduleEventId];
        basicScheduleEvent.parameterStr = [[basicScheduleEventsEntities objectAtIndex:i] parameterStr];
        [basicScheduleEvents addObject:basicScheduleEvent];
      }
      [schedule.scheduleEvent addBasicScheduleEvents:basicScheduleEvents];
      return schedule;
    }
    else {
      return [AppHelper nserrorWithDescription:@"Error in Database" code:200];
    }
  }
  else {
    return [AppHelper nserrorWithDescription:@"Database did not start properly" code:200];
  }
}

- (void)saveSchedule:(Schedule *)schedule ofType:(NSString *)scheduleType forRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if(!schedule) {
        debugLog(@"Schedule object is nil won't insert");
        return;
    }
    @synchronized(self) {
        if(self.managedObjectContext) {
            NeatoRobotEntity *robotEntity = [self getRobotEntityForSerialNumber:robotId];
            if (!robotEntity) {
                debugLog(@"Cound'nt find robot with Id = %@, will not insert schedule", robotId);
                return;
            }
            ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:schedule.scheduleId];
            if(scheduleEntity) {
                [robotEntity removeHasScheduleObject:scheduleEntity];
                [self deleteScheduleEntity:scheduleEntity];
                [self saveDatabase];
            }
            // Now insert new entity
            scheduleEntity = [self insertNewScheduleWithScheduleId:schedule.scheduleId ofType:scheduleType];
            scheduleEntity.server_scheduleId = schedule.serverScheduleId;
            scheduleEntity.schedule_version = schedule.scheduleVersion;
            [robotEntity addHasScheduleObject:scheduleEntity];
            
            if ([scheduleType isEqualToString:NEATO_SCHEDULE_BASIC]) {
                for (BasicScheduleEvent *basicScheduleEvent in schedule.scheduleEvent.basicScheduleEvents) {
                    BasicScheduleEventEntity *basicScheduleEventEntity = [self createBasicScheduleEventWithData:basicScheduleEvent.parameterStr withScheduleEventId:basicScheduleEvent.scheduleEventId];
                    [scheduleEntity.hasScheduleEvent addHasBasicScheduleEventsObject:basicScheduleEventEntity];
                }
            }
            else if ([scheduleType isEqualToString:NEATO_SCHEDULE_ADVANCE]) {
                // TODO: Needs implementation
            }
            [self saveDatabase];
            
        }
        else {
            debugLog(@"Managed object context is nil");
        }
    }
}

- (id)robotIdForScheduleId:(NSString *)scheduleId {
    if(self.managedObjectContext) {
        ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:scheduleId];
        if(scheduleEntity) {
            return scheduleEntity.ofRobot.serialNumber;
        }
        else {
           return [AppHelper nserrorWithDescription:@"No Schedule with this ScheduleId." code:200];
        }
    }
    else {
        return [AppHelper nserrorWithDescription:@"Managed object context is nil." code:200];
    }
}

- (id)updateServerScheduleId:(NSString *)serverScheduleId andScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId {
    if(self.managedObjectContext) {
        ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:scheduleId];
        if(scheduleEntity) {
            scheduleEntity.server_scheduleId = serverScheduleId;
            scheduleEntity.schedule_version = scheduleVersion;
            [self saveDatabase];
            return [NSNumber numberWithBool:YES];
        }
        else {
            return [AppHelper nserrorWithDescription:@"No Schedule with this ScheduleId." code:INVALID_SCHEDULE_ID];
        }
    }
    else {
        return [AppHelper nserrorWithDescription:@"Database could start properly." code:ERROR_DB_ERROR];
    }
}

- (id)updateScheduleVersion:(NSString *)scheduleVersion forScheduleWithScheduleId:(NSString *)scheduleId {
    if(self.managedObjectContext) {
        ScheduleEntity *scheduleEntity = [self getScheduleEntityForScheduleId:scheduleId];
        if(scheduleEntity) {
            scheduleEntity.schedule_version = scheduleVersion;
            [self saveDatabase];
            return [NSNumber numberWithBool:YES];
        }
        else {
            return [AppHelper nserrorWithDescription:@"No Schedule with this ScheduleId." code:INVALID_SCHEDULE_ID];
        }
    }
    else {
        return [AppHelper nserrorWithDescription:@"Database could not start properly." code:ERROR_DB_ERROR];
    }
}

- (void)updatePassword:(NSString *)newPassword {
    if (self.managedObjectContext) {
        NeatoUserEntity *userEntity;
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
        NSError *error = nil;
        NSArray *userEntityArray = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"There is an error while retrieving users");
            return;
        }
        if ([userEntityArray count] > 1) {
            debugLog(@"!!!ERROR!! There cannot be more than one user in our database(According to our schema)");
            return ;
        }
        userEntity = [userEntityArray lastObject];
        userEntity.password = newPassword;
        [self saveDatabase];
    }
}

- (void)insertOrUpdateNotificaton:(NeatoNotification *)notification forEmail:(NSString *)email {
    if (self.managedObjectContext) {
        NeatoUserEntity * userEntity = [self neatoUserEntityForEmail:email];
        if (userEntity) {
            NeatoNotificationEntity *notificationsEntity = [self insertOrUpdateNeatoNotificaton:notification];
            if (notificationsEntity) {
                [userEntity addHasNotificationOptionsObject:notificationsEntity];
                [self saveDatabase];
            }
        }
        else {
            debugLog(@"User with email = %@ does not exist",email);
        }
    }
}

- (NeatoUserEntity *)neatoUserEntityForEmail:(NSString *)email {
    NeatoUserEntity *userEntity;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_USER];
    request.predicate = [NSPredicate predicateWithFormat:@"email= [c]%@", email];
    NSError *error = nil;
    NSArray *users = [self.managedObjectContext executeFetchRequest:request error:&error];
    if(error) {
        debugLog(@"Error while fetching users.");
        return nil;
    }
    if([users count] > 1) {
        debugLog(@"!!!!Error!!!!! there cannot be more than one userEntity with same email");
        return nil;
    }
    
    if([users count] == 1) {
        userEntity = [users lastObject];
    }
    return userEntity;
}

- (NeatoNotificationEntity *)insertOrUpdateNeatoNotificaton:(NeatoNotification *)notification {
    NeatoNotificationEntity *notificationsEntity;
    if(self.managedObjectContext) {
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_NOTIFICATION];
        request.predicate = [NSPredicate predicateWithFormat:@"notificationId= %@", notification.notificationId];
        NSError *error = nil;
        NSArray *notificationEntites = [self.managedObjectContext executeFetchRequest:request error:&error];
        if(error) {
            debugLog(@"Error while fetching notification entities.");
            return nil;
        }
        if([notificationEntites count] > 1) {
            debugLog(@"!!!!Error!!!!! there cannot be more than one notificationEntites with same notificationId");
            return nil;
        }
        
        if([notificationEntites count] == 1) {
            notificationsEntity = [notificationEntites lastObject];
        }
        else {
            notificationsEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_NEATO_NOTIFICATION inManagedObjectContext:self.managedObjectContext];
            notificationsEntity.notificationId = notification.notificationId;
        }
        notificationsEntity.notificationValue = notification.notificationValue;
        [self saveDatabase];
    }
    else {
        debugLog(@"Managed object context is nil");
    }
    return  notificationsEntity;
}

- (BOOL)notificationsExistForUserWithEmail:(NSString *)email {
    if (self.managedObjectContext) {
        NeatoUserEntity * userEntity = [self neatoUserEntityForEmail:email];
        if (userEntity) {
            NSArray *notificationEntities = [userEntity.hasNotificationOptions allObjects];
            if ([notificationEntities count] == 0) {
                return NO;
            }
            else {
                return YES;
            }
        }
        else {
            debugLog(@"User with email = %@ does not exist",email);
            return nil;
        }
    }
    else {
        debugLog(@"Managed object context is nil");
        return nil;
    }
}

- (void)setNotificationsFromNotificationsArray:(NSArray *)notificationOptionsArray forEmail:(NSString *)email {
    for (int i = 0; i < [notificationOptionsArray count]; i++) {
        NeatoNotification *neatoNotification = [notificationOptionsArray objectAtIndex:i];
        [self insertOrUpdateNotificaton:neatoNotification forEmail:email];
    } 
}

- (NSArray *)notificationsForUserWithEmail:(NSString *)email {
    NSMutableArray *notifications = [[NSMutableArray alloc] init];
    if (self.managedObjectContext) {
        NeatoUserEntity * userEntity = [self neatoUserEntityForEmail:email];
        if (userEntity) {
            NSArray *notificationOptionsEntities = [userEntity.hasNotificationOptions allObjects];
            for (int i = 0 ; i < [notificationOptionsEntities count] ; i++) {
                NeatoNotification *neatoNotification = [[NeatoNotification alloc] init];
                NeatoNotificationEntity *notificationsEntity = [notificationOptionsEntities objectAtIndex:i];
                neatoNotification.notificationId = notificationsEntity.notificationId;
                neatoNotification.notificationValue = notificationsEntity.notificationValue;
                [notifications addObject:neatoNotification];
            }
        }
        else {
            debugLog(@"User with email = %@ does not exist",email);
        }
    }
    return notifications; 
}
- (id)setCleaningArea:(CleaningArea *)cleaningArea {
    debugLog(@"");
    if (self.managedObjectContext) {
        NeatoRobotEntity *robotEntity = [self getRobotEntityForSerialNumber:cleaningArea.robotId];
        if (robotEntity) {
            if(robotEntity.hasCleaningArea) {
                robotEntity.hasCleaningArea.length = [NSNumber numberWithInt:cleaningArea.length];
                robotEntity.hasCleaningArea.height = [NSNumber numberWithInt:cleaningArea.height];
                [self saveDatabase];
                return [NSNumber numberWithBool:YES];
            } else {
                CleaningAreaEntity *cleaningAreaEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_CLEANING_AREA inManagedObjectContext:self.managedObjectContext];
                cleaningAreaEntity.ofRobot = robotEntity;
                cleaningAreaEntity.length = [NSNumber numberWithInt:cleaningArea.length];
                cleaningAreaEntity.height = [NSNumber numberWithInt:cleaningArea.height];
                [self saveDatabase];
                return [NSNumber numberWithBool:YES];
            }
        } else {
            debugLog(@"Robot with Serial no. = %@ does not exist", cleaningArea.robotId);
            return [AppHelper nserrorWithDescription:@"Robot does not exist" code:ERROR_DB_ERROR];
        }
    } else {
        debugLog(@"Managed object context is nil");
        return [AppHelper nserrorWithDescription:@"Database could not start properly." code:ERROR_DB_ERROR];
    }
}

- (id)cleaningAreaForRobotWithId:(NSString *)robotId {
    debugLog(@"");
    if(self.managedObjectContext) {
        CleaningArea *cleaningArea = [[CleaningArea alloc] init];
        cleaningArea.robotId = robotId;
        CleaningAreaEntity *cleaningAreaEntity;
        NeatoRobotEntity *robotEntity = [self getRobotEntityForSerialNumber:robotId];
        if(robotEntity) {
            cleaningAreaEntity = robotEntity.hasCleaningArea;
            cleaningArea.height = [cleaningAreaEntity.height intValue];
            cleaningArea.length = [cleaningAreaEntity.length intValue];
            
            return cleaningArea;
        }
        else {
            debugLog(@"Couldn't fetch robotEntity with this serialNumber %@",robotId);
            return [AppHelper nserrorWithDescription:@"Robot does not exist" code:ERROR_DB_ERROR];
        }
    }
    else {
        debugLog(@"Managed object context is nil");
        return [AppHelper nserrorWithDescription:@"Database could not start properly." code:ERROR_DB_ERROR];
    }
}

- (void)saveXMPPCallbackId:(NSString *)xmppCallbackId {
    debugLog(@"");
    if(!xmppCallbackId) {
        debugLog(@"xmppCallbackId is nil won't insert");
        return;
    }
    [self insertOrUpdateXMPPCallbackId:xmppCallbackId];
}

- (void)insertOrUpdateXMPPCallbackId:(NSString *)xmppCallbackId {
    @synchronized(self) {
        if(self.managedObjectContext) {
            // Find or create the callbackid
            XMPPCallbackEntity *callbackEntity = [self xmppCallbackEntityFromStore];
            
            if (!callbackEntity) {
                callbackEntity = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_NEATO_CALLBACKID inManagedObjectContext:self.managedObjectContext];
                callbackEntity.callbackId = xmppCallbackId;
            }
            else {
                callbackEntity.callbackId = xmppCallbackId;
            }
            [self saveDatabase];
        }
        else {
            debugLog(@"Managed object context is nil");
        }
    }
}

- (NSString *)xmppCallbackId {
    debugLog(@"");
    @synchronized(self) {
        if (self.managedObjectContext) {
            return [self xmppCallbackEntityFromStore].callbackId;
        }
        else {
            debugLog(@"Managed object context is nil!");
            return nil;
        }
    }
}

- (void)removeXMPPCallbackId {
    @synchronized(self) {
        XMPPCallbackEntity *neatoCallbackEntity = [self xmppCallbackEntityFromStore];
        if(neatoCallbackEntity && self.managedObjectContext) {
            [self.managedObjectContext deleteObject:neatoCallbackEntity];
            [self saveDatabase];
            debugLog(@"xmpp callbackid deleted successfully from db");
        }
        else {
            debugLog(@"Failed to remove XMPP callbackId");
        }
    }
}

- (XMPPCallbackEntity *)xmppCallbackEntityFromStore {
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_NEATO_CALLBACKID];
    
    NSError *error = nil;
    NSArray *callbackIds = [self.managedObjectContext executeFetchRequest:request error:&error];
    if(error) {
        debugLog(@"There is an error while retrieving callbackid");
        return nil;
    }
    if ([callbackIds count] > 1) {
        debugLog(@"!!!ERROR!! there cannot be more than one callbackid in our database(According to our schema)");
        [self removeXMPPCallbackId];
        return nil;
    }
    if([callbackIds count] == 0) {
        debugLog(@"There is no XMPP callbackId in our database");
        return nil;
    }
    XMPPCallbackEntity *callbackEntity = [callbackIds lastObject];
    return callbackEntity;
}

@end
