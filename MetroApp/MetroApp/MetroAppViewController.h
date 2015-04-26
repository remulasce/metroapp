//
//  MetroAppViewController.h
//  MetroApp
//
//  Created by Nighelles on 3/27/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MultiArrivalTrip.h"
#import "RequestStatusTrip.h"
#import "StopRouteDestinationArrival.h"

#import "HTTPGetter.h"
#import "ServiceRequestHandler.h"
#import "StopServiceRequest.h"
#import "Stop.h"
#import "Arrival.h"


#import "java/util/ArrayList.h"
#import "java/util/List.h"

#import "MetroAppShared.h"

#import "ReminderViewController.h"

#import "MetroAppLocationManager.h"

@interface MetroAppViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate>
{
    ComRemulasceLametroappJava_coreServiceRequestHandler *requestHandler;
    
    ComRemulasceLametroappJava_coreBasic_typesStop *testStop;
    
    NSMutableArray *serviceRequestList;
    
    NSArray *searchResults;
    BOOL    searchResultsAvailable;
    
    int searchState;
    
    NSOperationQueue *queue;
    
    id<JavaUtilList> multiArrivalTrips;
    
    id<JavaUtilList> arrivalsToDisplay;
    
    NSUserDefaults* sharedMetroAppDefaults;
    NSMutableArray* metroAppWidgetArrivals;
    
    ReminderViewController* reminderViewController;
    
    MetroAppLocationManager* metroAppLocationManager;
}

@property (nonatomic, weak) IBOutlet UITableView* serviceRequestView;
@property (nonatomic, weak) IBOutlet UITableView* multiArrivalTripView;
@property (nonatomic, weak) IBOutlet UITableView* searchView;

@property (nonatomic, weak) IBOutlet UIView* windowView;

@property (nonatomic, strong) IBOutlet UITextField* searchText;

- (void)createReminderWithName:(NSString*)name forArrival:(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival*)arrival;
- (void)createServiceRequestWithName:(NSString*)stopName;
- (void)addClosestStop;
- (IBAction)enterSearchState:(id)sender;
- (IBAction)exitSearchState:(id)sender;

- (void)updateMultiArrivalView;

@end
