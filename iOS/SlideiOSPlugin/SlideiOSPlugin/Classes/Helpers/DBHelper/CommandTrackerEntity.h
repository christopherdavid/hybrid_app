#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface CommandTrackerEntity : NSManagedObject

@property (nonatomic, retain) NSNumber * commandType;
@property (nonatomic, retain) NSDate * creationTime;
@property (nonatomic, retain) NSString * requestId;
@property (nonatomic, retain) NSString * xmlCommand;

@end
