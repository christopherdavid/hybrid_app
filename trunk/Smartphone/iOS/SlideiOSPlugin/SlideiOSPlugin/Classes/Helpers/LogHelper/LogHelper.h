#import <Foundation/Foundation.h>
#import "NeatoConstants.h"

#ifdef LOGGING_ENABLED
        #define debugLog(fmt, ...) do { \
                            NSString *logFormat = [NSString stringWithFormat:fmt, ##__VA_ARGS__]; \
                            [LogHelper logDebug:logFormat name:[NSString stringWithFormat:@"%s",__PRETTY_FUNCTION__] lineNumber:__LINE__]; \
                            NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__); \
                            }while(0)
        #else
            #define debugLog(...)
#endif

#define releaseLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__); 
                        

@interface LogHelper : NSObject


+(void) logDebug:(NSString *) log name:(NSString *) name lineNumber:(int) lineNumber;

@end
