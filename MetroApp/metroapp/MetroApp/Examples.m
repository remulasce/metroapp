//
//  Examples.m
//  MetroApp
//
//  Created by Nighelles on 3/29/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTTPGetter.h"

-(void) examples
{
     ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter* getter;
     getter = [[ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter alloc] init];
     NSLog([getter doGetHTTPResponseWithNSString:@"http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=lametro-rail&stopId=80122" withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:nil]);
}