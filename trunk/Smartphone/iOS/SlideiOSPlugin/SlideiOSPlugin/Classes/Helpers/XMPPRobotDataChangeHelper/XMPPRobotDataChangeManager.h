#import <Foundation/Foundation.h>

@interface XMPPRobotDataChangeManager : NSObject

// When the device receives data change notification over XMPP,
// this class processes the data and notifies the observer to update
// the UI if required.
// The observer must implement 'updateUIForRobotDataChangeNotification:' method.
// NSDictionary object passed as parameter will contain the data to be passed to JS layer.

+ (id)sharedXmppDataChangeManager;
// Starts listening on XMPP for data change notifications.
// Notifies the observer if the data has changed on
// Observer must implement 'updateUIForRobotDataChangeNotification'
- (void)startListeningRobotDataChangeNotificationsFor:(id)notificationObserver;
// Stops listening XMPP data change notifications
- (void)stopListeningRobotDataChangeNotificationsFor:(id)notificationObserver;

- (BOOL)updateDataTimestampIfChangedForKey:(NSString *)key withProfile:(NSDictionary *)robotProfile;

// Call this method to notify UI about any change in data for a robot.
- (void)notifyDataChangeForRobotId:(NSString *)robotId withKeyCode:(NSNumber *)key andData:(NSDictionary *)data;

- (void)robotStateAtServerForRobotId:(NSString *)robotId;
@end
