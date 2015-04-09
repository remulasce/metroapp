//
//  TodayViewController.m
//  MetroAppWidget
//
//  Created by Nighelles on 4/9/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "TodayViewController.h"
#import <NotificationCenter/NotificationCenter.h>


@interface TodayViewController () <NCWidgetProviding>

@end

@implementation TodayViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [self updateInformation];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) updateInformation
{
    sharedMetroAppDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.com.fornought.metroapp"];
    metroAppWidgetArrivals = [[NSMutableArray alloc] init];
    
    // Retrieve saved arrivals from shared user defaults.
    // This is confusing because apple sandboxes us, we can also use core data if that works better later
    
    metroAppWidgetArrivals = [sharedMetroAppDefaults objectForKey:@"widgetarrivals"];
    
    if ([metroAppWidgetArrivals count] > 0) {
        [self.nameTestLabel setText:(NSString*)[metroAppWidgetArrivals objectAtIndex:0]];
        [self.timeTestLabel setText:(NSString*)[metroAppWidgetArrivals objectAtIndex:1]];
    }
}

- (void)widgetPerformUpdateWithCompletionHandler:(void (^)(NCUpdateResult))completionHandler {
    // Perform any setup necessary in order to update the view.
    
    // If an error is encountered, use NCUpdateResultFailed
    // If there's no update required, use NCUpdateResultNoData
    // If there's an update, use NCUpdateResultNewData

    completionHandler(NCUpdateResultNewData);
    [self updateInformation];
}

@end
