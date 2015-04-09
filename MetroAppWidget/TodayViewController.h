//
//  TodayViewController.h
//  MetroAppWidget
//
//  Created by Nighelles on 4/9/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TodayViewController : UIViewController
{
    NSUserDefaults* sharedMetroAppDefaults;
    NSMutableArray* metroAppWidgetArrivals;
    
}

@property (nonatomic, weak) IBOutlet UILabel* nameTestLabel;
@property (nonatomic, weak) IBOutlet UILabel* timeTestLabel;

@end
