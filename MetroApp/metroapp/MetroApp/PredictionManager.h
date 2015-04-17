//
//  PredictionManager.h
//  MetroApp
//
//  Created by Nighelles on 3/6/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PredictionManager : NSObject
{

    int UPDATE_INTERVAL;
    
    NSMutableArray *trackingList;

}

+ (id) getInstance;


@end
