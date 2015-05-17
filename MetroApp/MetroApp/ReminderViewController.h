//
//  ReminderViewController.h
//  MetroApp
//
//  Created by Nighelles on 4/12/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Arrival.h"
#import "Vehicle.h"
#import "MultiArrivalTrip.h"    

#import "java/util/ArrayList.h"
#import "java/util/List.h"

@class MetroAppViewController;

@interface ReminderViewController : UIViewController <UITableViewDataSource,UITableViewDelegate>
{
    id<JavaUtilList> arrivalOptions;
    ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip *_multiArrivalTrip;
    MetroAppViewController *delegate;
}

@property (nonatomic, weak) IBOutlet UITableView* arrivalListView;
@property (nonatomic, weak) IBOutlet UIView* reminderConfigView;

-(ReminderViewController*)initWithDelegate:(MetroAppViewController*)theDelegate;
-(void)displayReminderDialogWithArrivals:(ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip*)multiArrivalTrip inView:(UIView*)parentView;
-(IBAction)cancel:(id)sender;

@end
