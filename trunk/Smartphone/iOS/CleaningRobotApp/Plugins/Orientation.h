//
//  Orientation.h
//  CleaningRobotApp
//
//  Created by User Interface Design GmbH on 05.06.14.
//
//

#import <UIKit/UIKit.h>
#import <Cordova/CDV.h>


@interface Orientation : CDVPlugin

- (void) forceOrientation:(CDVInvokedUrlCommand*)command;

@end
