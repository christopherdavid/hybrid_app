#import "LogHelper.h"

#define DEBUG_FILE_NAME @"debugLog.txt"
#define DEBUG_LOGS_FOLDER @"Applogs"

#define WRITE_TO_DOCUMENTS_FOLDER 1

static NSFileHandle *mDebugFileHandle;
@interface LogHelper()
{

}

+(NSString *) getDebugFilePath;
@end

@implementation LogHelper



-(void) dealloc
{
    if (mDebugFileHandle != nil)
    {
        NSString *eos = @"=======================================================";
        [mDebugFileHandle writeData:[eos dataUsingEncoding:NSUTF8StringEncoding]];
        [mDebugFileHandle closeFile];
        mDebugFileHandle = nil;
    }
}

+(void) logDebug:(NSString *) log name:(NSString *) name lineNumber:(int) lineNumber
{
    if (mDebugFileHandle == nil)
    {
        mDebugFileHandle = [NSFileHandle fileHandleForWritingAtPath:[self getDebugFilePath]];
        [mDebugFileHandle seekToEndOfFile];
    }
    
    NSString *formatedString = [NSString stringWithFormat:@"%@\r\n",[NSString stringWithFormat:@"%@. Function : %@. Line: %d", log, name, lineNumber]];
    
    [mDebugFileHandle writeData:[formatedString dataUsingEncoding:NSUTF8StringEncoding ]];
}


+(NSString *) getDebugFilePath
{

#ifdef WRITE_TO_DOCUMENTS_FOLDER
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);      
#else
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
#endif
    
    NSString *rootCacheDirectory = [[paths objectAtIndex:0] stringByAppendingPathComponent:DEBUG_LOGS_FOLDER];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if (![[NSFileManager defaultManager] fileExistsAtPath:rootCacheDirectory])
    {
        NSLog(@"creating DIR");
        [fileManager createDirectoryAtPath:rootCacheDirectory
                                  withIntermediateDirectories:YES
                                                   attributes:nil
                                                        error:NULL];
    }
    
    rootCacheDirectory = [[rootCacheDirectory stringByAppendingString:@"/"] stringByAppendingString:DEBUG_FILE_NAME];
    
    if (![[NSFileManager defaultManager] fileExistsAtPath:rootCacheDirectory])
    {
        NSLog(@"creating File");
        [fileManager createFileAtPath:rootCacheDirectory contents:nil attributes:nil];
    }
    
    
    //NSLog(@"getRootCacheDirectoryPath = [%@]", rootCacheDirectory);
    return rootCacheDirectory;
}


@end
