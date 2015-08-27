//
//  MetroAppViewController.m
//  MetroApp
//
//  Created by Nighelles on 3/27/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "MetroAppViewController.h"

#import "StopNameDatabase.h"
#import "StopNameInfo.h"
#import "Destination.h"
#import "Vehicle.h"
#import "Stop.h"

#import "MetroWidgetItem.h"
#import <CoreLocation/CoreLocation.h>
#import "LaMetroUtil.h"
#import "RegionalizationHelper.h"

@interface MetroAppViewController ()

@end

@implementation MetroAppViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //NETWORK TESTING
    ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter* getter;
    getter = [[ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter alloc] init];
    NSLog([getter doGetHTTPResponseWithNSString:@"http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=lametro-rail&stopId=80122" withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:nil]);
    //END TESTING
    
    // Setup Location
    
    metroAppLocationManager = [[MetroAppLocationManager alloc] init];
    [metroAppLocationManager startStandardUpdates];
    
    // HAVE TO WAIT FOR UPDATES HERE
    CLLocation *currentLocation = [[metroAppLocationManager getLocationManager] location];
    
    // TEST geo search
    
    requestHandler = [[ComRemulasceLametroappJava_coreServiceRequestHandler alloc] init];
    
    testStop = [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithInt:80122];
    
    
    StopNameInfo *testStops = [[StopNameDatabase database] getClosestStopLat:currentLocation.coordinate.latitude
                                                                        Long:currentLocation.coordinate.longitude
                                                                         Tol:.01];
    NSLog(@"Search Test: %@",[testStops stopName]);

    
    // TEST THE DATABASE
    serviceRequestList = [[NSMutableArray alloc] init];
    
    ComRemulasceLametroappJava_coreBasic_typesStop *testStop;
    testStop = [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithNSString:@"80122"];
    
   //[serviceRequestList addObject:[[ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest alloc] initWithComRemulasceLametroappJava_coreBasic_typesStop:testStop withNSString:@"7th Street / Metro Center Station - Metro Blue & Expo Lines"]];
    
    // Search Bar Setup
    searchState = 0;
    searchResultsAvailable = NO;
    
    // Update MultiArrivalView table with timer
    queue = [NSOperationQueue new];
    [queue setMaxConcurrentOperationCount:1];
    [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateMultiArrivalView) userInfo:nil repeats:YES];
    
    // Setup Shared Defaults
    sharedMetroAppDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.com.fornought.metroapp"];
    metroAppWidgetArrivals = [[NSMutableArray alloc] init];
    
    // Reminder Dialog
    
    reminderViewController = [[ReminderViewController alloc] initWithDelegate:self];
    
    // Set regionalization
    
    
    ComRemulasceLametroappJava_coreRegionalizationHelper *regionalizationHelper;
    regionalizationHelper = [ComRemulasceLametroappJava_coreRegionalizationHelper getInstance];
    //OLD regionalizationHelper->agencyName_ = @"actransit";
    
    
    
    // Final UI tweaks
    
    self.serviceRequestView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    self.multiArrivalTripView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

#pragma mark - Table View Code

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    if (tableView == self.serviceRequestView) {
        // Add one for the closest stop display
        return [serviceRequestList count]+1;
    } else if (tableView == self.multiArrivalTripView){
        NSLog(@"Should be this many multi arrival trips: %d",[multiArrivalTrips size]);
        return [multiArrivalTrips size];
    } else if (tableView == self.searchView) {
        if (searchState == 0)
        {
            return 0;
        } else {
            return [searchResults count];
        }
    }
    return 0;
}

- (NSString*) formatTime:(int) time
{
    int seconds = time % 60;
    int minutes = (time / 60) % 60;
    int hours = time/3600;
    return [NSString stringWithFormat:@"%d:%d:%d",hours,minutes,seconds];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == self.multiArrivalTripView) {
        unsigned long int tripIndex = [indexPath indexAtPosition:1];
        
        if (tripIndex == 0)
        {
            NSLog(@"#####");
        }
        
        if (tripIndex < [multiArrivalTrips size])
        {
            NSLog(@"Trip Index: %d", tripIndex);
            ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip *multiArrivalTrip;
            multiArrivalTrip = [multiArrivalTrips getWithInt:(int)tripIndex];
            
            if ([multiArrivalTrip isKindOfClass:[ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip class]]) {
                // Scale based on number of arrivals in trip
                ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *tempSRDA;
                tempSRDA = multiArrivalTrip->parentArrival_;
                
                id<JavaUtilList> tempArrivals = (id<JavaUtilList>)[tempSRDA getArrivals];
                
                NSLog(@"### HERE %@",[self formatTime:(int)[(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival*)[tempArrivals getWithInt:0] getEstimatedArrivalSeconds]]);
                int numArrivals = [tempArrivals size];
                NSLog(@"### Number of arrivals %d", numArrivals);
                
                return 55+(25*numArrivals)+25.0;
            } else if ([multiArrivalTrip isKindOfClass:[ComRemulasceLametroappJava_coreDynamic_dataTypesRequestStatusTrip class]]) {
                return 40.0;
            }
        }
        return 10.0;
    } else {
        return 40.0;
    }
}

- (void)swipeDismissStopServiceRequest:(UIPanGestureRecognizer*)gestureRecognizer
{
    if (gestureRecognizer.state == UIGestureRecognizerStateBegan)
    {
        NSLog(@"Began pan");
    }
    UITableViewCell *cell = (UITableViewCell *)gestureRecognizer.view;
    NSIndexPath* indexPath = [self.serviceRequestView indexPathForCell:cell];
    if ([indexPath indexAtPosition:1] < [serviceRequestList count])
    {
        if (gestureRecognizer.state == UIGestureRecognizerStateChanged)
        {
            NSLog(@"%@",gestureRecognizer.view);
            float translationAmount = [gestureRecognizer translationInView:gestureRecognizer.view].x;
            NSLog(@"Dragging %f",translationAmount);
            
            UITableViewCell *cell = (UITableViewCell *)gestureRecognizer.view;
            if (translationAmount > 75) translationAmount = 75;
            if (translationAmount <= 0) translationAmount = 0;
            
            CGRect frame = cell.contentView.frame;
            frame.origin = CGPointMake(translationAmount, frame.origin.y);
            cell.contentView.frame = frame;
        }
        if (gestureRecognizer.state == UIGestureRecognizerStateEnded) {
            UITableViewCell *cell = (UITableViewCell *)gestureRecognizer.view;
            NSIndexPath* indexPath = [self.serviceRequestView indexPathForCell:cell];
            
            float translationAmount = [gestureRecognizer translationInView:gestureRecognizer.view].x;
            if (translationAmount > 75) {
                int multiArrivalTripIndex = [indexPath indexAtPosition:1];
                
                [[serviceRequestList objectAtIndex:multiArrivalTripIndex ] cancelRequest];
                [serviceRequestList removeObjectAtIndex:multiArrivalTripIndex];
                
                JavaUtilArrayList *tempStopRequestList = [[JavaUtilArrayList alloc] init];
                for (ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest *i in serviceRequestList)
                {
                    [tempStopRequestList addWithId:i];
                }
                
                [requestHandler SetServiceRequestsWithJavaUtilCollection:tempStopRequestList];
                
                [self.serviceRequestView reloadData];
            } else {
                CGRect frame = cell.frame;
                frame.origin = CGPointMake(0, frame.origin.y);
                cell.contentView.frame = frame;
            }
            
        }
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == self.serviceRequestView) {
        static NSString *simpleTableIdentifier = @"SimpleTableItem";
        
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
        
        if (cell == nil) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleTableIdentifier];
        }
        
        if ([indexPath indexAtPosition:1] == [serviceRequestList count]) {
            cell.textLabel.text = @"Add Closest Stop";
            cell.textLabel.alpha = 0.5;
        } else {
            ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest *temp =
            [serviceRequestList objectAtIndex:[indexPath indexAtPosition:1]];
            
            cell.textLabel.text = temp->displayName_;
            cell.textLabel.alpha = 1.0;
            
            // Add code to recognize when we pan to dismiss a stopServiceRequest
            
            UIPanGestureRecognizer* sgr =
                [[UIPanGestureRecognizer alloc] initWithTarget:self
                                                          action:@selector(swipeDismissStopServiceRequest:) ];
            
            [cell addGestureRecognizer:sgr];
        }
        
        return cell;
    } else if (tableView == self.multiArrivalTripView) {
        static NSString *simpleTableIdentifier = @"MultiArrivalTripViewCell";
        
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
        
        UILabel *tripNameLabel;
        UILabel *timeLabel[5];
        UILabel *vehicleLabel[5];
        UILabel *destinationLabel;
        
        ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *tripToDisplay;
        tripToDisplay = [multiArrivalTrips getWithInt:[indexPath indexAtPosition:1]];
        
        
        NSLog(@"##### multiArrivalTrip is this kind of Arrival: %@",[tripToDisplay class]);
        if ([tripToDisplay isKindOfClass:[ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip class]]) {
            ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip *multiArrivalTrip = (ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip*)tripToDisplay;
            // It's a multiArrivalTrip
            ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *tempSRDA;
            tempSRDA = multiArrivalTrip->parentArrival_;
            
            id<JavaUtilList> tempArrivals = [tempSRDA getArrivals];
            
            int numArrivals = [tempArrivals size];
            
            if (cell == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleTableIdentifier];
                CGRect nameLabelFrame = CGRectMake(10,0,220, 25);
                tripNameLabel = [[UILabel alloc] initWithFrame:nameLabelFrame];
                tripNameLabel.tag = 1;
                [cell.contentView addSubview:tripNameLabel];
                
                CGRect destinationLabelFrame = CGRectMake(20, 35, 400, 25);
                destinationLabel = [[UILabel alloc] initWithFrame:destinationLabelFrame];
                destinationLabel.tag = 2;
                [cell.contentView addSubview:destinationLabel];
                
                for (int i=0; i<5; i++) {
                    CGRect timeLabelFrame = CGRectMake(30,55+(25*i),220,25);
                    timeLabel[i] = [[UILabel alloc] initWithFrame:timeLabelFrame];
                    timeLabel[i].tag = 3+i;
                    [cell.contentView addSubview:timeLabel[i]];
                    
                    [timeLabel[i] setFont:[UIFont fontWithName:@"TrebuchetMS-Bold" size:16]];
                    
                    CGRect vehicleLabelFrame = CGRectMake(100,55+(25*i),500,25);
                    vehicleLabel[i] = [[UILabel alloc] initWithFrame:vehicleLabelFrame];
                    vehicleLabel[i].tag = (20+i);
                    [cell.contentView addSubview:vehicleLabel[i]];
                    
                    [vehicleLabel[i] setFont:[UIFont fontWithName:@"TrebuchetMS" size:12]];
                }
            } else {
                tripNameLabel = (UILabel *)[cell.contentView viewWithTag:1];
                destinationLabel = (UILabel *)[cell.contentView viewWithTag:2];
                for (int i=0; i<5; i++) {
                    timeLabel[i]=(UILabel*)[cell.contentView viewWithTag:3+i];
                    vehicleLabel[i]=(UILabel*)[cell.contentView viewWithTag:20+i];
                }
            }
            
            [tripNameLabel setText: [multiArrivalTrip description]];
            
            ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *tempArrival;
            
            tempArrival = [tempArrivals getWithInt:0];
            [destinationLabel setText:[[tempArrival getDirection] getString]];
            
            for (int i=0; i<5; i++) {
                if (i < numArrivals) {
                    tempArrival = [tempArrivals getWithInt:i];
                    int arrivalSeconds = (int)[tempArrival getEstimatedArrivalSeconds];
                    if (arrivalSeconds > 5) {
                        [timeLabel[i] setText:[self formatTime:(int)[tempArrival getEstimatedArrivalSeconds] ]];
                    } else if (arrivalSeconds > 0) {
                        [timeLabel[i] setText:@"Arriving"];
                    } else {
                        NSLog(@"Arrival with < 0 sec exists! FOR SHAME!");
                        ;; // Fixed in SRDA, should never happen
                    }
                    [vehicleLabel[i] setText:[NSString stringWithFormat:@"Veh %@",[[tempArrival getVehicleNum] getString]]];
                } else {
                    if ([timeLabel[i] isKindOfClass:[UILabel class]])
                    {
                        [timeLabel[i] setText:@""];
                        [vehicleLabel[i] setText:@""];
                    }
                }
            }
            
            return cell;
        } else if ([tripToDisplay isKindOfClass:[ComRemulasceLametroappJava_coreDynamic_dataTypesRequestStatusTrip class]]) {
            // It's a RequestStatusTrip
            ComRemulasceLametroappJava_coreDynamic_dataTypesRequestStatusTrip *requestStatusTrip = (ComRemulasceLametroappJava_coreDynamic_dataTypesRequestStatusTrip*)tripToDisplay;
            static NSString *simpleTableIdentifier = @"SimpleTableItem";
            
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
            
            UIActivityIndicatorView *spinner;
            
            if (cell == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleTableIdentifier];
                
                CGRect spinnerFrame = CGRectMake(40,10,20,20);
                spinner = [[UIActivityIndicatorView alloc] initWithFrame:spinnerFrame];
                [spinner startAnimating];
                spinner.tag = 1;
                [cell.contentView addSubview: spinner];
            } else {
                spinner = (UIActivityIndicatorView*)[[cell contentView] viewWithTag:1];
                [spinner startAnimating];
            }
            
            //cell.textLabel.text = @"Testing Status";
            spinner.color = [UIColor grayColor];
            return cell;
        } else {
            // This should never happen
            NSLog(@"Unknown Arrival type while building cell");
            return cell;
        }
        
    } else if (tableView == self.searchView && searchResultsAvailable==YES) {
        static NSString *simpleTableIdentifier = @"SimpleTableItem";
        
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
        
        if (cell == nil) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleTableIdentifier];
        }
        
        cell.textLabel.text = [[searchResults objectAtIndex:[indexPath indexAtPosition:1]] stopName];
        NSLog(@"Search Test: %@",[[searchResults objectAtIndex:0] stopName]);
        return cell;
    }
    return nil;
}

-(NSIndexPath*)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == self.searchView) {
        [self exitSearchState:self];
        [self.searchText setText:@""];
        
        NSString *stopName = [[[self.searchView cellForRowAtIndexPath:indexPath] textLabel] text];
        
        [self createServiceRequestWithName:stopName];
    } else if (tableView == self.multiArrivalTripView){
        // Code for setting a reminder
        
        
        // Just add reminder for first one at the moment
        ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip *multiArrivalTrip;
        multiArrivalTrip = [multiArrivalTrips getWithInt:[indexPath indexAtPosition:1]];
        ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *tempSRDA;
        tempSRDA = multiArrivalTrip->parentArrival_;
        
        id<JavaUtilList> tempArrivals = [tempSRDA getArrivals];
        
        //[self createReminderForArrival:[tempArrivals getWithInt:0]];
        
        [reminderViewController displayReminderDialogWithArrivals:multiArrivalTrip inView:self.view];
    } else if (tableView == self.serviceRequestView){
        // Use case for adding closest stop
        if ([indexPath indexAtPosition:1] == [serviceRequestList count]) {
            [self addClosestStop];
        }
    }
    
    return nil;
}

#pragma mark - MultiArrivalView

- (void)updateMultiArrivalView {
    NSLog(@"Timer ticked!");
    void (^block)() = ^{
        multiArrivalTrips = [requestHandler GetSortedTripList];
        
    };
    
    [queue addOperationWithBlock:block];
    [self.multiArrivalTripView reloadData];
}

#pragma mark - Search Bar Code

- (IBAction)enterSearchState:(id)sender
{
    searchState = 1;
    [self.searchView setHidden:false];
}

- (IBAction)exitSearchState:(id)sender
{
    searchState = 0;
    [self.searchView setHidden:true];
    [self.searchText resignFirstResponder];
}

-(BOOL)textFieldShouldEndEditing:(UITextField *)textField
{
    NSLog(@"Exiting");
    if (textField==self.searchText) {
        [self exitSearchState:self];
        [textField resignFirstResponder];
        return YES;
    }
    return NO;
}

-(BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    NSLog(@"Begin search");
    if (textField==self.searchText) {
        [self enterSearchState:self];
        return YES;
    }
    return NO;
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    NSLog(@"Return");
    if (textField==self.searchText) {
        [self exitSearchState:self];
        [textField resignFirstResponder];
        return YES;
    }
    return NO;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *tempSearchString = [textField text];
    
    if (tempSearchString.length >= 3) {
        // Do SQL Search to populate search bar
        searchResults = [[StopNameDatabase database] getStopsByNameFragment:tempSearchString];

        if ([searchResults count] > 0)
        {
            NSLog(@"Search Test: %@",[[searchResults objectAtIndex:0] stopName]);
            searchResultsAvailable = YES;
        } else {
            searchResultsAvailable = NO;
        }
        [self.searchView reloadData];
    }
    
    return YES;
}

#pragma mark - Other program logic

- (void)createReminderWithName:(NSString*)name forArrival:(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival*)arrival
{
    // Create a reminder item for the widget
    
    NSArray* newItem;
    
    NSString* displayText = [NSString stringWithFormat:@"%@", name ]; //,[[arrival getDirection] getString]
    
    NSNumber *endTime;
    endTime = [[NSNumber alloc] initWithDouble:([[NSDate date] timeIntervalSince1970] + [arrival getEstimatedArrivalSeconds])];
    
    NSLog(@"Creating reminder for %@, %@",displayText,endTime);

    newItem = [[NSArray alloc] initWithObjects:displayText, endTime, nil];
    
    [metroAppWidgetArrivals addObject:newItem];
    
    [sharedMetroAppDefaults setObject:metroAppWidgetArrivals forKey:@"widgetarrivals"];
    
    //Create Local Notification for the bus as well
    
    UILocalNotification *newBusArrivalNotification = [[UILocalNotification alloc] init];
    
    double arrivalLeadSeconds = 120;
    
    newBusArrivalNotification.fireDate = [NSDate dateWithTimeIntervalSince1970:((NSTimeInterval)[endTime doubleValue]-arrivalLeadSeconds)];
    newBusArrivalNotification.alertBody = [NSString stringWithFormat:@"%@ arriving in 2 minutes.",displayText];
    newBusArrivalNotification.soundName = UILocalNotificationDefaultSoundName;
    newBusArrivalNotification.applicationIconBadgeNumber = 1;
    [[UIApplication sharedApplication] scheduleLocalNotification:newBusArrivalNotification];
}

- (void)createServiceRequestWithName:(NSString*)stopName
{
    searchResults = [[StopNameDatabase database] getStopsByName:stopName];
    
    JavaUtilArrayList *tempStopList = [[JavaUtilArrayList alloc] init];
    
    [tempStopList addWithId:
         [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithNSString:[[searchResults objectAtIndex:0] stopID]]];

    
    ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest *newServiceRequest;
    
    newServiceRequest = [[ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest alloc]
                         initWithJavaUtilCollection:tempStopList withNSString:stopName];
    
    [serviceRequestList addObject:newServiceRequest];
    
    JavaUtilArrayList *tempStopRequestList = [[JavaUtilArrayList alloc] init];
    for (ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest *i in serviceRequestList)
    {
        [tempStopRequestList addWithId:i];
    }
    
    [requestHandler SetServiceRequestsWithJavaUtilCollection:tempStopRequestList];
    
    [self.serviceRequestView reloadData];
}

- (void)addClosestStop
{
    CLLocation *currentLocation = [[metroAppLocationManager getLocationManager] location];

    StopNameInfo *closestStop = [[StopNameDatabase database] getClosestStopLat:currentLocation.coordinate.latitude
                                                                        Long:currentLocation.coordinate.longitude
                                                                         Tol:.005];
    
    if (closestStop != nil) {
        [self createServiceRequestWithName:[closestStop stopName]];
    }
    
    return;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
