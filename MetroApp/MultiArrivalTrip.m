//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/types/MultiArrivalTrip.java
//

#include "Destination.h"
#include "GlobalLocationProvider.h"
#include "J2ObjC_source.h"
#include "LocationRetriever.h"
#include "MultiArrivalTrip.h"
#include "Route.h"
#include "Stop.h"
#include "StopRouteDestinationArrival.h"
#include "java/lang/Math.h"
#include "java/lang/System.h"

@interface ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip () {
 @public
  jlong lastLocationUpdate_;
  jdouble lastDistanceToStop_;
}
@end

@implementation ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip

- (instancetype)initWithComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival:(ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *)parentArrival {
  if (self = [super init]) {
    lastLocationUpdate_ = 0;
    lastDistanceToStop_ = 0;
    self->parentArrival_ = parentArrival;
  }
  return self;
}

- (NSString *)description {
  if (parentArrival_ == nil) {
    return @"Invalid parent";
  }
  ComRemulasceLametroappJava_coreBasic_typesRoute *route = [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(parentArrival_)) getRoute];
  ComRemulasceLametroappJava_coreBasic_typesStop *stop = [parentArrival_ getStop];
  ComRemulasceLametroappJava_coreBasic_typesDestination *dest = [parentArrival_ getDirection];
  NSString *routeString = [((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk(route)) getString];
  NSString *stopString = [((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk(stop)) getStopName];
  NSString *destString = [((ComRemulasceLametroappJava_coreBasic_typesDestination *) nil_chk(dest)) getString];
  jboolean destinationStartsWithNum = [((NSString *) nil_chk(destString)) hasPrefix:routeString];
  NSString *destination = JreStrcat("$$$", (destinationStartsWithNum ? @"" : JreStrcat("$$", routeString, @": ")), destString, @" \n");
  NSString *stop_ = JreStrcat("$C", stopString, 0x000a);
  return JreStrcat("$$", stop_, destination);
}

- (NSUInteger)hash {
  return ((jint) [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(parentArrival_)) hash]);
}

- (jboolean)isEqual:(id)obj {
  if (!([obj isKindOfClass:[ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip class]])) return NO;
  if (obj == self) return YES;
  jint ourCode = ((jint) [self hash]);
  jint theirCode = ((jint) [nil_chk(obj) hash]);
  return ourCode == theirCode;
}

- (jdouble)getCurrentDistanceToStop {
  id<ComRemulasceLametroappJava_coreLocationLocationRetriever> retriever = ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_getRetriever();
  if (retriever != nil && JavaLangSystem_currentTimeMillis() > lastLocationUpdate_ + 30000) {
    lastLocationUpdate_ = JavaLangSystem_currentTimeMillis();
    lastDistanceToStop_ = [retriever getCurrentDistanceToStopWithComRemulasceLametroappJava_coreBasic_typesStop:[((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(parentArrival_)) getStop]];
  }
  return lastDistanceToStop_;
}

- (jfloat)getPriority {
  jfloat proximity = 0;
  jdouble distance = [self getCurrentDistanceToStop];
  proximity += JavaLangMath_maxWithFloat_withFloat_(0, .2f * (jfloat) (1 - (distance / 32000)));
  proximity += JavaLangMath_maxWithFloat_withFloat_(0, .8f * (jfloat) (1 - (distance / 3200)));
  proximity = JavaLangMath_maxWithFloat_withFloat_(proximity, 0);
  return proximity;
}

- (jboolean)isValid {
  return [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(parentArrival_)) isInScope];
}

- (void)dismiss {
  [((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(parentArrival_)) setScopeWithBoolean:NO];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival:", "MultiArrivalTrip", NULL, 0x1, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
    { "isEqual:", "equals", "Z", 0x1, NULL },
    { "getCurrentDistanceToStop", NULL, "D", 0x1, NULL },
    { "getPriority", NULL, "F", 0x1, NULL },
    { "isValid", NULL, "Z", 0x1, NULL },
    { "dismiss", NULL, "V", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "parentArrival_", NULL, 0x11, "Lcom.remulasce.lametroapp.java_core.dynamic_data.types.StopRouteDestinationArrival;", NULL,  },
    { "lastLocationUpdate_", NULL, 0x2, "J", NULL,  },
    { "lastDistanceToStop_", NULL, 0x2, "D", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip = { 2, "MultiArrivalTrip", "com.remulasce.lametroapp.java_core.dynamic_data.types", NULL, 0x1, 8, methods, 3, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataTypesMultiArrivalTrip)