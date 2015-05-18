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

@interface MetroAppLocationManager : NSObject <CLLocationManagerDelegate>
{
    CLLocationManager* locationManager;
}

- (CLLocationManager*)getLocationManager;
- (void)startStandardUpdates;
- (void)stopStandardUpdates;

- (double)getCurrentDistanceToStop:(ComRemulasceLametroappJava_coreBasic_typesStop*)stop;

@end
