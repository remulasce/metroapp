//
//  DatabaseHandler.m
//  MetroApp
//
//  Created by Nighelles on 8/30/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "DatabaseHandler.h"

#import "java/util/ArrayList.h"
#import "LocationUtil.h"

@implementation DatabaseHandler

- (id) init
{
    managedAgencyDatabases = [[NSMutableDictionary alloc] init];
    
    return self;
}

- (void) setRegionalization:(ComRemulasceLametroappJava_coreRegionalizationHelper*) regionalization
{
    _regionalization = regionalization;
}

- (void) setupRegions {
    JavaUtilArrayList* installedAgencies = [_regionalization getInstalledAgencies];
    
    for (int i = 0; i < [installedAgencies size]; i++) {
        ComRemulasceLametroappJava_coreBasic_typesAgency *tempAgency = [installedAgencies getWithInt:i];
        
        NSString* agencyName = tempAgency->raw_;
        
        StopNameDatabase* tempDatabase = [[StopNameDatabase alloc] initWithFilename:agencyName];
        
        // Ugh, cause you can't have a hashmap of java objects nicely
        
        [managedAgencyDatabases setObject:tempDatabase forKey:agencyName];
    }
}

- (NSArray *)getStopsByNameFragment:(NSString*)stopNameFragment
{
    JavaUtilArrayList *activeAgencies = [_regionalization getActiveAgencies];
    
    NSMutableArray *stopsToReturn = [[NSMutableArray alloc] init];
    for (int i = 0; i < [activeAgencies size]; i++) {
        ComRemulasceLametroappJava_coreBasic_typesAgency *tempAgency = [activeAgencies getWithInt:i];
        NSString* agencyName = tempAgency->raw_;
        
        if ([managedAgencyDatabases objectForKey:agencyName]) {
            [stopsToReturn addObjectsFromArray:[[managedAgencyDatabases objectForKey:agencyName] getStopsByNameFragment:stopNameFragment]];
        } else {
            [NSException raise:@"Agency should be there" format:@"Requested lookup on an active agency that doesn't exist."];
        }
    }
    return stopsToReturn;
}

- (NSArray*)getStopsByLat:(float)latitude Long:(float)longitude Tol:(float)tolerance
{
    JavaUtilArrayList *activeAgencies = [_regionalization getActiveAgencies];
    
    NSMutableArray *stopsToReturn = [[NSMutableArray alloc] init];
    for (int i = 0; i < [activeAgencies size]; i++) {
        ComRemulasceLametroappJava_coreBasic_typesAgency *tempAgency = [activeAgencies getWithInt:i];
        NSString* agencyName = tempAgency->raw_;
        
        if ([managedAgencyDatabases objectForKey:agencyName]) {
            [stopsToReturn addObjectsFromArray:[[managedAgencyDatabases objectForKey:agencyName] getStopsByLat:latitude Long:longitude Tol:tolerance]];
        } else {
            [NSException raise:@"Agency should be there" format:@"Requested lookup on an active agency that doesn't exist."];
        }
    }
    return stopsToReturn;
}

- (ComRemulasceLametroappJava_coreBasic_typesStop*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance
{
    JavaUtilArrayList *activeAgencies = [_regionalization getActiveAgencies];
    NSMutableArray *stopsToReturn = [[NSMutableArray alloc] init];
    for (int i = 0; i < [activeAgencies size]; i++) {
        ComRemulasceLametroappJava_coreBasic_typesAgency *tempAgency = [activeAgencies getWithInt:i];
        NSString* agencyName = tempAgency->raw_;
        
        if ([managedAgencyDatabases objectForKey:agencyName]) {
            [stopsToReturn addObject:[[managedAgencyDatabases objectForKey:agencyName] getClosestStopLat:latitude Long:longitude Tol:tolerance]];
        } else {
            [NSException raise:@"Agency should be there" format:@"Requested lookup on an active agency that doesn't exist."];
        }
    }
    
    ComRemulasceLametroappJava_coreBasic_typesStop *stopToReturn = nil;
    if ([stopsToReturn count] > 0) {
        stopToReturn = [stopsToReturn objectAtIndex:0];
        double lowestDist = [LocationUtil GeoDistLat1:latitude Lat2:[stopToReturn getLocation]->latitude_
                                                 Lon1:longitude Lon2:[stopToReturn getLocation]->longitude_];
        
        for (ComRemulasceLametroappJava_coreBasic_typesStop *tempStop in stopsToReturn) {
            double tempDist =[LocationUtil GeoDistLat1:latitude Lat2:[tempStop getLocation]->latitude_
                                                  Lon1:longitude Lon2:[tempStop getLocation]->longitude_];
            
            if (tempDist < lowestDist) {
                lowestDist = tempDist;
                stopToReturn = tempStop;
            }
        }
    }
    
    return stopToReturn;
}

@end
