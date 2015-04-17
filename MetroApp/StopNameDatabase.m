                                                                                                                                                                                                                 //
//  StopNameDatabase.m
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "StopNameDatabase.h"
#import "regionalization.h"

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
                                                             ofType:@"sqlite3"];
#endif
#if REGIONSANFRANCISCO
        NSString *sqLiteDb = [[NSBundle mainBundle] pathForResource:@"actransit"
                                                             ofType:@"sqlite3"];
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
    NSMutableArray *result = [[NSMutableArray alloc] init];
    NSString *query = [NSString stringWithFormat:@"SELECT * FROM stopnames WHERE stopname LIKE '%%%@%%'",stopNameFragment];
    
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
-(StopNameInfo*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance
{
    NSArray *stopsWithinTolerance = [self getStopsByLat:latitude Long:longitude Tol:tolerance];
    
    StopNameInfo* closestStop = nil;
    double closestDist = 0;
    double newDist = 0;
    
    for (StopNameInfo *testStop in stopsWithinTolerance) {
        if (closestStop == nil)
        {
            closestStop = testStop;
            // This is not actually a distance, but we don't care, because we're just comparing
            // No need for an expensive square root
            closestDist = (closestStop.latitude-latitude)*(closestStop.latitude-latitude) +
                    (closestStop.longitude-longitude)*(closestStop.longitude-longitude);
        } else {
            newDist = (testStop.latitude-latitude)*(testStop.latitude-latitude) +
                    (testStop.longitude-longitude)*(testStop.longitude-longitude);
            
            if (newDist < closestDist)
            {
                closestStop = testStop;
            }
        }
    }
    return closestStop;
}

- (void)dealloc {
    sqlite3_close(_database);
}

@end
