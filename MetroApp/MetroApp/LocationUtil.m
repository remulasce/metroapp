//
//  LocationUtil.m
//  MetroApp
//
//  Created by Nighelles on 8/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "LocationUtil.h"

@implementation LocationUtil

+ (double)GeoDistLat1:(double)lat1 Lat2:(double)lat2 Lon1:(double)lon1 Lon2:(double)lon2
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

@end
