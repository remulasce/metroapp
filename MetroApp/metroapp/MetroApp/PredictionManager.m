//
//  PredictionManager.m
//  MetroApp
//
//  Created by Nighelles on 3/6/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "PredictionManager.h"

@implementation PredictionManager

#pragma mark Singleton

+ (id) getInstance
{
    static PredictionManager *sharedManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedManager = [[self alloc] init];
    });
    return sharedManager;
}
@end
