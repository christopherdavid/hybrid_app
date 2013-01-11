#import <Foundation/Foundation.h>
#import "GCDAsyncUdpSocket.h"

@interface UDPSocket : NSObject <GCDAsyncUdpSocketDelegate>

@property(nonatomic, weak) id<GCDAsyncUdpSocketDelegate> delegate;

+(UDPSocket *) getSharedUDPSocket;
-(BOOL) enableBroadcast:(BOOL) enable;
-(BOOL) receiveOnce;
-(BOOL) beginReceiving;
-(void) closeSocket;
-(BOOL) prepareUDPSocket;
-(void) sendData:(NSData *)data host:(NSString *) host port:(int) port tag:(long) tag;
@end
