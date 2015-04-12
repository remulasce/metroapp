//
//  TodayViewController.m
//  MetroAppWidget
//
//  Created by Nighelles on 4/9/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "TodayViewController.h"
#import <NotificationCenter/NotificationCenter.h>

#import "MetroWidgetItem.h"

@interface TodayViewController () <NCWidgetProviding>

@end

@implementation TodayViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [self updateInformation];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    NSLog(@"Widget will appear");

    updateTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateNotifications) userInfo:nil repeats:YES];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [updateTimer invalidate];
}

- (void)updateNotifications {
    NSLog(@"Widget Timer ticked!");
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        [self updateInformation];
        NSLog(@"Updated Widget Display");
        
    });
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSString*) formatTime:(int) time
{
    int seconds = time % 60;
    int minutes = (time / 60) % 60;
    int hours = time/3600;
    return [NSString stringWithFormat:@"%d:%d:%d",hours,minutes,seconds];
}

- (void) updateInformation
{
    // This needs to be changed, the time that was left when you added the notification is useless. Probably we should take in time since
    //the epoch when the bus will arrive, and then do math to present a good time in this notification.
    
    sharedMetroAppDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.com.fornought.metroapp"];
    metroAppWidgetArrivals = [[NSMutableArray alloc] init];
    
    // Retrieve saved arrivals from shared user defaults.
    // This is confusing because apple sandboxes us, we can also use core data if that works better later
    
    metroAppWidgetArrivals = [sharedMetroAppDefaults objectForKey:@"widgetarrivals"];
    
    NSTimeInterval timeNow = [[NSDate date] timeIntervalSince1970];
    
    NSLog(@"Doing update for the reminder screen");
    
    for (MetroWidgetItem *item in metroAppWidgetArrivals) {
        NSLog(@"Theres at least one item here!");
        NSString* tripName = [(NSArray*)item objectAtIndex:0];
        double reminderEndTime = [(NSNumber*)[(NSArray*)item objectAtIndex:1] doubleValue];
        NSString* timeString = [self formatTime:(int)(reminderEndTime - timeNow)];
        NSLog(@"##### REMINDER: %@, %f", tripName, reminderEndTime);
        NSLog(@"Time now: %f",timeNow);
        if ([(NSNumber*)[(NSArray*)item objectAtIndex:1] doubleValue] < timeNow) {
            [metroAppWidgetArrivals removeObject:item];
        } else {
            [self.nameTestLabel setText:tripName];
            [self.timeTestLabel setText:timeString];
        }
    }
    
    [self.timeTestLabel setNeedsDisplay];
    [self.nameTestLabel setNeedsDisplay];
    [sharedMetroAppDefaults setObject:metroAppWidgetArrivals forKey:@"widgetarrivals"];
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
