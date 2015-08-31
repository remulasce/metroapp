//
//  IosFieldSaver.m
//  MetroApp
//
//  Created by Nighelles on 8/30/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import "IosFieldSaver.h"

@implementation IosFieldSaver

+ (IosFieldSaver*) sharedInstance
{
    static IosFieldSaver* _sharedInstance = nil;
    static dispatch_once_t onceSecurePredicate;
    dispatch_once(&onceSecurePredicate,
                  ^{
                      _sharedInstance = [[self alloc] init];
                  });
    
    return _sharedInstance;
}

- (void) saveServiceRequests:(NSArray*) serviceRequests
{
    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:serviceRequests];
    
    [[NSUserDefaults standardUserDefaults] setObject:data forKey:@"serviceRequests"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSArray*) loadServiceRequests
{

    NSData* serviceRequestsData = [[NSUserDefaults standardUserDefaults] objectForKey:@"serviceRequests"];
    NSArray *serviceRequests = [NSKeyedUnarchiver unarchiveObjectWithData:serviceRequestsData];
    
    return serviceRequests;
}

- (void) saveObjectWithNSString:(NSString*)key withId:(id) object
{
    if ([object class] == [JavaLangBoolean class]) {
        NSNumber* booleanInteger = [[NSNumber alloc] initWithBool: [(JavaLangBoolean*)object booleanValue]];
        
        [[NSUserDefaults standardUserDefaults] setObject:booleanInteger forKey:key];
        
    }else if ([object class] == [JavaUtilArrayList class]) {
        for (int i = 0; i<[(JavaUtilArrayList*)object size]; i++) {
            id j = [(JavaUtilArrayList*)object getWithInt:i];
            NSLog(@"Trying to save JavaUtilArrayList with type %@",[j class]);
            
            //[NSException raise:@"UNDER CONSTRUCTION" format:@"Tried to call saveObjectWithNSString on JavaUtilArrayList"];
        }
    }else {
        NSLog(@"Cannot save persistance object");
        //[NSException raise:@"IosFieldSaver cannot encode" format:@"Tried to call saveObjectWithNSString on unsupported object"];
    }
}
- (id) loadObject:(NSString*)key
{
    NSData* objectData = [[NSUserDefaults standardUserDefaults] objectForKey:key];
    
    id object = [NSKeyedUnarchiver unarchiveObjectWithData:objectData];
    
    return object;
}

@end
