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
#import "Agency.h"
#import "BasicLocation.h"
#import "Stop.h"

#import "LocationUtil.h"

@interface StopNameDatabase : NSObject {
    sqlite3 *_database;
@public
    ComRemulasceLametroappJava_coreBasic_typesAgency *_agency;
}


// Change this so they return Stops

- (id) initWithFilename:(NSString*)fileName;
- (NSArray *)getStopsByNameFragment:(NSString*)stopNameFragment;
- (NSArray*)getStopsByLat:(float)latitude Long:(float)longitude Tol:(float)tolerance;
- (ComRemulasceLametroappJava_coreBasic_typesStop*)getClosestStopLat:(float)latitude Long:(float)longitude Tol:(float)tolerance;

@end
