//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/analytics/Tracking.java
//

#ifndef _ComRemulasceLametroappJava_coreAnalyticsTracking_H_
#define _ComRemulasceLametroappJava_coreAnalyticsTracking_H_

@class JavaUtilHashMap;

#include "J2ObjC_header.h"

@interface ComRemulasceLametroappJava_coreAnalyticsTracking : NSObject {
}

#pragma mark Public

- (instancetype)init;

+ (void)averageUITimeWithNSString:(NSString *)name
                     withNSString:(NSString *)label
                         withLong:(jlong)startTime;

- (void)do_sendEventWithNSString:(NSString *)category
                    withNSString:(NSString *)action
                    withNSString:(NSString *)label;

- (void)do_sendRawTimeWithNSString:(NSString *)category
                      withNSString:(NSString *)name
                      withNSString:(NSString *)label
                          withLong:(jlong)timeSpent;

- (void)do_setScreenNameWithNSString:(NSString *)name;

+ (void)sendEventWithNSString:(NSString *)category
                 withNSString:(NSString *)action;

+ (void)sendEventWithNSString:(NSString *)category
                 withNSString:(NSString *)action
                 withNSString:(NSString *)label;

+ (void)sendRawUITimeWithNSString:(NSString *)name
                     withNSString:(NSString *)label
                         withLong:(jlong)timeSpent;

+ (void)sendTimeWithNSString:(NSString *)category
                withNSString:(NSString *)name
                withNSString:(NSString *)label
                    withLong:(jlong)startTime;

+ (void)sendUITimeWithNSString:(NSString *)name
                  withNSString:(NSString *)label
                      withLong:(jlong)startTime;

+ (void)setScreenNameWithNSString:(NSString *)name;

+ (void)setTrackerWithComRemulasceLametroappJava_coreAnalyticsTracking:(ComRemulasceLametroappJava_coreAnalyticsTracking *)tracker;

+ (jlong)startTime;

+ (jlong)timeSpentWithLong:(jlong)startTime;

@end

FOUNDATION_EXPORT BOOL ComRemulasceLametroappJava_coreAnalyticsTracking_initialized;
J2OBJC_STATIC_INIT(ComRemulasceLametroappJava_coreAnalyticsTracking)

FOUNDATION_EXPORT jlong ComRemulasceLametroappJava_coreAnalyticsTracking_startTime();

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_averageUITimeWithNSString_withNSString_withLong_(NSString *name, NSString *label, jlong startTime);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_setTrackerWithComRemulasceLametroappJava_coreAnalyticsTracking_(ComRemulasceLametroappJava_coreAnalyticsTracking *tracker);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_setScreenNameWithNSString_(NSString *name);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_(NSString *category, NSString *action);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_withNSString_(NSString *category, NSString *action, NSString *label);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_sendUITimeWithNSString_withNSString_withLong_(NSString *name, NSString *label, jlong startTime);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawUITimeWithNSString_withNSString_withLong_(NSString *name, NSString *label, jlong timeSpent);

FOUNDATION_EXPORT jlong ComRemulasceLametroappJava_coreAnalyticsTracking_timeSpentWithLong_(jlong startTime);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsTracking_sendTimeWithNSString_withNSString_withNSString_withLong_(NSString *category, NSString *name, NSString *label, jlong startTime);

FOUNDATION_EXPORT ComRemulasceLametroappJava_coreAnalyticsTracking *ComRemulasceLametroappJava_coreAnalyticsTracking_t_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreAnalyticsTracking, t_, ComRemulasceLametroappJava_coreAnalyticsTracking *)
J2OBJC_STATIC_FIELD_SETTER(ComRemulasceLametroappJava_coreAnalyticsTracking, t_, ComRemulasceLametroappJava_coreAnalyticsTracking *)

FOUNDATION_EXPORT JavaUtilHashMap *ComRemulasceLametroappJava_coreAnalyticsTracking_averagedValues_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreAnalyticsTracking, averagedValues_, JavaUtilHashMap *)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreAnalyticsTracking)

@interface ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum : NSObject {
 @public
  jdouble totalValue_;
  jdouble numPoints_;
}

#pragma mark Public

- (instancetype)init;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum)

#endif // _ComRemulasceLametroappJava_coreAnalyticsTracking_H_
