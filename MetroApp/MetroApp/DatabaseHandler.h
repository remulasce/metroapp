//
//  DatabaseHandler.h
//  MetroApp
//
//  Created by Nighelles on 8/30/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "Stop.h"
#import "StopNameDatabase.h"
#import "Agency.h"
#import "RegionalizationHelper.h"

@interface DatabaseHandler : NSObject
{
    NSMutableDictionary* managedAgencyDatabases;
    
    ComRemulasceLametroappJava_coreRegionalizationHelper *_regionalization;
}

- (void) setRegionalization:(ComRemulasceLametroappJava_coreRegionalizationHelper*) regionalization;
- (void) setupRegions;

- (NSArray *)getStopsByNameFragment:(NSString*)stopNameFragment;
- (NSArray*)getStopsByLat:(float)latitude Long:(float)longitude Tol:(float)tolerance;
- (ComRemulasceLametroappJava_coreBasic_typesStop*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance;

@end
