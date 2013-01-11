#import "NeatoUser.h"
#import "LogHelper.h"
#import "NeatoSocialNetworks.h"
#import "NeatoRobot.h"

@implementation NeatoUser
@synthesize chatId = _chatId,chatPassword = _chatPassword,email = _email,name = _name,socialNetworks = _socialNetworks,userId = _userId,robots = _robots, account_type = _account_type, external_social_id = _external_social_id, password = _password;

-(id) initWithDictionary:(NSDictionary *) dictionary
{
    id obj = [super init];
    if (obj)
    {
        self.chatId = [dictionary valueForKey:@"chat_id"];
        self.chatPassword = [dictionary valueForKey:@"chat_pwd"];
        self.email = [dictionary valueForKey:@"email"];
        self.userId = [dictionary valueForKey:@"id"];
        self.name = [dictionary valueForKey:@"name"];
        
        NSArray *robots = [dictionary valueForKey:@"robots"];
        NSArray *socialNetworks = [dictionary valueForKey:@"social_networks"];

        debugLog(@"robots count = %d", [robots count]);
        debugLog(@"socialNetworks count = %d", [socialNetworks count]);
        
        for (NSDictionary *robotData in robots) {
            NeatoRobot *robot = [[NeatoRobot alloc] initWithDictionary:robotData];
            if (!self.robots)
            {
                self.robots = [[NSMutableArray alloc] init];
            }
            [self.robots addObject:robot];
        }
        
        for (NSDictionary *sociaNetwork in socialNetworks) {
            NeatoSocialNetworks *network = [[NeatoSocialNetworks alloc] initWithDictionary:sociaNetwork];
            if (!self.socialNetworks)
            {
                self.socialNetworks = [[NSMutableArray alloc] init];
            }
            [self.socialNetworks addObject:network];
        }
        
        return obj;
    }
    return nil;
}

@end
