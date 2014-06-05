//
//  Orientation.h
//  CleaningRobotApp
//
//  Created by User Interface Design GmbH on 05.06.14.
//
//

#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface Orientation : CDVPlugin{
    
    NSString* callbackID;
}

@property (nonatomic, copy) NSString* callbackID;

- (void) forceOrientation:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@end
