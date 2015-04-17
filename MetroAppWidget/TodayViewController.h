//
//  TodayViewController.h
//  MetroAppWidget
//
//  Created by Nighelles on 4/9/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MetroAppShared.h"

@interface TodayViewController : UIViewController
{
    NSUserDefaults* sharedMetroAppDefaults;
    NSMutableArray* metroAppWidgetArrivals;
    
    NSOperationQueue* queue;
    NSTimer* updateTimer;
    
    NSMutableArray* arrivals;
}

@property (nonatomic, weak) IBOutlet UILabel* nameTestLabel;
@property (nonatomic, weak) IBOutlet UILabel* timeTestLabel;

- (NSString*) formatTime:(int) time;

@end
