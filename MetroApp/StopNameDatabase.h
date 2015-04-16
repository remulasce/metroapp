//
//  StopNameDatabase.h
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

#import "StopNameInfo.h"

@interface StopNameDatabase : NSObject {
    sqlite3 *_database;
}

+ (StopNameDatabase*)database;
- (NSArray *)getStopsByName:(NSString*)stopNameFragment;
- (NSArray *)getStopsByNameFragment:(NSString*)stopNameFragment;
-(NSArray*)getStopsByLat:(float)latitude Long:(float)longitude Tol:(float)tolerance;
-(StopNameInfo*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance;

@end
