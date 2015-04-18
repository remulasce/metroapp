//
//  StopNameInfo.m
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "StopNameInfo.h"

@implementation StopNameInfo

@synthesize uniqueID;
@synthesize stopID;
@synthesize stopName;
@synthesize latitude;
@synthesize longitude;

-(id) initWithUniqueID:(int)newUniqueID
                stopID:(NSString *)newStopID
              stopName:(NSString *)newStopName
              latitude:(double)newLatitude
             longitude:(double)newLongitude
{
    self = [super init];

    uniqueID = newUniqueID;
    stopID   = newStopID;
    stopName = newStopName;
    latitude = newLatitude;
    longitude= newLongitude;
    
    return self;
}

- (void) dealloc
{
    stopName = nil;
    stopID = nil;
    // [super dealloc];
}

@end
