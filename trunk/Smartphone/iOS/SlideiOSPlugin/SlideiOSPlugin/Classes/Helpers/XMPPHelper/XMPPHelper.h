#import <Foundation/Foundation.h>

@interface XMPPHelper : NSObject

-(void) startCleaning:(NSString *) toJID delegate:(id) delegate;
-(void) stopCleaning:(NSString *) toJID delegate:(id) delegate;;

@end
