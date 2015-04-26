//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/types/StopRouteDestinationPrediction.java
//

#include "Arrival.h"
#include "Destination.h"
#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "LaMetroUtil.h"
#include "Prediction.h"
#include "PredictionManager.h"
#include "Route.h"
#include "Stop.h"
#include "StopRouteDestinationArrival.h"
#include "StopRouteDestinationPrediction.h"
#include "Tracking.h"
#include "android/util/Log.h"
#include "java/io/IOException.h"
#include "java/io/ObjectInputStream.h"
#include "java/io/ObjectOutputStream.h"
#include "java/lang/ClassNotFoundException.h"
#include "java/lang/Exception.h"
#include "java/lang/Math.h"
#include "java/lang/StringBuilder.h"
#include "java/lang/System.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"
#include "java/util/List.h"

@interface ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction () {
 @public
  jint MINIMUM_UPDATE_INTERVAL_;
  ComRemulasceLametroappJava_coreBasic_typesStop *stop_;
  ComRemulasceLametroappJava_coreBasic_typesRoute *route_;
  id<JavaUtilCollection> trackedArrivals_;
}

- (void)writeObjectWithJavaIoObjectOutputStream:(JavaIoObjectOutputStream *)oos;

- (void)readObjectWithJavaIoObjectInputStream:(JavaIoObjectInputStream *)ois;
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction, stop_, ComRemulasceLametroappJava_coreBasic_typesStop *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction, route_, ComRemulasceLametroappJava_coreBasic_typesRoute *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction, trackedArrivals_, id<JavaUtilCollection>)

@implementation ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction

NSString * ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_ = @"SRDPrediction";

- (instancetype)initWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)stop
                   withComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)route {
  if (self = [super init]) {
    MINIMUM_UPDATE_INTERVAL_ = 5000;
    trackedArrivals_ = [[JavaUtilArrayList alloc] init];
    self->stop_ = stop;
    self->route_ = route;
  }
  return self;
}

- (void)restoreTrips {
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong arrival in nil_chk(trackedArrivals_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(arrival)) setScopeWithBoolean:YES];
  }
  needsQuickUpdate_ = YES;
}

- (void)cancelTrips {
  inScope_ = NO;
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong e in nil_chk(trackedArrivals_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(e)) setScopeWithBoolean:NO];
  }
  [self stopPredicting];
}

- (jboolean)hasAnyPredictions {
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong arrival in nil_chk(trackedArrivals_)) {
    if ([((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(arrival)) isInScope]) {
      return YES;
    }
  }
  return NO;
}

- (void)startPredicting {
  AndroidUtilLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_, @"StartPredicting SRDP");
  @synchronized(trackedArrivals_) {
    inScope_ = YES;
    inUpdate_ = NO;
    [((ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *) nil_chk(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_getInstance())) startTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:self];
  }
}

- (void)stopPredicting {
  inScope_ = NO;
  inUpdate_ = NO;
  needsQuickUpdate_ = YES;
  [((ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *) nil_chk(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_getInstance())) stopTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:self];
}

- (NSString *)getRequestString {
  return ComRemulasceLametroappJava_coreLaMetroUtil_makePredictionsRequestWithComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesRoute_(stop_, route_);
}

- (ComRemulasceLametroappJava_coreBasic_typesStop *)getStop {
  return stop_;
}

- (jlong)getTimeSinceLastUpdate {
  jlong ret = 0;
  if (inUpdate_) {
    return 0;
  }
  ret = JavaLangSystem_currentTimeMillis() - lastUpdate_;
  AndroidUtilLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_, JreStrcat("$J$$", @"Time since last update: ", ret, @" on ", [self description]));
  return ret;
}

- (jboolean)arrivalTrackedWithComRemulasceLametroappJava_coreDynamic_dataTypesArrival:(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *)a {
  if (!ComRemulasceLametroappJava_coreLaMetroUtil_isValidRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_(route_)) {
    return YES;
  }
  if ([((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(a)) getRoute])) isEqual:route_]) {
    return YES;
  }
  return NO;
}

- (void)handleResponseWithNSString:(NSString *)response {
  [super handleResponseWithNSString:response];
  id<JavaUtilList> arrivals = ComRemulasceLametroappJava_coreLaMetroUtil_parseAllArrivalsWithNSString_(response);
  if (arrivals == nil) {
    if ([((id<JavaUtilCollection>) nil_chk(self->trackedArrivals_)) size] > 0) {
      predictionState_ = ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_PredictionStateEnum_get_CACHED();
      return;
    }
    else {
      predictionState_ = ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_PredictionStateEnum_get_BAD();
      return;
    }
  }
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong newA in nil_chk(arrivals)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(newA)) setScopeWithBoolean:inScope_];
    if ([self arrivalTrackedWithComRemulasceLametroappJava_coreDynamic_dataTypesArrival:newA]) {
      ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *a = nil;
      @synchronized(trackedArrivals_) {
        for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong arrival in nil_chk(trackedArrivals_)) {
          if ([((ComRemulasceLametroappJava_coreBasic_typesDestination *) nil_chk([((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(arrival)) getDirection])) isEqual:[newA getDirection]] && [((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk([arrival getRoute])) isEqual:[newA getRoute]] && [((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk([arrival getStop])) isEqual:[newA getStop]]) {
            a = arrival;
            break;
          }
        }
      }
      if (a == nil) {
        @synchronized(trackedArrivals_) {
          ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *newSRDA = [[ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival alloc] initWithComRemulasceLametroappJava_coreBasic_typesStop:[newA getStop] withComRemulasceLametroappJava_coreBasic_typesRoute:[newA getRoute] withComRemulasceLametroappJava_coreBasic_typesDestination:[newA getDirection]];
          [newSRDA setScopeWithBoolean:inScope_];
          [trackedArrivals_ addWithId:newSRDA];
        }
      }
    }
  }
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong a in nil_chk(trackedArrivals_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(a)) updateArrivalTimesWithJavaUtilCollection:arrivals];
  }
}

- (ComRemulasceLametroappJava_coreBasic_typesRoute *)getRoute {
  return route_;
}

- (id<JavaUtilCollection>)getArrivals {
  return trackedArrivals_;
}

- (jint)getRequestedUpdateInterval {
  if (needsQuickUpdate_) {
    return 0;
  }
  ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *first = nil;
  jfloat interval;
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong a in nil_chk(trackedArrivals_)) {
    if (first == nil || [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(a)) getRequestedUpdateInterval] < [first getRequestedUpdateInterval]) {
      if ([((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(a)) getRequestedUpdateInterval] != -1) {
        first = a;
      }
    }
  }
  if (first == nil) {
    interval = 30 * 1000;
  }
  else {
    interval = [first getRequestedUpdateInterval];
  }
  AndroidUtilLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_, JreStrcat("$F", @"GetRequestedUpdateInterval SRDArrival ", interval));
  return J2ObjCFpToInt(JavaLangMath_maxWithFloat_withFloat_(MINIMUM_UPDATE_INTERVAL_, interval));
}

- (NSUInteger)hash {
  JavaLangStringBuilder *build = [[JavaLangStringBuilder alloc] init];
  if (stop_ != nil) {
    (void) [build appendWithNSString:[stop_ getString]];
  }
  if (route_ != nil) {
    (void) [build appendWithNSString:[route_ getString]];
  }
  if ([build length] == 0) {
    AndroidUtilLog_eWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_, @"Hashcode had nothing to hash");
    ComRemulasceLametroappJava_coreAnalyticsTracking_sendEventWithNSString_withNSString_withNSString_(@"Errors", @"StopRouteDestinationPrediction", @"Hashcode had nothing to hash");
  }
  return ((jint) [((NSString *) nil_chk(([build description]))) hash]);
}

- (void)writeObjectWithJavaIoObjectOutputStream:(JavaIoObjectOutputStream *)oos {
  [((JavaIoObjectOutputStream *) nil_chk(oos)) writeObjectWithId:stop_];
  [oos writeObjectWithId:route_];
  [oos writeObjectWithId:trackedArrivals_];
  [oos writeBooleanWithBoolean:inScope_];
}

- (void)readObjectWithJavaIoObjectInputStream:(JavaIoObjectInputStream *)ois {
  stop_ = (ComRemulasceLametroappJava_coreBasic_typesStop *) check_class_cast([((JavaIoObjectInputStream *) nil_chk(ois)) readObject], [ComRemulasceLametroappJava_coreBasic_typesStop class]);
  route_ = (ComRemulasceLametroappJava_coreBasic_typesRoute *) check_class_cast([ois readObject], [ComRemulasceLametroappJava_coreBasic_typesRoute class]);
  @try {
    trackedArrivals_ = (id<JavaUtilCollection>) check_protocol_cast([ois readObject], @protocol(JavaUtilCollection));
  }
  @catch (JavaLangException *e) {
    trackedArrivals_ = [[JavaUtilArrayList alloc] init];
    [((JavaLangException *) nil_chk(e)) printStackTrace];
    AndroidUtilLog_wWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_, @"Couldn't load tracked arrivals, making empty list");
  }
  inScope_ = [ois readBoolean];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithComRemulasceLametroappJava_coreBasic_typesStop:withComRemulasceLametroappJava_coreBasic_typesRoute:", "StopRouteDestinationPrediction", NULL, 0x1, NULL },
    { "restoreTrips", NULL, "V", 0x1, NULL },
    { "cancelTrips", NULL, "V", 0x1, NULL },
    { "hasAnyPredictions", NULL, "Z", 0x1, NULL },
    { "startPredicting", NULL, "V", 0x1, NULL },
    { "stopPredicting", NULL, "V", 0x1, NULL },
    { "getRequestString", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getStop", NULL, "Lcom.remulasce.lametroapp.java_core.basic_types.Stop;", 0x1, NULL },
    { "getTimeSinceLastUpdate", NULL, "J", 0x1, NULL },
    { "arrivalTrackedWithComRemulasceLametroappJava_coreDynamic_dataTypesArrival:", "arrivalTracked", "Z", 0x0, NULL },
    { "handleResponseWithNSString:", "handleResponse", "V", 0x1, NULL },
    { "getRoute", NULL, "Lcom.remulasce.lametroapp.java_core.basic_types.Route;", 0x1, NULL },
    { "getArrivals", NULL, "Ljava.util.Collection;", 0x1, NULL },
    { "getRequestedUpdateInterval", NULL, "I", 0x1, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
    { "writeObjectWithJavaIoObjectOutputStream:", "writeObject", "V", 0x2, "Ljava.io.IOException;" },
    { "readObjectWithJavaIoObjectInputStream:", "readObject", "V", 0x2, "Ljava.lang.ClassNotFoundException;Ljava.io.IOException;" },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "TAG_", NULL, 0x1a, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction_TAG_,  },
    { "MINIMUM_UPDATE_INTERVAL_", NULL, 0x12, "I", NULL,  },
    { "stop_", NULL, 0x2, "Lcom.remulasce.lametroapp.java_core.basic_types.Stop;", NULL,  },
    { "route_", NULL, 0x2, "Lcom.remulasce.lametroapp.java_core.basic_types.Route;", NULL,  },
    { "trackedArrivals_", NULL, 0x2, "Ljava.util.Collection;", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction = { 2, "StopRouteDestinationPrediction", "com.remulasce.lametroapp.java_core.dynamic_data.types", NULL, 0x1, 17, methods, 5, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction)
