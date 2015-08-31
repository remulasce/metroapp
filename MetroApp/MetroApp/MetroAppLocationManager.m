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

- (double) getCurrentDistanceToStopWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop*) stop {
    return [self getCurrentDistanceToLocationWithComRemulasceLametroappJava_core_Basic_typesBasicLocation: [stop getLocation]];
}

- (double) getCurrentDistanceToLocationWithComRemulasceLametroappJava_core_Basic_typesBasicLocation:(ComRemulasceLametroappJava_coreBasic_typesBasicLocation*) location
{
    CLLocation *currentLocation = [[self getLocationManager] location];
    
    return [LocationUtil GeoDistLat1:location->latitude_ Lat2:currentLocation.coordinate.latitude
                                Lon1:location->longitude_ Lon2:currentLocation.coordinate.longitude];
    
}

- (ComRemulasceLametroappJava_coreBasic_typesBasicLocation*) getCurrentLocation
{
    CLLocation *currentLocation = [[self getLocationManager] location];
    
    ComRemulasceLametroappJava_coreBasic_typesBasicLocation *location = [[ComRemulasceLametroappJava_coreBasic_typesBasicLocation alloc] init];
    location->latitude_ = currentLocation.coordinate.latitude;
    location->longitude_ = currentLocation.coordinate.longitude;
    return location;
}

@end
