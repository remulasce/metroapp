                                                                                                                                                                                                                 //
//  StopNameDatabase.m
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "StopNameDatabase.h"

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
        NSString *sqLiteDb = [[NSBundle mainBundle] pathForResource:@"StopNames"
                                                             ofType:@"sqlite3"];
        
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

- (void)dealloc {
    sqlite3_close(_database);
}

@end
