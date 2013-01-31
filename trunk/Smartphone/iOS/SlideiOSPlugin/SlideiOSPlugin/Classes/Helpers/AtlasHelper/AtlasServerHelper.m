#import "AtlasServerHelper.h"
#import "LogHelper.h"
#import "NSURLConnectionHelper.h"
#import "AppHelper.h"
#import "AtlasGridMetadata.h"

#define GET_ATLAS_DATA_FOR_ROBOT_POST_STRING @"api_key=%@&serial_number=%@"
#define GET_ATLAS_GRID_METADATA_POST_STRING @"api_key=%@&id_atlas=%@"
#define UPDATE_ATLAS_METADATA_POST_STRING @"api_key=%@&serial_number=%@&atlas_id=%@&delete_grids=%@&xml_data_version=%@&xml_data=%@"

#define SERVER_REPONSE_HANDLER_KEY @"atlas_response_handler_key"

#define GET_ATLAS_DATA_FOR_ROBOT_HANDLER @"getAtlasDataForRobotWithIdHandler:"
#define GET_ATLAS_GRID_METADATA_HANDLER @"getAtlasGridMetadataHandler:"
#define UPDATE_ATLAS_METADATA_HANDLER @"updateAtlasMetadataHandler:"
#define DELETE_ATLAS_GRIDS_HANDLER @"deleteAtlasGridHandler:"

#define RESPONSE_SUCCESS_TRUE @"true"

@interface AtlasServerHelper()

@property(nonatomic, retain) AtlasServerHelper *retained_self;

-(void) notifyRequestFailed:(SEL)selector withError:(NSError *) error;
-(NeatoRobotAtlas *) sanitizeRobotAtlas:(NeatoRobotAtlas *) robotAtlas;
@end

@implementation AtlasServerHelper
@synthesize retained_self = _retained_self;
@synthesize delegate = _delegate;

-(void) getAtlasDataForRobotWithId:(NSString *) robotId
{
    debugLog(@"");
    if (!robotId || [robotId length] == 0)
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"robotId cannot be nil. Will not fetch Atlas data fro robot!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(getAtlasDataFailed:) withError:error];
        return;
    }
    
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_ROBOT_ATLAS_DATA_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ATLAS_DATA_FOR_ROBOT_POST_STRING, NEATO_API_KEY, robotId] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:GET_ATLAS_DATA_FOR_ROBOT_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    

    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void) getAtlasDataForRobotWithIdHandler:(id) value
{
    debugLog(@"");
    if ([value isKindOfClass:[NSError class]])
    {
        [self notifyRequestFailed:@selector(getAtlasDataFailed:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        
        NSDictionary *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        debugLog(@"data = %@", data);
        NeatoRobotAtlas *robotAtlas = [[NeatoRobotAtlas alloc] initWithDictionary:data];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotAtlasData:)])
            {
                [self.delegate performSelector:@selector(gotAtlasData:) withObject:robotAtlas];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(getAtlasDataFailed:) withError:error];
    }
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection responseData:(NSData *) responseData
{
    debugLog(@"");
    NSString *selectorStr = [[connection originalRequest] valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    if (responseData != nil)
    {
        [self performSelector:selector withObject:responseData];
    }
    else
    {
        debugLog(@"Connection did complete successfully but there was not data at server.");
        /*NSMutableDictionary* details = [NSMutableDictionary dictionary];
         [details setValue:NETWORK_CONNECTION_FAILURE_MSG forKey:NSLocalizedDescriptionKey];
         
         NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
         [self notifyRequestFailed:error];*/
    }
}

// This gets called when the connection fails for any reason.
-(void) requestFailedForConnection:(NSURLConnection *)connection error:(NSError *) error
{
    debugLog(@"");
    NSString *selectorStr = [[connection originalRequest] valueForHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    SEL selector = NSSelectorFromString(selectorStr);
    [self performSelector:selector withObject:error];
    //[self notifyRequestFailed:error];
}

-(void) notifyRequestFailed:(SEL) selector withError:(NSError *) error
{
    debugLog(@"");
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:selector])
        {
            [self.delegate performSelector:selector withObject:error];
        }
        self.delegate = nil;
        self.retained_self = nil;
    });
}

-(void) getAtlasGridMetadata:(NSString *) atlasId
{
    debugLog(@"");
    if (!atlasId || [atlasId length] == 0)
    {
        debugLog(@"atlasId cannot be nil. Will not fetch altas grid metadadata!");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"atlasId cannot be nil. Will not fetch altas grid metadadata!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:error];
        return;
    }
    
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_GET_ATLAS_GRID_METADATA_URL]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[NSString stringWithFormat:GET_ATLAS_GRID_METADATA_POST_STRING, NEATO_API_KEY, atlasId] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:GET_ATLAS_GRID_METADATA_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void) getAtlasGridMetadataHandler:(id) value
{
    debugLog(@"");
    if ([value isKindOfClass:[NSError class]])
    {
        [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        
        NSArray *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        NSMutableArray *gridMetadataArr = [[NSMutableArray alloc] init];
        for (NSDictionary *gridDict in data) {
            AtlasGridMetadata *gridMetadata = [[AtlasGridMetadata alloc] initWithDictionary:gridDict];
            [gridMetadataArr addObject:gridMetadata];
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(gotAtlasGridMetadata:)])
            {
                [self.delegate performSelector:@selector(gotAtlasGridMetadata:) withObject:gridMetadataArr];
            }
            self.delegate = nil;
            self.retained_self = nil;
        });
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(getAtlasGridMetadataFailed:) withError:error];
    }
}

-(void) updateRobotAtlasData:(NeatoRobotAtlas *) robotAtlas
{
    debugLog(@"");
    if (!robotAtlas || !robotAtlas.atlasId || [robotAtlas.atlasId length] == 0)
    {
        debugLog(@"NeatoRobotAtlas object cannot be nil. Atlas data update failed!");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"NeatoRobotAtlas object cannot be nil. Atlas data update failed!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToUpdateAtlasMetadataWithError:) withError:error];
        return;
    }
    
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_UPDATE_ATLAS_METADATA_URL]];
    [request setHTTPMethod:@"POST"];
    
    robotAtlas = [self sanitizeRobotAtlas:robotAtlas];
    
    [request setHTTPBody:[[NSString stringWithFormat:UPDATE_ATLAS_METADATA_POST_STRING, NEATO_API_KEY, @"", robotAtlas.atlasId, /*grid deletion is no */ @"0", robotAtlas.version, robotAtlas.atlasMetadata] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:UPDATE_ATLAS_METADATA_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

// Makes sure that none of the properties for an NeatoRobotAtlas object are nil
// If a property is nil, it set to @"" i.e. empty string
-(NeatoRobotAtlas *) sanitizeRobotAtlas:(NeatoRobotAtlas *) robotAtlas
{
    if ([AppHelper isStringNilOrEmpty:robotAtlas.atlasId])
    {
        robotAtlas.atlasId = @"";
    }
    if ([AppHelper isStringNilOrEmpty:robotAtlas.xmlDataUrl])
    {
        robotAtlas.xmlDataUrl = @"";
    }
    if ([AppHelper isStringNilOrEmpty:robotAtlas.version])
    {
        robotAtlas.version = @"";
    }
    if ([AppHelper isStringNilOrEmpty:robotAtlas.robotId])
    {
        robotAtlas.robotId = @"";
    }
    if ([AppHelper isStringNilOrEmpty:robotAtlas.atlasMetadata])
    {
        robotAtlas.atlasMetadata = @"";
    }
    return  robotAtlas;
}

-(void) updateAtlasMetadataHandler:(id) value
{
    debugLog(@"");
    if ([value isKindOfClass:[NSError class]])
    {
        [self notifyRequestFailed:@selector(failedToUpdateAtlasMetadataWithError:) withError:value];
        return;
    }
    
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        
        NSArray *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        
        if([data valueForKey:NEATO_RESPONSE_SUCCESS])
        {
            //NSString *robotAtlasId = [data valueForKey:NEATO_RESPONSE_ROBOT_ATLAS_ID];
            NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self.delegate respondsToSelector:@selector(atlasMetadataUpdated:)])
                {
                    [self.delegate performSelector:@selector(atlasMetadataUpdated:) withObject:message];
                }
                self.delegate = nil;
                self.retained_self = nil;
            });
            
        }
        else
        {
            debugLog(@"Server did not return success!");
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Update failed with an unknown reason." forKey:NSLocalizedDescriptionKey];
            
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            [self notifyRequestFailed:@selector(failedToUpdateAtlasMetadataWithError:) withError:error];
        }
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToUpdateAtlasMetadataWithError:) withError:error];
    }
}

-(void) deleteRobotAtlasData:(NSString *) atalsId
{
    debugLog(@"");
    if (!atalsId || [atalsId length] == 0)
    {
        debugLog(@"atalsId cannot be nil. Atlas data deletion failed!");
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"atalsId cannot be nil. Atlas data deletion failed!" forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToDeleteAtlasGridsWithError:) withError:error];
        return;
    }
    
    self.retained_self = self;
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:NEATO_UPDATE_ATLAS_METADATA_URL]];
    [request setHTTPMethod:@"POST"];

    [request setHTTPBody:[[NSString stringWithFormat:UPDATE_ATLAS_METADATA_POST_STRING, NEATO_API_KEY, @"", atalsId, /*grid deletion is yes */ @"1", @"", @""] dataUsingEncoding:NSUTF8StringEncoding]];
    
    [request setValue:DELETE_ATLAS_GRIDS_HANDLER forHTTPHeaderField:SERVER_REPONSE_HANDLER_KEY];
    
    NSURLConnectionHelper *helper = [[NSURLConnectionHelper alloc] init];
    helper.delegate = self;
    [helper getDataForRequest:request];
}

-(void) deleteAtlasGridHandler:(id) value
{
    debugLog(@"");
    if ([value isKindOfClass:[NSError class]])
    {
        [self notifyRequestFailed:@selector(failedToDeleteAtlasGridsWithError:) withError:value];
        return;
    }
    NSDictionary *jsonData = [AppHelper parseJSON:value];
    NSNumber *status = [NSNumber numberWithInt:[[jsonData valueForKey:NEATO_RESPONSE_STATUS] integerValue]];
    debugLog(@"status = %d", [status intValue]);
    if ([status intValue] == NEATO_STATUS_SUCCESS)
    {
        NSArray *data = [jsonData valueForKey:NEATO_RESPONSE_RESULT];
        
        if([[data valueForKey:NEATO_RESPONSE_SUCCESS] isEqualToString:RESPONSE_SUCCESS_TRUE])
        {
            //NSString *robotAtlasId = [data valueForKey:NEATO_RESPONSE_ROBOT_ATLAS_ID];
            NSString *message = [data valueForKey:NEATO_RESPONSE_MESSAGE];
           
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self.delegate respondsToSelector:@selector(atlasGridsDeleted:)])
                {
                    [self.delegate performSelector:@selector(atlasGridsDeleted:) withObject:message];
                }
                self.delegate = nil;
                self.retained_self = nil;
            });
            
        }
        else
        {
            NSMutableDictionary* details = [NSMutableDictionary dictionary];
            [details setValue:@"Grid deletion failed with an unknown reason." forKey:NSLocalizedDescriptionKey];
            
            NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
            [self notifyRequestFailed:@selector(failedToDeleteAtlasGridsWithError:) withError:error];
        }
    }
    else
    {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:[jsonData valueForKey:NEATO_RESPONSE_MESSAGE] forKey:NSLocalizedDescriptionKey];
        
        NSError *error = [NSError errorWithDomain:SMART_APP_ERROR_DOMAIN code:200 userInfo:details];
        [self notifyRequestFailed:@selector(failedToDeleteAtlasGridsWithError:) withError:error];
    }
}

@end
