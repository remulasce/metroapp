//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/types/StopRouteDestinationArrival.java
//

#include "Arrival.h"
#include "Destination.h"
#include "J2ObjC_source.h"
#include "Log.h"
#include "MultiArrivalTrip.h"
#include "Route.h"
#include "Stop.h"
#include "StopRouteDestinationArrival.h"
#include "Trip.h"
#include "Vehicle.h"
#include "java/lang/Math.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"
#include "java/util/Collections.h"
#include "java/util/List.h"
#include "java/util/concurrent/CopyOnWriteArrayList.h"

__attribute__((unused)) static id<JavaUtilCollection> ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_sortedArrivals(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *self);

@interface ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival () {
 @public
  jint MINIMUM_UPDATE_INTERVAL_;
  jint MAXIMUM_UPDATE_INTERVAL_;
  jint INTERVAL_INCREASE_PER_SECOND_;
  id<JavaUtilCollection> arrivals_;
  ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *trip_;
  jboolean isInScope__;
}

- (id<JavaUtilCollection>)sortedArrivals;
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival, arrivals_, id<JavaUtilCollection>)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival, trip_, ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *)

@implementation ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival

NSString * ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_TAG_ = @"SRDArrival";

- (instancetype)initWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)s
                   withComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)r
             withComRemulasceLametroappJava_coreBasic_typesDestination:(ComRemulasceLametroappJava_coreBasic_typesDestination *)d {
  if (self = [super init]) {
    MINIMUM_UPDATE_INTERVAL_ = 10000;
    MAXIMUM_UPDATE_INTERVAL_ = 60000;
    INTERVAL_INCREASE_PER_SECOND_ = 400;
    isInScope__ = NO;
    self->stop_ = s;
    self->route_ = r;
    self->destination_ = d;
    arrivals_ = [[JavaUtilConcurrentCopyOnWriteArrayList alloc] init];
    trip_ = [[ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip alloc] initWithComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival:self];
    ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_TAG_, JreStrcat("$@C@C@", @"New StopRouteDestinationArrival: ", s, ' ', r, ' ', d));
  }
  return self;
}

- (jfloat)getRequestedUpdateInterval {
  ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *first = nil;
  jfloat firstTime;
  jfloat interval;
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong a in nil_chk(arrivals_)) {
    if (first == nil || [((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(a)) getEstimatedArrivalSeconds] < [first getEstimatedArrivalSeconds]) {
      if ([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(a)) getEstimatedArrivalSeconds] >= 0) {
        first = a;
      }
    }
  }
  if (first == nil) {
    firstTime = 15;
  }
  else {
    firstTime = [first getEstimatedArrivalSeconds];
  }
  interval = JavaLangMath_maxWithFloat_withFloat_(MINIMUM_UPDATE_INTERVAL_, firstTime * INTERVAL_INCREASE_PER_SECOND_);
  interval = JavaLangMath_minWithFloat_withFloat_(MAXIMUM_UPDATE_INTERVAL_, interval);
  return interval;
}

- (void)updateArrivalTimesWithJavaUtilCollection:(id<JavaUtilCollection>)updatedArrivals {
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_TAG_, JreStrcat("$I$", @"Updating SRDArrival times from ", [((id<JavaUtilCollection>) nil_chk(updatedArrivals)) size], @" arrivals"));
  id<JavaUtilList> arrivalsToDelete = [[JavaUtilArrayList alloc] init];
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong update in updatedArrivals) {
    if ([((ComRemulasceLametroappJava_coreBasic_typesDestination *) nil_chk([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(update)) getDirection])) isEqual:destination_] && [((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk([update getRoute])) isEqual:route_] && [((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk([update getStop])) isEqual:stop_]) {
      ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *a = nil;
      for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong arrival in nil_chk(arrivals_)) {
        if ([((ComRemulasceLametroappJava_coreBasic_typesVehicle *) nil_chk([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(arrival)) getVehicleNum])) isEqual:[update getVehicleNum]]) {
          a = arrival;
          break;
        }
      }
      if (a != nil && [update getEstimatedArrivalSeconds] <= 0) {
        [arrivalsToDelete addWithId:a];
      }
      else {
        if (a == nil) {
          a = [[ComRemulasceLametroappJava_coreDynamic_dataTypesArrival alloc] init];
          [a setRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:route_];
          [a setStopWithComRemulasceLametroappJava_coreBasic_typesStop:stop_];
          [a setDestinationWithComRemulasceLametroappJava_coreBasic_typesDestination:destination_];
          [a setVehicleWithComRemulasceLametroappJava_coreBasic_typesVehicle:[update getVehicleNum]];
          [a setScopeWithBoolean:isInScope__];
          [arrivals_ addWithId:a];
        }
        [((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(a)) setEstimatedArrivalSecondsWithFloat:[update getEstimatedArrivalSeconds]];
      }
    }
  }
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong arrival in nil_chk(arrivals_)) {
    if ([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(arrival)) getEstimatedArrivalSeconds] <= 0) {
      [arrivalsToDelete addWithId:arrival];
    }
  }
  [arrivals_ removeAllWithJavaUtilCollection:arrivalsToDelete];
}

- (id<JavaUtilCollection>)sortedArrivals {
  return ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_sortedArrivals(self);
}

- (id<JavaUtilCollection>)getArrivals {
  return ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_sortedArrivals(self);
}

- (ComRemulasceLametroappJava_coreBasic_typesRoute *)getRoute {
  return route_;
}

- (ComRemulasceLametroappJava_coreBasic_typesStop *)getStop {
  return stop_;
}

- (ComRemulasceLametroappJava_coreBasic_typesDestination *)getDirection {
  return destination_;
}

- (ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *)getTrip {
  return trip_;
}

- (void)setScopeWithBoolean:(jboolean)inScope {
  self->isInScope__ = inScope;
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong a in nil_chk(arrivals_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(a)) setScopeWithBoolean:inScope];
  }
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_TAG_, JreStrcat("$Z", @"Setting scopes: ", inScope));
}

- (jboolean)isInScope {
  return isInScope__;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithComRemulasceLametroappJava_coreBasic_typesStop:withComRemulasceLametroappJava_coreBasic_typesRoute:withComRemulasceLametroappJava_coreBasic_typesDestination:", "StopRouteDestinationArrival", NULL, 0x1, NULL },
    { "getRequestedUpdateInterval", NULL, "F", 0x1, NULL },
    { "updateArrivalTimesWithJavaUtilCollection:", "updateArrivalTimes", "V", 0x1, NULL },
    { "sortedArrivals", NULL, "Ljava.util.Collection;", 0x2, NULL },
    { "getArrivals", NULL, "Ljava.util.Collection;", 0x1, NULL },
    { "getRoute", NULL, "Lcom.remulasce.lametroapp.java_core.basic_types.Route;", 0x1, NULL },
    { "getStop", NULL, "Lcom.remulasce.lametroapp.java_core.basic_types.Stop;", 0x1, NULL },
    { "getDirection", NULL, "Lcom.remulasce.lametroapp.java_core.basic_types.Destination;", 0x1, NULL },
    { "getTrip", NULL, "Lcom.remulasce.lametroapp.java_core.dynamic_data.types.Trip;", 0x1, NULL },
    { "setScopeWithBoolean:", "setScope", "V", 0x1, NULL },
    { "isInScope", NULL, "Z", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "MINIMUM_UPDATE_INTERVAL_", NULL, 0x12, "I", NULL,  },
    { "MAXIMUM_UPDATE_INTERVAL_", NULL, 0x12, "I", NULL,  },
    { "INTERVAL_INCREASE_PER_SECOND_", NULL, 0x12, "I", NULL,  },
    { "TAG_", NULL, 0x1a, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_TAG_,  },
    { "stop_", NULL, 0x10, "Lcom.remulasce.lametroapp.java_core.basic_types.Stop;", NULL,  },
    { "route_", NULL, 0x10, "Lcom.remulasce.lametroapp.java_core.basic_types.Route;", NULL,  },
    { "destination_", NULL, 0x10, "Lcom.remulasce.lametroapp.java_core.basic_types.Destination;", NULL,  },
    { "arrivals_", NULL, 0x12, "Ljava.util.Collection;", NULL,  },
    { "trip_", NULL, 0x12, "Lcom.remulasce.lametroapp.java_core.dynamic_data.types.Trip;", NULL,  },
    { "isInScope__", "isInScope", 0x2, "Z", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival = { 2, "StopRouteDestinationArrival", "com.remulasce.lametroapp.java_core.dynamic_data.types", NULL, 0x1, 11, methods, 10, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival;
}

@end

id<JavaUtilCollection> ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_sortedArrivals(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *self) {
  id<JavaUtilList> sorted = [[JavaUtilArrayList alloc] initWithJavaUtilCollection:self->arrivals_];
  JavaUtilCollections_sortWithJavaUtilList_withJavaUtilComparator_(sorted, [[ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_$1 alloc] init]);
  return sorted;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival)

@implementation ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_$1

- (jint)compareWithId:(id)o
               withId:(id)o2 {
  ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *a = (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) check_class_cast(o, [ComRemulasceLametroappJava_coreDynamic_dataTypesArrival class]);
  ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *b = (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) check_class_cast(o2, [ComRemulasceLametroappJava_coreDynamic_dataTypesArrival class]);
  if ([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(a)) getEstimatedArrivalSeconds] < [((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(b)) getEstimatedArrivalSeconds]) {
    return -1;
  }
  else if ([a getEstimatedArrivalSeconds] > [b getEstimatedArrivalSeconds]) {
    return 1;
  }
  return 0;
}

- (instancetype)init {
  return [super init];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "compareWithId:withId:", "compare", "I", 0x1, NULL },
    { "init", NULL, NULL, 0x0, NULL },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_$1 = { 2, "", "com.remulasce.lametroapp.java_core.dynamic_data.types", "StopRouteDestinationArrival", 0x8008, 2, methods, 0, NULL, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_$1;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival_$1)
