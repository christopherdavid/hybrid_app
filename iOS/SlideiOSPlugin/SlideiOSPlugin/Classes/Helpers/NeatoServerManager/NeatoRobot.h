#import <Foundation/Foundation.h>

@interface NeatoRobot : NSObject

@property(nonatomic, retain) NSString *ipAddress;
@property(nonatomic, retain) NSString *chatId;
@property(nonatomic, retain) NSString *robotId;
@property(nonatomic, retain) NSString *name;
@property(nonatomic, retain) NSString *serialNumber;
@property(nonatomic, readwrite) int port;

-(id) initWithDictionary:(NSDictionary *) data;

@end
