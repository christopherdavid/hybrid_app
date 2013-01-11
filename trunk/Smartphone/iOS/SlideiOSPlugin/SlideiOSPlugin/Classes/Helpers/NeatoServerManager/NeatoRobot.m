#import "NeatoRobot.h"

@implementation NeatoRobot
@synthesize name = _name,chatId = _chatId,robotId = _robotId,serialNumber = _serialNumber, ipAddress = _ipAddress,port = _port;

-(id) initWithDictionary:(NSDictionary *) data
{
    id obj = [super init];
    if (obj)
    {
        self.chatId = [data valueForKey:@"chat_id"];
        self.robotId = [data valueForKey:@"id"];
        self.name = [data valueForKey:@"name"];
        self.serialNumber = [data valueForKey:@"serial_number"];
        return obj;
    }
    return nil;
}


@end
