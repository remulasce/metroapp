                                                                                                                                                                                                                 //
//  StopNameDatabase.m
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "StopNameDatabase.h"
#import "regionalization.h"
#import <math.h>

@implementation StopNameDatabase

- (id)initWithFilename:(NSString*)fileName {
    
    if ((self = [super init])) {
        
        NSString *sqLiteDb = [[NSBundle mainBundle] pathForResource:fileName
                                                             ofType:@"db"];
        
        if (sqlite3_open([sqLiteDb UTF8String], &_database) != SQLITE_OK) {
            NSLog(@"Could not load stop name database.");
        } else {
            NSLog(@"Loaded Database");
        }
        
        NSString *query = [NSString stringWithFormat:@"SELECT * FROM agencyinfo;"];
        
        sqlite3_stmt *statement;
        if (sqlite3_prepare_v2(_database, [query UTF8String], -1, &statement, nil)
            == SQLITE_OK) {
            while (sqlite3_step(statement) == SQLITE_ROW) {
                char *agencyNameCharacters = sqlite3_column_text(statement, 0);
                NSString *agencyName = [[NSString alloc] initWithUTF8String:agencyNameCharacters];
                
                char *agencyDisplayNameCharacters = sqlite3_column_text(statement, 1);
                NSString *agencyDisplayName = [[NSString alloc] initWithUTF8String:agencyDisplayNameCharacters];
                
                double latMin = sqlite3_column_double(statement, 2);
                double latMax = sqlite3_column_double(statement, 3);
                double lonMin = sqlite3_column_double(statement, 4);
                double lonMax = sqlite3_column_double(statement, 5);
                
                ComRemulasceLametroappJava_coreBasic_typesBasicLocation *bottomLeft = [[ComRemulasceLametroappJava_coreBasic_typesBasicLocation alloc] initWithDouble:latMin withDouble:lonMin];
                ComRemulasceLametroappJava_coreBasic_typesBasicLocation *topRight = [[ComRemulasceLametroappJava_coreBasic_typesBasicLocation alloc] initWithDouble:latMax withDouble:lonMax];
                
                _agency = [[ComRemulasceLametroappJava_coreBasic_typesAgency alloc] initWithNSString:agencyName withNSString:agencyDisplayName withComRemulasceLametroappJava_coreBasic_typesBasicLocation:bottomLeft withComRemulasceLametroappJava_coreBasic_typesBasicLocation:topRight];
                
            }
            sqlite3_finalize(statement);
        } else {
            NSLog(@"could not prepare statement: %s\n", sqlite3_errmsg(_database));
        }
        
    }
    return self;
}

-(NSArray *)getStopsByNameFragment:(NSString*)stopNameFragment
{
    NSString *queryString = [stopNameFragment stringByReplacingOccurrencesOfString:@" " withString:@"%%"];
    
    NSMutableArray *result = [[NSMutableArray alloc] init];
    NSString *query = [NSString stringWithFormat:@"SELECT * FROM stopnames WHERE stopname LIKE '%%%@%%'",queryString];
    
    NSLog(@"%@",query);
    
    sqlite3_stmt *statement;
    if (sqlite3_prepare_v2(_database, [query UTF8String], -1, &statement, nil)
        == SQLITE_OK) {
        while (sqlite3_step(statement) == SQLITE_ROW) {
            int uniqueId = sqlite3_column_int(statement, 0);
            char *stopIDChars = (char *) sqlite3_column_text(statement, 1);
            char *stopNameChars = (char *) sqlite3_column_text(statement, 2);
            double latitude = sqlite3_column_double(statement, 3);
            double longitude = sqlite3_column_double(statement, 4);
            NSString *stopID = [[NSString alloc] initWithUTF8String:stopIDChars];
            NSString *stopName = [[NSString alloc] initWithUTF8String:stopNameChars];
            
            ComRemulasceLametroappJava_coreBasic_typesBasicLocation* tempLocation = [[ComRemulasceLametroappJava_coreBasic_typesBasicLocation alloc] initWithDouble:latitude withDouble:longitude];
            ComRemulasceLametroappJava_coreBasic_typesStop *newStop = [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithNSString:stopID withNSString:stopName withComRemulasceLametroappJava_coreBasic_typesAgency:_agency withComRemulasceLametroappJava_coreBasic_typesBasicLocation:tempLocation];
            
            [result addObject:newStop];
        }
        sqlite3_finalize(statement);
    } else {
        NSLog(@"could not prepare statement: %s\n", sqlite3_errmsg(_database));
    }
    
    return result;
}

-(NSArray*)getStopsByLat:(float)latitude Long:(float)longitude Tol:(float)tolerance
{
    NSMutableArray *result = [[NSMutableArray alloc] init];
    float longmin = longitude-tolerance;
    float longmax = longitude+tolerance;
    float latmin = latitude-tolerance;
    float latmax = latitude+tolerance;
    NSString *query = [NSString stringWithFormat:@"SELECT * FROM stopnames WHERE latitude >= %f AND latitude <= %f AND longitude >= %f AND longitude <= %f",latmin,latmax,longmin,longmax];
    
    NSLog(@"%@",query);
    
    sqlite3_stmt *statement;
    if (sqlite3_prepare_v2(_database, [query UTF8String], -1, &statement, nil)
        == SQLITE_OK) {
        while (sqlite3_step(statement) == SQLITE_ROW) {
            int uniqueId = sqlite3_column_int(statement, 0);
            char *stopIDChars = (char *) sqlite3_column_text(statement, 1);
            char *stopNameChars = (char *) sqlite3_column_text(statement, 2);
            double latitude = sqlite3_column_double(statement, 3);
            double longitude = sqlite3_column_double(statement, 4);
            NSString *stopID = [[NSString alloc] initWithUTF8String:stopIDChars];
            NSString *stopName = [[NSString alloc] initWithUTF8String:stopNameChars];
            
            ComRemulasceLametroappJava_coreBasic_typesBasicLocation* tempLocation = [[ComRemulasceLametroappJava_coreBasic_typesBasicLocation alloc] initWithDouble:latitude withDouble:longitude];
            ComRemulasceLametroappJava_coreBasic_typesStop *newStop = [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithNSString:stopID withNSString:stopName withComRemulasceLametroappJava_coreBasic_typesAgency:_agency withComRemulasceLametroappJava_coreBasic_typesBasicLocation:tempLocation];
            
            [result addObject:newStop];
        }
        sqlite3_finalize(statement);
    } else {
        NSLog(@"could not prepare statement: %s\n", sqlite3_errmsg(_database));
    }
    return result;
}

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

-(ComRemulasceLametroappJava_coreBasic_typesStop*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance
{
    NSArray *stopsWithinTolerance = [self getStopsByLat:latitude Long:longitude Tol:tolerance];
    
    ComRemulasceLametroappJava_coreBasic_typesStop* closestStop = nil;
    ComRemulasceLametroappJava_coreBasic_typesStop* testStop = nil;
    double closestDist = 0;
    double newDist = 0;
    
    for (int i = 0; i<[stopsWithinTolerance count]; i++) {
        testStop = [stopsWithinTolerance objectAtIndex:i];
        if (closestStop == nil)
        {
            closestStop = (ComRemulasceLametroappJava_coreBasic_typesStop*)[stopsWithinTolerance objectAtIndex:i];
            // This is not actually a distance, but we don't care, because we're just comparing
            // No need for an expensive square root
            closestDist = [self GeoDistLat1:[closestStop getLocation]->latitude_ Lat2:latitude Lon1:[closestStop getLocation]->longitude_ Lon2:longitude];
            
            NSLog(@"Setting initial stop to: %@, %f",[closestStop getStopName],closestDist);
        } else {
            newDist = [self GeoDistLat1:[closestStop getLocation]->latitude_ Lat2:latitude Lon1:[closestStop getLocation]->longitude_ Lon2:longitude];
            
            NSLog(@"Checking stop: %@, %f",[testStop getStopName],newDist);
            if (newDist < closestDist)
            {
                NSLog(@"Set as new closest");
                closestStop = [stopsWithinTolerance objectAtIndex:i];
                closestDist = newDist;
            }
        }
        NSLog(@"Current selection is: %@", [closestStop getStopName]);
    }
    NSLog(@"Search Chose: %@, %f",[closestStop getStopName],closestDist);
    
    return closestStop;
}

- (void)dealloc {
    sqlite3_close(_database);
}

@end
