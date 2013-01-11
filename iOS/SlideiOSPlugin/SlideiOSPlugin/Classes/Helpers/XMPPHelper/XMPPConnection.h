#import <Foundation/Foundation.h>
#import "XMPP.h"

@interface XMPPConnection : NSObject <XMPPStreamDelegate>


@property(nonatomic, weak) id delegate;

+(XMPPConnection *) getSharedXMPPConnection;
-(BOOL) connectJID:(NSString *)jid password:(NSString *) password withHost:(NSString *) host;
-(void)sendData:(NSString *) command to:(NSString *) to;
-(void) disconnect;
-(BOOL) isConnected;

@end
