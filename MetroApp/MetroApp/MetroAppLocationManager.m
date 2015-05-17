//
//  MetroAppLocationManager.m
//  MetroApp
//
//  Created by Nighelles on 4/15/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "MetroAppLocationManager.h"

@implementation MetroAppLocationManager


- (void)startStandardUpdates
{
    // Create the location manager if this object does not
    // already have one.
    if (nil == locationManager)
        locationManager = [[CLLocationManager alloc] init];
    
    [locationManager requestWhenInUseAuthorization];
    
    locationManager.delegate = self;
    locationManager.desiredAccuracy = kCLLocationAccuracyKilometer;
    
    // Set a movement threshold for new events.
    locationManager.distanceFilter = 10; // meters
    
    [locationManager startUpdatingLocation];
}

- (void)stopStandardUpdates
{
    if (locationManager != nil) {
        [locationManager stopUpdatingLocation];
    }
}
- (CLLocationManager*)getLocationManager
{
    return locationManager;
}


@end
