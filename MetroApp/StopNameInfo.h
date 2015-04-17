//
//  StopNameInfo.h
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface StopNameInfo : NSObject
{
    int uniqueID;
    NSString *stopID;
    NSString *stopName;
    double   latitude;
    double   longitude;
}

@property(assign) int uniqueID;
@property(nonatomic, copy) NSString *stopID;
@property(nonatomic, copy) NSString *stopName;
@property(assign) double latitude;
@property(assign) double longitude;

-(id) initWithUniqueID:(int)uniqueID
                stopID:(NSString *)stopID
              stopName:(NSString *)stopName
              latitude:(double)latitude
             longitude:(double)longitude;

@end
