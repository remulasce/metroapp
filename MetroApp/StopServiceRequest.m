//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/StopServiceRequest.java
//

#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "Log.h"
#include "Prediction.h"
#include "Stop.h"
#include "StopRouteDestinationArrival.h"
#include "StopRouteDestinationPrediction.h"
#include "StopServiceRequest.h"
#include "Trip.h"
#include "java/io/IOException.h"
#include "java/io/ObjectInputStream.h"
#include "java/io/ObjectOutputStream.h"
#include "java/lang/ClassNotFoundException.h"
#include "java/lang/Exception.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"

__attribute__((unused)) static void ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_makePredictions(ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest *self);

@interface ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest () {
 @public
  id<JavaUtilCollection> stops_;
  id<JavaUtilCollection> predictions_;
  jboolean updateAvailable__;
}

- (void)makePredictions;

- (void)writeObjectWithJavaIoObjectOutputStream:(JavaIoObjectOutputStream *)oos;

- (void)readObjectWithJavaIoObjectInputStream:(JavaIoObjectInputStream *)ois;
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest, stops_, id<JavaUtilCollection>)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest, predictions_, id<JavaUtilCollection>)

@implementation ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest

NSString * ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_TAG_ = @"StopServiceRequest";

- (instancetype)initWithJavaUtilCollection:(id<JavaUtilCollection>)stops
                              withNSString:(NSString *)displayName {
  if (self = [super init]) {
    predictions_ = [[JavaUtilArrayList alloc] init];
    updateAvailable__ = YES;
    self->stops_ = stops;
    self->displayName_ = displayName;
  }
  return self;
}

- (instancetype)initWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)stop
                                                          withNSString:(NSString *)displayName {
  if (self = [super init]) {
    predictions_ = [[JavaUtilArrayList alloc] init];
    updateAvailable__ = YES;
    stops_ = [[JavaUtilArrayList alloc] init];
    [stops_ addWithId:stop];
    self->displayName_ = displayName;
  }
  return self;
}

- (jboolean)isValid {
  if (stops_ == nil || [stops_ isEmpty] || [stops_ containsWithId:nil] || displayName_ == nil || [displayName_ isEmpty]) {
    return NO;
  }
  return YES;
}

- (id<JavaUtilCollection>)getTrips {
  id<JavaUtilCollection> trips = [[JavaUtilArrayList alloc] init];
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction * __strong p in nil_chk(self->predictions_)) {
    if ([p isKindOfClass:[ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction class]]) {
      for (ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival * __strong srda in nil_chk([((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction *) nil_chk(((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction *) check_class_cast(p, [ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction class])))) getArrivals])) {
        [trips addWithId:[((ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationArrival *) nil_chk(srda)) getTrip]];
      }
    }
  }
  return trips;
}

- (void)startRequest {
  [super startRequest];
  if ([((id<JavaUtilCollection>) nil_chk(predictions_)) size] == 0) {
    ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_makePredictions(self);
  }
  [self resumeRequest];
}

- (void)resumeRequest {
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction * __strong p in nil_chk(predictions_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) startPredicting];
  }
}

- (void)pauseRequest {
  [super pauseRequest];
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction * __strong p in nil_chk(predictions_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) stopPredicting];
  }
}

- (void)cancelRequest {
  [super cancelRequest];
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction * __strong p in nil_chk(predictions_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) cancelTrips];
    [p stopPredicting];
  }
  [predictions_ clear];
}

- (void)restoreTrips {
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction * __strong p in nil_chk(predictions_)) {
    [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) restoreTrips];
  }
}

- (jboolean)updateAvailable {
  return updateAvailable__;
}

- (jboolean)hasTripsToDisplay {
  for (ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction * __strong p in nil_chk(predictions_)) {
    if ([((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) hasAnyPredictions]) {
      return YES;
    }
  }
  return NO;
}

- (void)updateTaken {
  updateAvailable__ = NO;
}

- (void)makePredictions {
  ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_makePredictions(self);
}

- (id<JavaUtilCollection>)getRaw {
  JavaUtilArrayList *strings = [[JavaUtilArrayList alloc] init];
  for (ComRemulasceLametroappJava_coreBasic_typesStop * __strong s in nil_chk(stops_)) {
    [strings addWithId:[((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk(s)) getStopID]];
  }
  return strings;
}

- (void)writeObjectWithJavaIoObjectOutputStream:(JavaIoObjectOutputStream *)oos {
  [((JavaIoObjectOutputStream *) nil_chk(oos)) writeObjectWithId:stops_];
  [oos writeObjectWithId:predictions_];
}

- (void)readObjectWithJavaIoObjectInputStream:(JavaIoObjectInputStream *)ois {
  @try {
    stops_ = (id<JavaUtilCollection>) check_protocol_cast([((JavaIoObjectInputStream *) nil_chk(ois)) readObject], @protocol(JavaUtilCollection));
    predictions_ = (id<JavaUtilCollection>) check_protocol_cast([ois readObject], @protocol(JavaUtilCollection));
  }
  @catch (JavaLangException *e) {
    stops_ = [[JavaUtilArrayList alloc] init];
    predictions_ = [[JavaUtilArrayList alloc] init];
    [((JavaLangException *) nil_chk(e)) printStackTrace];
  }
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithJavaUtilCollection:withNSString:", "StopServiceRequest", NULL, 0x1, NULL },
    { "initWithComRemulasceLametroappJava_coreBasic_typesStop:withNSString:", "StopServiceRequest", NULL, 0x1, NULL },
    { "isValid", NULL, "Z", 0x1, NULL },
    { "getTrips", NULL, "Ljava.util.Collection;", 0x1, NULL },
    { "startRequest", NULL, "V", 0x1, NULL },
    { "resumeRequest", NULL, "V", 0x1, NULL },
    { "pauseRequest", NULL, "V", 0x1, NULL },
    { "cancelRequest", NULL, "V", 0x1, NULL },
    { "restoreTrips", NULL, "V", 0x1, NULL },
    { "updateAvailable", NULL, "Z", 0x1, NULL },
    { "hasTripsToDisplay", NULL, "Z", 0x1, NULL },
    { "updateTaken", NULL, "V", 0x1, NULL },
    { "makePredictions", NULL, "V", 0x2, NULL },
    { "getRaw", NULL, "Ljava.util.Collection;", 0x1, NULL },
    { "writeObjectWithJavaIoObjectOutputStream:", "writeObject", "V", 0x2, "Ljava.io.IOException;" },
    { "readObjectWithJavaIoObjectInputStream:", "readObject", "V", 0x2, "Ljava.lang.ClassNotFoundException;Ljava.io.IOException;" },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "TAG_", NULL, 0x1a, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_TAG_,  },
    { "stops_", NULL, 0x2, "Ljava.util.Collection;", NULL,  },
    { "predictions_", NULL, 0x2, "Ljava.util.Collection;", NULL,  },
    { "updateAvailable__", "updateAvailable", 0x2, "Z", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest = { 2, "StopServiceRequest", "com.remulasce.lametroapp.java_core.basic_types", NULL, 0x1, 16, methods, 4, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest;
}

@end

void ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_makePredictions(ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest *self) {
  if (![self isValid]) {
    ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest_TAG_, @"Make predictions in invalid StopServiceRequest");
    return;
  }
  if ([((id<JavaUtilCollection>) nil_chk(self->predictions_)) isEmpty]) {
    for (ComRemulasceLametroappJava_coreBasic_typesStop * __strong s in nil_chk(self->stops_)) {
      ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction *stopRouteDestinationPrediction = [[ComRemulasceLametroappJava_coreDynamic_dataTypesStopRouteDestinationPrediction alloc] initWithComRemulasceLametroappJava_coreBasic_typesStop:s withComRemulasceLametroappJava_coreBasic_typesRoute:nil];
      [self->predictions_ addWithId:stopRouteDestinationPrediction];
    }
  }
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreBasic_typesStopServiceRequest)
