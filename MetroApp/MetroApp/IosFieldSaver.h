//
//  IosFieldSaver.h
//  MetroApp
//
//  Created by Nighelles on 8/30/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "java/lang/Boolean.h"
#import "java/util/ArrayList.h"

@interface IosFieldSaver : NSObject

+ (IosFieldSaver*) sharedInstance;

- (void) saveServiceRequests:(NSArray*) serviceRequests;
- (NSArray*) loadServiceRequests;

- (void) saveObjectWithNSString:(NSString*)key withId:(id) object;
- (id) loadObject:(NSString*)key;

@end
