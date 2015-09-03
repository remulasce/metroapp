//
//  MetroAppLocationManager.h
//  MetroApp
//
//  Created by Nighelles on 4/15/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

#import "Stop.h"
#import "BasicLocation.h"

#import "LocationUtil.h"

@interface MetroAppLocationManager : NSObject <CLLocationManagerDelegate>
{
    CLLocationManager* locationManager;
}

- (CLLocationManager*)getLocationManager;
- (void)startStandardUpdates;
- (void)stopStandardUpdates;

// In order to implement location manager interface. This may not work...
// Looks like, it does work. Though, have to actually name them what j2objc will change the names in the other file to.

- (double) getCurrentDistanceToStopWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop*) stop;
- (double) getCurrentDistanceToLocationWithComRemulasceLametroappJava_core_Basic_typesBasicLocation:(ComRemulasceLametroappJava_coreBasic_typesBasicLocation*) location;
- (ComRemulasceLametroappJava_coreBasic_typesBasicLocation*) getCurrentLocation;

@end
