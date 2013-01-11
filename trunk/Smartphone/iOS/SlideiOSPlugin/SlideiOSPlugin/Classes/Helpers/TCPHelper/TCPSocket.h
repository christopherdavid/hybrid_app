
#import <Foundation/Foundation.h>
#import "GCDAsyncSocket.h"

@interface TCPSocket : NSObject <GCDAsyncSocketDelegate>

@property(nonatomic, weak) id<GCDAsyncSocketDelegate> delegate;

+(TCPSocket *) getSharedTCPSocket;

-(BOOL) connectHost:(NSString *) host overPort:(int) port;
-(void) beginReadingData;
-(BOOL) isConnected;
-(void) writeData:(NSData *) data withTag:(long) tag;
-(void) disconnect;

@end
