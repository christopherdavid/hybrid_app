#import <Foundation/Foundation.h>
#import "TCPConnectionHelper.h"

@interface TCPHelper : NSObject

-(void) startCleaning:(id<TCPConnectionHelperProtocol>) delegate;
-(void) stopCleaning:(id<TCPConnectionHelperProtocol>) delegate;

@end
