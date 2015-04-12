//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/ServiceRequest.java
//

#ifndef _ComRemulasceLametroappJava_coreBasic_typesServiceRequest_H_
#define _ComRemulasceLametroappJava_coreBasic_typesServiceRequest_H_

@class ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum;
@protocol JavaUtilCollection;

#include "J2ObjC_header.h"
#include "java/io/Serializable.h"
#include "java/lang/Enum.h"

@interface ComRemulasceLametroappJava_coreBasic_typesServiceRequest : NSObject < JavaIoSerializable > {
 @public
  NSString *displayName_;
  ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *lifecycleState_;
}

#pragma mark Public

- (instancetype)init;

- (instancetype)initWithNSString:(NSString *)s;

- (void)cancelRequest;

- (void)descope;

- (NSString *)getDisplayName;

- (ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *)getLifecycleState;

- (id<JavaUtilCollection>)getRaw;

- (id<JavaUtilCollection>)getTrips;

- (jboolean)hasTripsToDisplay;

- (jboolean)isInScope;

- (jboolean)isValid;

- (void)pauseRequest;

- (void)restoreTrips;

- (void)setDisplayNameWithNSString:(NSString *)displayName;

- (void)startRequest;

- (NSString *)description;

- (jboolean)updateAvailable;

- (void)updateTaken;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreBasic_typesServiceRequest)

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest, displayName_, NSString *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest, lifecycleState_, ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest)

typedef NS_ENUM(NSUInteger, ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState) {
  ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState_STOPPED = 0,
  ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState_PAUSED = 1,
  ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState_RUNNING = 2,
};

@interface ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum : JavaLangEnum < NSCopying > {
}

#pragma mark Public

- (instancetype)initWithNSString:(NSString *)__name
                         withInt:(jint)__ordinal;

+ (IOSObjectArray *)values;
FOUNDATION_EXPORT IOSObjectArray *ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values();

+ (ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *)valueOfWithNSString:(NSString *)name;

FOUNDATION_EXPORT ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_valueOfWithNSString_(NSString *name);
- (id)copyWithZone:(NSZone *)zone;

@end

FOUNDATION_EXPORT BOOL ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_initialized;
J2OBJC_STATIC_INIT(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum)

FOUNDATION_EXPORT ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_[];

#define ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_STOPPED ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_[ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState_STOPPED]
J2OBJC_ENUM_CONSTANT_GETTER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum, STOPPED)

#define ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_PAUSED ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_[ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState_PAUSED]
J2OBJC_ENUM_CONSTANT_GETTER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum, PAUSED)

#define ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_RUNNING ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_[ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleState_RUNNING]
J2OBJC_ENUM_CONSTANT_GETTER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum, RUNNING)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum)

#endif // _ComRemulasceLametroappJava_coreBasic_typesServiceRequest_H_