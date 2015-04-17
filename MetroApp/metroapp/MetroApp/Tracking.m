//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/analytics/Tracking.java
//

#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "Log.h"
#include "Tracking.h"
#include "java/lang/System.h"
#include "java/util/HashMap.h"

__attribute__((unused)) static void ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawTimeWithNSString_withNSString_withNSString_withLong_(NSString *category, NSString *name, NSString *label, jlong timeSpent);

@interface ComRemulasceLametroappJava_coreAnalyticsTracking () {
}

+ (void)sendRawTimeWithNSString:(NSString *)category
                   withNSString:(NSString *)name
                   withNSString:(NSString *)label
                       withLong:(jlong)timeSpent;
@end

BOOL ComRemulasceLametroappJava_coreAnalyticsTracking_initialized = NO;

@implementation ComRemulasceLametroappJava_coreAnalyticsTracking

ComRemulasceLametroappJava_coreAnalyticsTracking * ComRemulasceLametroappJava_coreAnalyticsTracking_t_;
JavaUtilHashMap * ComRemulasceLametroappJava_coreAnalyticsTracking_averagedValues_;

+ (jlong)startTime {
  return ComRemulasceLametroappJava_coreAnalyticsTracking_startTime();
}

+ (void)averageUITimeWithNSString:(NSString *)name
                     withNSString:(NSString *)label
                         withLong:(jlong)startTime {
  ComRemulasceLametroappJava_coreAnalyticsTracking_averageUITimeWithNSString_withNSString_withLong_(name, label, startTime);
}

+ (void)setTrackerWithComRemulasceLametroappJava_coreAnalyticsTracking:(ComRemulasceLametroappJava_coreAnalyticsTracking *)tracker {
  ComRemulasceLametroappJava_coreAnalyticsTracking_setTrackerWithComRemulasceLametroappJava_coreAnalyticsTracking_(tracker);
}

+ (void)setScreenNameWithNSString:(NSString *)name {
  ComRemulasceLametroappJava_coreAnalyticsTracking_setScreenNameWithNSString_(name);
}

+ (void)sendEventWithNSString:(NSString *)category
                 withNSString:(NSString *)action {
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_(category, action);
}

+ (void)sendEventWithNSString:(NSString *)category
                 withNSString:(NSString *)action
                 withNSString:(NSString *)label {
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_withNSString_(category, action, label);
}

+ (void)sendUITimeWithNSString:(NSString *)name
                  withNSString:(NSString *)label
                      withLong:(jlong)startTime {
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendUITimeWithNSString_withNSString_withLong_(name, label, startTime);
}

+ (void)sendRawUITimeWithNSString:(NSString *)name
                     withNSString:(NSString *)label
                         withLong:(jlong)timeSpent {
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawUITimeWithNSString_withNSString_withLong_(name, label, timeSpent);
}

+ (jlong)timeSpentWithLong:(jlong)startTime {
  return ComRemulasceLametroappJava_coreAnalyticsTracking_timeSpentWithLong_(startTime);
}

+ (void)sendTimeWithNSString:(NSString *)category
                withNSString:(NSString *)name
                withNSString:(NSString *)label
                    withLong:(jlong)startTime {
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendTimeWithNSString_withNSString_withNSString_withLong_(category, name, label, startTime);
}

+ (void)sendRawTimeWithNSString:(NSString *)category
                   withNSString:(NSString *)name
                   withNSString:(NSString *)label
                       withLong:(jlong)timeSpent {
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawTimeWithNSString_withNSString_withNSString_withLong_(category, name, label, timeSpent);
}

- (void)do_setScreenNameWithNSString:(NSString *)name {
}

- (void)do_sendRawTimeWithNSString:(NSString *)category
                      withNSString:(NSString *)name
                      withNSString:(NSString *)label
                          withLong:(jlong)timeSpent {
}

- (void)do_sendEventWithNSString:(NSString *)category
                    withNSString:(NSString *)action
                    withNSString:(NSString *)label {
}

- (instancetype)init {
  return [super init];
}

+ (void)initialize {
  if (self == [ComRemulasceLametroappJava_coreAnalyticsTracking class]) {
    ComRemulasceLametroappJava_coreAnalyticsTracking_averagedValues_ = [[JavaUtilHashMap alloc] init];
    J2OBJC_SET_INITIALIZED(ComRemulasceLametroappJava_coreAnalyticsTracking)
  }
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "startTime", NULL, "J", 0x9, NULL },
    { "averageUITimeWithNSString:withNSString:withLong:", "averageUITime", "V", 0x9, NULL },
    { "setTrackerWithComRemulasceLametroappJava_coreAnalyticsTracking:", "setTracker", "V", 0x9, NULL },
    { "setScreenNameWithNSString:", "setScreenName", "V", 0x9, NULL },
    { "sendEventWithNSString:withNSString:", "sendEvent", "V", 0x9, NULL },
    { "sendEventWithNSString:withNSString:withNSString:", "sendEvent", "V", 0x9, NULL },
    { "sendUITimeWithNSString:withNSString:withLong:", "sendUITime", "V", 0x9, NULL },
    { "sendRawUITimeWithNSString:withNSString:withLong:", "sendRawUITime", "V", 0x9, NULL },
    { "timeSpentWithLong:", "timeSpent", "J", 0x9, NULL },
    { "sendTimeWithNSString:withNSString:withNSString:withLong:", "sendTime", "V", 0x9, NULL },
    { "sendRawTimeWithNSString:withNSString:withNSString:withLong:", "sendRawTime", "V", 0xa, NULL },
    { "do_setScreenNameWithNSString:", "do_setScreenName", "V", 0x1, NULL },
    { "do_sendRawTimeWithNSString:withNSString:withNSString:withLong:", "do_sendRawTime", "V", 0x1, NULL },
    { "do_sendEventWithNSString:withNSString:withNSString:", "do_sendEvent", "V", 0x1, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "t_", NULL, 0xa, "Lcom.remulasce.lametroapp.java_core.analytics.Tracking;", &ComRemulasceLametroappJava_coreAnalyticsTracking_t_,  },
    { "averagedValues_", NULL, 0x1a, "Ljava.util.HashMap;", &ComRemulasceLametroappJava_coreAnalyticsTracking_averagedValues_,  },
  };
  static const char *inner_classes[] = {"Lcom.remulasce.lametroapp.java_core.analytics.Tracking$AveragedDatum;"};
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreAnalyticsTracking = { 2, "Tracking", "com.remulasce.lametroapp.java_core.analytics", NULL, 0x1, 15, methods, 2, fields, 0, NULL, 1, inner_classes};
  return &_ComRemulasceLametroappJava_coreAnalyticsTracking;
}

@end

jlong ComRemulasceLametroappJava_coreAnalyticsTracking_startTime() {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  return JavaLangSystem_nanoTime();
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_averageUITimeWithNSString_withNSString_withLong_(NSString *name, NSString *label, jlong startTime) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum *data;
  JavaUtilHashMap *labels = [((JavaUtilHashMap *) nil_chk(ComRemulasceLametroappJava_coreAnalyticsTracking_averagedValues_)) getWithId:name];
  if (labels == nil) {
    labels = [[JavaUtilHashMap alloc] init];
    (void) [ComRemulasceLametroappJava_coreAnalyticsTracking_averagedValues_ putWithId:name withId:labels];
  }
  data = [((JavaUtilHashMap *) nil_chk(labels)) getWithId:label];
  if (data == nil) {
    data = [[ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum alloc] init];
    (void) [labels putWithId:label withId:data];
  }
  ((ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum *) nil_chk(data))->totalValue_ += ComRemulasceLametroappJava_coreAnalyticsTracking_timeSpentWithLong_(startTime);
  data->numPoints_ += 1;
  if (data->numPoints_ >= 1000) {
    @synchronized(data) {
      if (data->numPoints_ > 0) {
        ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawUITimeWithNSString_withNSString_withLong_(name, label, J2ObjCFpToLong((data->totalValue_ / data->numPoints_)));
      }
      data->numPoints_ = 0;
      data->totalValue_ = 0;
    }
  }
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_setTrackerWithComRemulasceLametroappJava_coreAnalyticsTracking_(ComRemulasceLametroappJava_coreAnalyticsTracking *tracker) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  ComRemulasceLametroappJava_coreAnalyticsTracking_t_ = tracker;
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_setScreenNameWithNSString_(NSString *name) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  if (ComRemulasceLametroappJava_coreAnalyticsTracking_t_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsTracking_t_ do_setScreenNameWithNSString:name];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_(NSString *category, NSString *action) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  if (ComRemulasceLametroappJava_coreAnalyticsTracking_t_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsTracking_t_ do_sendEventWithNSString:category withNSString:action withNSString:nil];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_withNSString_(NSString *category, NSString *action, NSString *label) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  if (ComRemulasceLametroappJava_coreAnalyticsTracking_t_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsTracking_t_ do_sendEventWithNSString:category withNSString:action withNSString:label];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_sendUITimeWithNSString_withNSString_withLong_(NSString *name, NSString *label, jlong startTime) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendTimeWithNSString_withNSString_withNSString_withLong_(@"UITiming", name, label, startTime);
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawUITimeWithNSString_withNSString_withLong_(NSString *name, NSString *label, jlong timeSpent) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawTimeWithNSString_withNSString_withNSString_withLong_(@"UITiming", name, label, timeSpent);
}

jlong ComRemulasceLametroappJava_coreAnalyticsTracking_timeSpentWithLong_(jlong startTime) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  return (JavaLangSystem_nanoTime() - startTime) / 1000000;
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_sendTimeWithNSString_withNSString_withNSString_withLong_(NSString *category, NSString *name, NSString *label, jlong startTime) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawTimeWithNSString_withNSString_withNSString_withLong_(category, name, label, ComRemulasceLametroappJava_coreAnalyticsTracking_timeSpentWithLong_(startTime));
}

void ComRemulasceLametroappJava_coreAnalyticsTracking_sendRawTimeWithNSString_withNSString_withNSString_withLong_(NSString *category, NSString *name, NSString *label, jlong timeSpent) {
  ComRemulasceLametroappJava_coreAnalyticsTracking_init();
  ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(category, JreStrcat("$C$$J", name, ' ', label, @": ", timeSpent));
  if (ComRemulasceLametroappJava_coreAnalyticsTracking_t_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsTracking_t_ do_sendRawTimeWithNSString:category withNSString:name withNSString:label withLong:timeSpent];
  }
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreAnalyticsTracking)

@implementation ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum

- (instancetype)init {
  if (self = [super init]) {
    totalValue_ = 0;
    numPoints_ = 0;
  }
  return self;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "totalValue_", NULL, 0x1, "D", NULL,  },
    { "numPoints_", NULL, 0x1, "D", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum = { 2, "AveragedDatum", "com.remulasce.lametroapp.java_core.analytics", "Tracking", 0x9, 1, methods, 2, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreAnalyticsTracking_AveragedDatum)
