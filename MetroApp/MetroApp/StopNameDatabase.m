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

static StopNameDatabase *m_database;

+ (StopNameDatabase*)database {
    if (m_database == nil) {
        m_database = [[StopNameDatabase alloc] init];
    }
    return m_database;
}

- (id)init {
    if ((self = [super init])) {
#if REGIONLOSANGELES
        NSString *sqLiteDb = [[NSBundle mainBundle] pathForResource:@"StopNamesLA"
                                                             ofType:@"db"];
#endif
#if REGIONSANFRANCISCO
        NSString *sqLiteDb = [[NSBundle mainBundle] pathForResource:@"actransit"
                                                             ofType:@"db"];
#endif
        
        if (sqlite3_open([sqLiteDb UTF8String], &_database) != SQLITE_OK) {
            NSLog(@"Could not load stop name database.");
        } else {
            NSLog(@"Loaded Database");
        }
    }
    return self;
}

-(NSArray *)getStopsByName:(NSString*)stopNameFragment
{
    NSMutableArray *result = [[NSMutableArray alloc] init];
    NSString *query = [NSString stringWithFormat:@"SELECT * FROM stopnames WHERE stopname LIKE '%@'",stopNameFragment];
    
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
            StopNameInfo *info = [[StopNameInfo alloc]
                                  initWithUniqueID:uniqueId
                                  stopID:stopID
                                  stopName:stopName
                                  latitude:latitude
                                  longitude:longitude];
            
            [result addObject:info];
        }
        sqlite3_finalize(statement);
    } else {
        NSLog(@"could not prepare statement: %s\n", sqlite3_errmsg(_database));
    }
    return result;
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
            StopNameInfo *info = [[StopNameInfo alloc]
                                initWithUniqueID:uniqueId
                                  stopID:stopID
                                  stopName:stopName
                                  latitude:latitude
                                  longitude:longitude];
            
            [result addObject:info];
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
            StopNameInfo *info = [[StopNameInfo alloc]
                                  initWithUniqueID:uniqueId
                                  stopID:stopID
                                  stopName:stopName
                                  latitude:latitude
                                  longitude:longitude];
            
            [result addObject:info];
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

-(StopNameInfo*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance
{
    NSArray *stopsWithinTolerance = [self getStopsByLat:latitude Long:longitude Tol:tolerance];
    
    StopNameInfo* closestStop = nil;
    StopNameInfo* testStop = nil;
    double closestDist = 0;
    double newDist = 0;
    
    for (int i = 0; i<[stopsWithinTolerance count]; i++) {
        testStop = [stopsWithinTolerance objectAtIndex:i];
        if (closestStop == nil)
        {
            closestStop = [stopsWithinTolerance objectAtIndex:i];
            // This is not actually a distance, but we don't care, because we're just comparing
            // No need for an expensive square root
            closestDist = [self GeoDistLat1:closestStop.latitude Lat2:latitude Lon1:closestStop.longitude Lon2:longitude];
            
            NSLog(@"Setting initial stop to: %@, %f",closestStop.stopName,closestDist);
        } else {
            newDist = [self GeoDistLat1:testStop.latitude Lat2:latitude Lon1:testStop.longitude Lon2:longitude];
            
            NSLog(@"Checking stop: %@, %f",testStop.stopName,newDist);
            if (newDist < closestDist)
            {
                NSLog(@"Set as new closest");
                closestStop = [stopsWithinTolerance objectAtIndex:i];
                closestDist = newDist;
            }
        }
        NSLog(@"Current selection is: %@", closestStop.stopName);
    }
    NSLog(@"Search Chose: %@, %f",closestStop.stopName,closestDist);
    return closestStop;
}

- (void)dealloc {
    sqlite3_close(_database);
}

@end
