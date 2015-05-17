//
//  MetroWidgetItem.h
//  MetroApp
//
//  Created by Nighelles on 4/9/15.
//  Copyright (c) 2015 Nought. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MetroWidgetItem : NSObject <NSCoding>
{
@public
    NSString* _name;
    NSTimeInterval _endTime;
}
-(id)initWithName:(NSString*)name andTime:(NSTimeInterval)time;

@end

@implementation MetroWidgetItem


-(id)initWithName:(NSString*)name andTime:(NSTimeInterval)time
{
    if (self = [super init]) {
        _name = name;
        _endTime = time;
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)decoder {
    if (self = [super init]) {
        _name = [decoder decodeObjectForKey:@"name"];
        NSNumber* test = [decoder decodeObjectForKey:@"endTime"];
        _endTime = [test doubleValue];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)encoder {
    [encoder encodeObject:_name forKey:@"name"];
    NSNumber* test = [NSNumber numberWithDouble: _endTime];
    [encoder encodeObject:test forKey:@"endTime"];
}
@end
