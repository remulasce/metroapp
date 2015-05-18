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


// Fix Code Reuse
-(double)GeoDistLat1:(double)lat1 Lat2:(double)lat2 Lon1:(double)lon1 Lon2:(double)lon2
{
    lat1 = 3.1415/180.0*lat1;
    lon1 = 3.1415/180.0*lon1;
    lat2 = 3.1415/180.0*lat2;
    lon2 = 3.1415/180.0*lon2;
    
    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;
    
    double R = 6373000.0;
    
    double a = pow(sin(dlat/2.0),2)+cos(lat1)*cos(lat2)*pow(sin(dlon/2.0),2);
    double c = 2.0*atan2(sqrt(a),sqrt(1.0-a));
    
    return R*c;
}

- (double) getCurrentDistanceToStopWithComRemulasceLametroappJava_coreBasic_typesStop:
    (ComRemulasceLametroappJava_coreBasic_typesStop*)stop
{
    CLLocation *currentLocation = [locationManager location];
    ComRemulasceLametroappJava_coreBasic_typesBasicLocation* stopLocation = [stop getLocation];
    
    if (currentLocation == nil) {
        // Is this an acceptable fail case?
        return 1000.0;
    }
    
    return [self GeoDistLat1:stopLocation->latitude_
                        Lat2:currentLocation.coordinate.latitude
                        Lon1:stopLocation->longitude_
                        Lon2:currentLocation.coordinate.longitude];
}


@end
