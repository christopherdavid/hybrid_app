
#import <Foundation/Foundation.h>
#import "GCDAsyncSocket.h"

@interface TCPSocket : NSObject <GCDAsyncSocketDelegate>

@property(nonatomic, weak) id<GCDAsyncSocketDelegate> delegate;

+(TCPSocket *) getSharedTCPSocket;

- (BOOL)connectToRobotWithId:(NSString *)robotId host:(NSString *)host overPort:(int)port;
- (void)beginReadingData;
- (BOOL)isConnected;
- (void)writeData:(NSData *)data withTag:(long)tag;
- (void)disconnect;
- (NSString *)connectedRobotId;
- (BOOL)isConnectedToRobotWithId:(NSString *)robotId;

@end
