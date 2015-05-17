//
//  ReminderViewController.m
//  MetroApp
//
//  Created by Nighelles on 4/12/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "ReminderViewController.h"
#import "MetroAppViewController.h"

@interface ReminderViewController ()

@end

@implementation ReminderViewController

-(ReminderViewController*)initWithDelegate:(MetroAppViewController*)theDelegate
{
    if (self = [super init]) {
        delegate = theDelegate;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    self.view.backgroundColor=[[UIColor blackColor] colorWithAlphaComponent:.6];
    self.reminderConfigView.layer.cornerRadius = 5;
    self.reminderConfigView.layer.shadowOpacity = 0.8;
    self.reminderConfigView.layer.shadowOffset = CGSizeMake(0.0f, 0.0f);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - TableViewDelegate and DataSource code

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [arrivalOptions size];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *simpleTableIdentifier = @"SimpleTableItem";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleTableIdentifier];
    }
    
    ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *tempArrival =
        (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival*)[arrivalOptions getWithInt:[indexPath indexAtPosition:1]];
    
    NSString *displayString = [NSString stringWithFormat:@"Vehicle %@ in %d min",
                               [[tempArrival getVehicleNum] getString],((int)[tempArrival getEstimatedArrivalSeconds]/60)];
    
    cell.textLabel.text = displayString;
    
    // Add code to recognize when we pan to dismiss a stopServiceRequest
    
    /*UIPanGestureRecognizer* sgr =
    [[UIPanGestureRecognizer alloc] initWithTarget:self
                                            action:@selector(swipeDismissStopServiceRequest:) ];
    
    [cell addGestureRecognizer:sgr];
    */
    return cell;
}

-(IBAction)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *selectedArrival;
    selectedArrival = [arrivalOptions getWithInt:[indexPath indexAtPosition:1]];
    
    [delegate createReminderWithName:[_multiArrivalTrip description] forArrival: selectedArrival];
    
    [self.view removeFromSuperview];
}

#pragma mark - Other code

-(void)displayReminderDialogWithArrivals:(ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip*)multiArrivalTrip
                                  inView:(UIView*)parentView
{
    _multiArrivalTrip = multiArrivalTrip;
    arrivalOptions = [multiArrivalTrip->parentArrival_ getArrivals];
    
    [self.view setFrame:[parentView frame]];
    
    self.reminderConfigView.center = [self.view convertPoint:parentView.center fromView:parentView];
    
    [self.arrivalListView reloadData];
    
    [parentView addSubview:self.view];
}


-(IBAction)cancel:(id)sender
{
    [self.view removeFromSuperview];
    // Cancel this view, go back to previous view
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
