//
//  Orientation.m
//  CleaningRobotApp
//
//  Created by User Interface Design GmbH on 05.06.14.
//
//

#import "Orientation.h"
#import <Cordova/CDV.h>

@implementation Orientation

- (void) forceOrientation:(CDVInvokedUrlCommand*)command
    {
        // HACK based on Adlotto cordova recheck plugin: Force rotate by changing the view hierarchy. Present modal view then dismiss it immediately.
        [self.viewController presentViewController:[UIViewController new] animated:NO completion:^{ [self.viewController dismissViewControllerAnimated:NO completion:nil]; }];
        
    }

@end
