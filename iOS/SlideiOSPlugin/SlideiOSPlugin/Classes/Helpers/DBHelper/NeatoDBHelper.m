#import "NeatoDBHelper.h"
#import "LogHelper.h"
#import "sqlite3.h"


#define ENABLE_DB_CREATION_IN_DOCUMENTS_DIR 1

#define DATABASE_NAME                   @"neato_smartapp_database.db"

#define NEATO_USER_TABLE    "CREATE TABLE IF NOT EXISTS neatoUser"\
                            "( userId TEXT NOT NULL PRIMARY KEY"\
                            ", name TEXT"\
                            ", email TEXT"\
                            ", chatId TEXT"\
                            ", chatPassword TEXT"\
                            ", account_type TEXT"\
                            ", password TEXT"\
                            ", external_social_id TEXT);"

#define NEATO_USER_ID_COLUMN_INDEX                   1
#define NEATO_NAME_COLUMN_INDEX                      2
#define NEATO_EMAIL_COLUMN_INDEX                     3
#define NEATO_CHAT_ID_COLUMN_INDEX                   4
#define NEATO_CHAT_PASSWORD_COLUMN_INDEX             5
#define NEATO_ACCOUNT_TYPE_COLUMN_INDEX              6
#define NEATOL_PASSWORD_COLUMN_INDEX                 7
#define NEATOL_EXTERNAL_SOCIAL_ID_COLUMN_INDEX       8

#define NEATO_ROBOT_TABLE   "CREATE TABLE IF NOT EXISTS neatoRobot"\
                            "( robotId TEXT"\
                            ", name TEXT"\
                            ", userId TEXT"\
                            ", chatId TEXT"\
                            ", serialNumber TEXT NOT NULL PRIMARY KEY"\
                            ", ipAddress TEXT"\
                            ", port TEXT);"

#define NEATO_ROBOT_ID_COLUMN_INDEX                        1
#define NEATO_ROBOT_NAME_COLUMN_INDEX                      2
#define NEATO_ROBOT_USER_ID_COLUMN_INDEX                   3
#define NEATO_ROBOT_CHAT_ID_COLUMN_INDEX                   4
#define NEATO_ROBOT_SERIAL_NUMBER_COLUMN_INDEX             5
#define NEATO_ROBOT_IP_ADDRESS_COLUMN_INDEX                6
#define NEATOL_ROBOT_PORT_COLUMN_INDEX                     7



#define NEATO_SOCIAL_NETWORKS_TABLE   "CREATE TABLE IF NOT EXISTS neatoSocialNetworks"\
                            "( externalSocialId TEXT NOT NULL PRIMARY KEY"\
                            ", provider TEXT"\
                            ", userId TEXT);"

#define NEATO_SOCIAL_NET_EXT_ID_COLUMN_INDEX                    1
#define NEATO_SOCIAL_NET_PROVIDER_COLUMN_INDEX                  2
#define NEATO_SOCIAL_NET_USER_ID_COLUMN_INDEX                   3



#define INSERT_QUERY_NEATO_USER       "INSERT INTO neatoUser"\
"(userId, name, email, chatId, chatPassword"\
", account_type, password, external_social_id) values (?, ?, ?, ?, ?, ?, ?, ?)"


#define INSERT_QUERY_NEATO_ROBOT       "INSERT INTO neatoRobot (robotId, name, userId, chatId, serialNumber, ipAddress, port) values (?, ?, ?, ?, ?, ?, ?)"

#define INSERT_QUERY_NEATO_SOCIAL_NETWOK       "INSERT INTO neatoSocialNetworks"\
"(externalSocialId, provider, userId) values (?, ?, ?)"

#define SELECT_USER_QUERY "SELECT * FROM neatoUser"


#define SELECT_ROBOT_WHER_USER_QUERY    @"SELECT * FROM neatoRobot WHERE userId ='%@' COLLATE NOCASE;"
#define SELECT_NETWORK_WHER_USER_QUERY    @"SELECT * FROM neatoSocialNetworks WHERE userId ='%@' COLLATE NOCASE;"

#define SELECT_ROBOT_WHERE_ID_QUERY    @"SELECT * FROM neatoRobot WHERE serialNumber ='%@' COLLATE NOCASE;"

#define DELETE_NEATO_USER @"DELETE FROM neatoUser;"
#define DELETE_NEATO_ROBOTS @"DELETE FROM neatoRobot;"
#define DELETE_NEATO_NETWORKS @"DELETE FROM neatoSocialNetworks;"

#define DELETE_ROBOT_WITH_SERIAL_NUMBER_QUERY @"DELETE FROM neatoRobot where serialNumber ='%@' COLLATE NOCASE;"

typedef unsigned long DWORD;
static NeatoDBHelper *sharedInstance  = nil;
@interface NeatoDBHelper()
{
    sqlite3 *mNeatoDatabase;
}
- (DWORD) createDatabaseIfNeeded;
- (NSString *) getDBPath;
- (DWORD) createNeatoUserTable;
- (DWORD) createNeatoRobotTable;
- (DWORD) createSocialNetworksTable;
- (void) deleteUserDetails;
- (void) deleteNeatoUser;
- (void) deleteAllRobots;
- (void) deleteAllNetworks;
- (BOOL) robotExistsWithSerialNumber:(NSString *) serialNumber forUser:(NSString *) userId;
- (void) deleteRobotWithSerialNumber:(NSString *) serialNumber forUser:(NSString *) userId;
@end

@implementation NeatoDBHelper



+(NeatoDBHelper *) sharedNeatoDBHelper
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
            [self createDatabaseIfNeeded];
        }
    }
    
    return self;
}

- (NSString *) getDBPath
{
    NSArray *paths;
    
#ifdef ENABLE_DB_CREATION_IN_DOCUMENTS_DIR
    
    paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
#else
    paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
#endif
    
    NSString *documentsDir = [paths objectAtIndex:0];
    
	return [documentsDir stringByAppendingPathComponent:DATABASE_NAME];
}

-(DWORD) createNeatoUserTable
{
    @synchronized(self)
    {
        char *errMsg = NULL;
        const char *sql_stmt = NEATO_USER_TABLE;
        
        DWORD dwRet = sqlite3_exec(mNeatoDatabase, sql_stmt, NULL, NULL, &errMsg);
        if (dwRet != SQLITE_OK)
        {
            NSLog(@"ERROR:Creating MODAL_EVENTS_TABLE  error message = [%s]", errMsg);
        }
        return dwRet;
    }
}

-(DWORD) createNeatoRobotTable
{
    @synchronized(self)
    {
        char *errMsg = NULL;
        const char *sql_stmt = NEATO_ROBOT_TABLE;
        
        DWORD dwRet = sqlite3_exec(mNeatoDatabase, sql_stmt, NULL, NULL, &errMsg);
        if (dwRet != SQLITE_OK)
        {
            NSLog(@"ERROR:Creating NEATO_ROBOT_TABLE  error message = [%s]", errMsg);
        }
        return dwRet;
    }
}

- (DWORD) createSocialNetworksTable
{
    @synchronized(self)
    {
        char *errMsg = NULL;
        const char *sql_stmt = NEATO_SOCIAL_NETWORKS_TABLE;
        
        DWORD dwRet = sqlite3_exec(mNeatoDatabase, sql_stmt, NULL, NULL, &errMsg);
        if (dwRet != SQLITE_OK)
        {
            NSLog(@"ERROR:Creating NEATO_SOCIAL_NETWORKS_TABLE  error message = [%s]", errMsg);
        }
        return dwRet;
    }
}

- (DWORD) createDatabaseIfNeeded
{
    debugLog(@"");
  
        // NSLog(@"createDatabaseIfNeeded called");
        
        @synchronized(self) {
            
            NSString *databasePath = [self getDBPath];
            
            const char *dbpath = [databasePath UTF8String];
            
            // open database
            DWORD dwRet = sqlite3_open(dbpath, &mNeatoDatabase);
            
            if (dwRet != SQLITE_OK)
            {
                NSLog(@"Error - The database [%s] could not be opened. Error code = [%lu]", dbpath, dwRet);
                return dwRet;
            }
            
            @try
            {
                dwRet = [self createNeatoUserTable];
                if (dwRet != SQLITE_OK)
                {
                    NSLog(@"Error - Failed to create user events table. Error code = [%lu]", dwRet);
                    return dwRet;
                }
                dwRet = [self createNeatoRobotTable];
                if (dwRet != SQLITE_OK)
                {
                    NSLog(@"Error - Failed to create robot table. Error code = [%lu]", dwRet);
                    return dwRet;
                }
                dwRet = [self createSocialNetworksTable];
                if (dwRet != SQLITE_OK)
                {
                    NSLog(@"Error - Failed to create social networs table. Error code = [%lu]", dwRet);
                    return dwRet;
                }
            }
            @catch (NSException *exception) {
                NSLog(@"createDatabaseIfNeeded: Exception Caught %@: %@", [exception name], [exception reason]);
            }
            @finally {
            }
            return dwRet;
        }
    
}
- (void) deleteUserDetails
{
    debugLog(@"");
    [self deleteNeatoUser];
    [self deleteAllNetworks];
    [self deleteAllRobots];
}


- (void) deleteNeatoUser
{
    debugLog(@"");
    sqlite3_stmt *deleteStmt = nil;
    
    NSString *sQuery = DELETE_NEATO_USER;
    const char *sql = [sQuery UTF8String];
    
    int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &deleteStmt, NULL);
    if (nRet != SQLITE_OK) {
        NSLog(@"Error: deleteNeatoUser failed to prepare delete query. error code = [%d]", nRet);
        return;
    }
    
    if (SQLITE_DONE != sqlite3_step(deleteStmt)) {
        NSLog(@"Error: deleteNeatoUser   failed with error code = [%d]", nRet);
        return;
    }
    
    sqlite3_reset(deleteStmt);
    sqlite3_finalize(deleteStmt);
    deleteStmt = nil;
}


- (void) deleteAllRobots
{
    debugLog(@"");
    sqlite3_stmt *deleteStmt = nil;
    
    NSString *sQuery = DELETE_NEATO_ROBOTS;
    const char *sql = [sQuery UTF8String];
    
    int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &deleteStmt, NULL);
    if (nRet != SQLITE_OK) {
        NSLog(@"Error: deleteAllRobots failed to prepare delete query. error code = [%d]", nRet);
        return;
    }
    
    if (SQLITE_DONE != sqlite3_step(deleteStmt)) {
        NSLog(@"Error: deleteAllRobots   failed with error code = [%d]", nRet);
        return;
    }
    
    sqlite3_reset(deleteStmt);
    sqlite3_finalize(deleteStmt);
    deleteStmt = nil;
}


- (void) deleteAllNetworks
{
    debugLog(@"");
    sqlite3_stmt *deleteStmt = nil;
    
    NSString *sQuery = DELETE_NEATO_NETWORKS;
    const char *sql = [sQuery UTF8String];
    
    int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &deleteStmt, NULL);
    if (nRet != SQLITE_OK) {
        NSLog(@"Error: deleteAllNetworks failed to prepare delete query. error code = [%d]", nRet);
        return;
    }
    
    if (SQLITE_DONE != sqlite3_step(deleteStmt)) {
        NSLog(@"Error: deleteAllNetworks   failed with error code = [%d]", nRet);
        return;
    }
    
    sqlite3_reset(deleteStmt);
    sqlite3_finalize(deleteStmt);
    deleteStmt = nil;
}


-(void) saveNeatoUser:(NeatoUser *) neatoUser
{
    debugLog(@"");
    [self deleteUserDetails];
    @synchronized(self) {
        @try {
            //[self open];
            sqlite3_stmt *insertStmt = nil;
            const char *sql = INSERT_QUERY_NEATO_USER;
            
            int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &insertStmt, NULL);
            
            if (nRet != SQLITE_OK) {
                NSLog(@"Error: saveNeatoUser failed to prepare insert query. error code = [%d]", nRet);
                //[self close];
                return;
            }
            
            // Bind the values
            sqlite3_bind_text(insertStmt,   NEATO_USER_ID_COLUMN_INDEX, [neatoUser.userId UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_NAME_COLUMN_INDEX, [neatoUser.name UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_EMAIL_COLUMN_INDEX, [neatoUser.email UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_CHAT_ID_COLUMN_INDEX, [neatoUser.chatId UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_CHAT_PASSWORD_COLUMN_INDEX, [neatoUser.chatPassword UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_ACCOUNT_TYPE_COLUMN_INDEX, [neatoUser.account_type UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATOL_PASSWORD_COLUMN_INDEX, [neatoUser.password UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATOL_EXTERNAL_SOCIAL_ID_COLUMN_INDEX, [neatoUser.external_social_id UTF8String], -1, SQLITE_TRANSIENT);
            
            nRet = sqlite3_step(insertStmt);
            
            if (nRet != SQLITE_DONE) {
                NSLog(@"Error: saveNeatoUser: Insert failed for user = [%@]. error code = [%d]",[neatoUser name], nRet);
                //[self close];
                return;
            }
            if (insertStmt)
            {
                sqlite3_finalize(insertStmt);
            }
            
            for (NeatoRobot *robot in neatoUser.robots) {
                [self saveNeatoRobot:robot forUser:neatoUser.userId];
            }
            
            for (NeatoSocialNetworks *network in neatoUser.socialNetworks) {
                [self saveSocialNetwork:network forUser:neatoUser.userId];
            }
        }
        @catch (NSException *exception) {
            
        }
        @finally {
            //[self close];
        }
        return;
    }
}

-(void) saveSocialNetwork:(NeatoSocialNetworks *) network forUser:(NSString *) userId
{
    debugLog(@"");
    @synchronized(self) {
        @try {
            //[self open];
            sqlite3_stmt *insertStmt = nil;
            const char *sql = INSERT_QUERY_NEATO_SOCIAL_NETWOK;
            
            int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &insertStmt, NULL);
            
            if (nRet != SQLITE_OK) {
                NSLog(@"Error: saveSocialNetwork failed to prepare insert query. error code = [%d]", nRet);
                //[self close];
                return;
            }
            
            // Bind the values
            sqlite3_bind_text(insertStmt,   NEATO_SOCIAL_NET_EXT_ID_COLUMN_INDEX, [network.externalSocialId UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_SOCIAL_NET_PROVIDER_COLUMN_INDEX, [network.provider UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_SOCIAL_NET_USER_ID_COLUMN_INDEX, [userId UTF8String], -1, SQLITE_TRANSIENT);
            
            nRet = sqlite3_step(insertStmt);
            
            if (nRet != SQLITE_DONE) {
                NSLog(@"Error: saveSocialNetwork: Insert failed for network = [%@]. error code = [%d]",[network provider], nRet);
                //[self close];
                return;
            }
            if (insertStmt)
            {
                sqlite3_finalize(insertStmt);
            }
        }
        @catch (NSException *exception) {
            
        }
        @finally {
            //[self close];
        }
        return;
    }
}

-(BOOL) robotExistsWithSerialNumber:(NSString *) serialNumber forUser:(NSString *) userId
{
    NSArray *robots = [self getAllRobotsForUser:userId];
    for (NeatoRobot *robot in robots) {
        if ([robot.serialNumber isEqualToString:serialNumber])
        {
            return YES;
        }
    }
    return NO;
}

-(void) deleteRobotWithSerialNumber:(NSString *) serialNumber forUser:(NSString *) userId
{
    @synchronized(self) {
        sqlite3_stmt *deleteStmt = nil;
        
        NSString *sQuery = [NSString stringWithFormat:DELETE_ROBOT_WITH_SERIAL_NUMBER_QUERY, serialNumber];
        
        const char *sql = [sQuery UTF8String];
        
        int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &deleteStmt, NULL);
        if (nRet != SQLITE_OK) {
            NSLog(@"Error: deleteRobotWithSerialNumber failed to prepare delete query. error code = [%d]", nRet);
            return;
        }
        
        if (SQLITE_DONE != sqlite3_step(deleteStmt)) {
            NSLog(@"Error: deleteRobotWithSerialNumber   failed with error code = [%d]", nRet);
            return;
        }
        
        sqlite3_reset(deleteStmt);
        sqlite3_finalize(deleteStmt);
        deleteStmt = nil;
    }
}

-(void) saveNeatoRobot:(NeatoRobot * )robot forUser:(NSString *) userId
{
    debugLog(@"");
    if ([self robotExistsWithSerialNumber:robot.serialNumber forUser:userId]) {
        [self deleteRobotWithSerialNumber:robot.serialNumber forUser:userId];
    }
    
    @synchronized(self) {
        @try {
            //[self open];
            sqlite3_stmt *insertStmt = nil;
            const char *sql = INSERT_QUERY_NEATO_ROBOT;
            
            int nRet = sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &insertStmt, NULL);
            
            if (nRet != SQLITE_OK) {
                NSLog(@"Error: saveNeatoRobot failed to prepare insert query. error code = [%d]", nRet);
                return;
            }
            
            // Bind the values
            sqlite3_bind_text(insertStmt,   NEATO_ROBOT_ID_COLUMN_INDEX, [robot.robotId UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_ROBOT_NAME_COLUMN_INDEX, [robot.name UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_ROBOT_USER_ID_COLUMN_INDEX, [userId UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_ROBOT_CHAT_ID_COLUMN_INDEX, [robot.chatId UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_ROBOT_SERIAL_NUMBER_COLUMN_INDEX, [robot.serialNumber UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATO_ROBOT_IP_ADDRESS_COLUMN_INDEX, [robot.ipAddress UTF8String], -1, SQLITE_TRANSIENT);
            sqlite3_bind_text(insertStmt,   NEATOL_ROBOT_PORT_COLUMN_INDEX, [[NSString stringWithFormat:@"%d", robot.port] UTF8String], -1, SQLITE_TRANSIENT);

            
            nRet = sqlite3_step(insertStmt);
            
            if (nRet != SQLITE_DONE) {
                NSLog(@"Error: saveNeatoRobot: Insert failed for robot = [%@]. error code = [%d]",[robot name], nRet);
                //[self close];
                return;
            }
            if (insertStmt)
            {
                sqlite3_finalize(insertStmt);
            }
        }
        @catch (NSException *exception) {
            
        }
        @finally {
            //[self close];
        }
        return;
    }
}

-(NeatoUser *) getNeatoUser
{
    debugLog(@"");
    @synchronized(self) {
        NeatoUser *neatoUser = nil;
        @try {
            sqlite3_stmt *selectStmt = nil;
            
            const char *sql = SELECT_USER_QUERY;
            
            //[self open];
            
            int nRet =  sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &selectStmt, NULL);
            if (nRet != SQLITE_OK) {
                NSLog(@"Error: getNeatoUser failed to prepare select query. error code = [%d]", nRet);
            }
            else {
                while (sqlite3_step(selectStmt) == SQLITE_ROW) {
                    neatoUser = [[NeatoUser alloc] init];
                    neatoUser.userId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_USER_ID_COLUMN_INDEX - 1)];
                    neatoUser.name = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_NAME_COLUMN_INDEX - 1)];
                    neatoUser.email = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_EMAIL_COLUMN_INDEX - 1)];
                    neatoUser.chatId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_CHAT_ID_COLUMN_INDEX - 1)];
                    neatoUser.chatPassword = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_CHAT_PASSWORD_COLUMN_INDEX - 1)];
                    neatoUser.account_type = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ACCOUNT_TYPE_COLUMN_INDEX - 1)];
                    neatoUser.password = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATOL_PASSWORD_COLUMN_INDEX - 1)];
                    neatoUser.external_social_id = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATOL_EXTERNAL_SOCIAL_ID_COLUMN_INDEX - 1)];
                    
                }
                
                NSMutableArray *robots = [self getAllRobotsForUser:neatoUser.userId];
                neatoUser.robots = robots;
                
                NSMutableArray *networsk = [self getAllSocialNetworksForUser:neatoUser.userId];
                neatoUser.socialNetworks = networsk;
                
            }
            sqlite3_reset(selectStmt);
            sqlite3_finalize(selectStmt);
            
            selectStmt = nil;
            
        }
        @catch (NSException *exception) {
            
        }
        @finally {
            //[self close];
        }
        return neatoUser;
    }

}

-(NSMutableArray *) getAllRobotsForUser:(NSString *) userId
{
    debugLog(@"");
    @synchronized(self) {
        NSMutableArray *robotList = [[NSMutableArray alloc] init];
        @try {
            sqlite3_stmt *selectStmt = nil;
            
            NSString *sQuery = [NSString stringWithFormat:SELECT_ROBOT_WHER_USER_QUERY, userId];
            
            //[self open];
            
            const char *sql = [sQuery UTF8String];
            
            int nRet =  sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &selectStmt, NULL);
            if (nRet != SQLITE_OK) {
                NSLog(@"Error: getAllRobotsForUser failed to prepare select query. error code = [%d]", nRet);
            }
            else {
                 while (sqlite3_step(selectStmt) == SQLITE_ROW) {
                    
                    NeatoRobot *robot = [[NeatoRobot alloc] init];
                    
                    robot.robotId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_ID_COLUMN_INDEX - 1)];
                    robot.name = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_NAME_COLUMN_INDEX - 1)];
                    robot.chatId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_CHAT_ID_COLUMN_INDEX - 1)];
                    robot.serialNumber = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_SERIAL_NUMBER_COLUMN_INDEX - 1)];
                    robot.port = [[NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATOL_ROBOT_PORT_COLUMN_INDEX - 1)] intValue];
                    robot.ipAddress = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_IP_ADDRESS_COLUMN_INDEX - 1)];
                    [robotList addObject:robot];
                }
            }
            sqlite3_reset(selectStmt);
            sqlite3_finalize(selectStmt);
            selectStmt = nil;
            
        }
        @catch (NSException *exception) {
            
        }
        @finally {
            //[self close];
        }
        return robotList;
    }
}


-(NSArray *) getAllSocialNetworksForUser:(NSString *) userId
{
    debugLog(@"");
    @synchronized(self) {
        NSMutableArray *networkList = [[NSMutableArray alloc] init];
        @try {
            sqlite3_stmt *selectStmt = nil;
            
            NSString *sQuery = [NSString stringWithFormat:SELECT_NETWORK_WHER_USER_QUERY, userId];
            
            //[self open];
            
            const char *sql = [sQuery UTF8String];
            
            int nRet =  sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &selectStmt, NULL);
            if (nRet != SQLITE_OK) {
                NSLog(@"Error: getAllSocialNetworksForUser failed to prepare select query. error code = [%d]", nRet);
            }
            else {
                while (sqlite3_step(selectStmt) == SQLITE_ROW) {
                    NeatoSocialNetworks *network = [[NeatoSocialNetworks alloc] init];
                    
                    network.externalSocialId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_SOCIAL_NET_EXT_ID_COLUMN_INDEX - 1)];
                    network.provider = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_SOCIAL_NET_PROVIDER_COLUMN_INDEX - 1)];
                    
                    [networkList addObject:network];
                }
            }
            sqlite3_reset(selectStmt);
            sqlite3_finalize(selectStmt);
            
            selectStmt = nil;
            
        }
        @catch (NSException *exception) {
            
        }
        @finally {
            //[self close];
        }
        return networkList;
    }
}

-(NeatoRobot *) getRobotForId:(NSString *) robotId
{
    debugLog(@"");
    @synchronized(self)
    {
        NeatoRobot *robot  = nil;
        sqlite3_stmt *selectStmt = nil;
        
        NSString *sQuery = [NSString stringWithFormat:SELECT_ROBOT_WHERE_ID_QUERY, robotId];
        
        //[self open];
        
        const char *sql = [sQuery UTF8String];
        
        int nRet =  sqlite3_prepare_v2(mNeatoDatabase, sql, -1, &selectStmt, NULL);
        if (nRet != SQLITE_OK) {
            NSLog(@"Error: getRobotForId failed to prepare select query. error code = [%d]", nRet);
        }
        else {
            while (sqlite3_step(selectStmt) == SQLITE_ROW) {
                robot = [[NeatoRobot alloc] init];
                robot.robotId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_ID_COLUMN_INDEX - 1)];
                robot.name = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_NAME_COLUMN_INDEX - 1)];
                robot.chatId = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_CHAT_ID_COLUMN_INDEX - 1)];
                robot.serialNumber = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_SERIAL_NUMBER_COLUMN_INDEX - 1)];
                robot.port = [[NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATOL_ROBOT_PORT_COLUMN_INDEX - 1)] intValue];
                robot.ipAddress = [NSString stringWithFormat:@"%s", sqlite3_column_text(selectStmt, NEATO_ROBOT_IP_ADDRESS_COLUMN_INDEX - 1)];
            }
        }
        sqlite3_reset(selectStmt);
        sqlite3_finalize(selectStmt);
        
        selectStmt = nil;
        return  robot;
    }
}

@end
