//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/ServiceRequestHandler.java
//

#include "J2ObjC_source.h"
#include "Log.h"
#include "ServiceRequest.h"
#include "ServiceRequestHandler.h"
#include "Trip.h"
#include "java/util/ArrayList.h"
#include "java/util/Collection.h"
#include "java/util/Collections.h"
#include "java/util/Comparator.h"
#include "java/util/List.h"
#include "java/util/concurrent/CopyOnWriteArrayList.h"

@interface ComRemulasceLametroappJava_coreServiceRequestHandler () {
 @public
  jboolean running_;
  id<JavaUtilList> serviceRequests_;
}
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreServiceRequestHandler, serviceRequests_, id<JavaUtilList>)

@implementation ComRemulasceLametroappJava_coreServiceRequestHandler

NSString * ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_ = @"ServiceRequestHandler";

- (id<JavaUtilList>)sortTripsWithJavaUtilCollection:(id<JavaUtilCollection>)trips {
  id<JavaUtilList> sortedTrips = [[JavaUtilArrayList alloc] initWithJavaUtilCollection:trips];
  JavaUtilCollections_sortWithJavaUtilList_withJavaUtilComparator_(sortedTrips, tripPriorityComparator_);
  return sortedTrips;
}

- (id<JavaUtilList>)GetSortedTripList {
  id<JavaUtilList> ret = [[JavaUtilArrayList alloc] init];
  for (ComRemulasceLametroappJava_coreBasic_typesServiceRequest * __strong request in nil_chk(serviceRequests_)) {
    [ret addAllWithJavaUtilCollection:[((ComRemulasceLametroappJava_coreBasic_typesServiceRequest *) nil_chk(request)) getTrips]];
  }
  return [self sortTripsWithJavaUtilCollection:ret];
}

- (jboolean)isRunning {
  return running_;
}

- (jint)numRequests {
  return [((id<JavaUtilList>) nil_chk(serviceRequests_)) size];
}

- (void)StartPopulating {
  if (running_) {
    ComRemulasceLametroappJava_coreAnalyticsLog_eWithNSString_withNSString_(ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_, @"Started an already-populating populator");
    return;
  }
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_, @"Starting TripPopulator");
  for (ComRemulasceLametroappJava_coreBasic_typesServiceRequest * __strong r in nil_chk(serviceRequests_)) {
    [((ComRemulasceLametroappJava_coreBasic_typesServiceRequest *) nil_chk(r)) startRequest];
  }
  running_ = YES;
}

- (void)StopPopulating {
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_, @"Stopping TripPopulator");
  if (!running_) {
    ComRemulasceLametroappJava_coreAnalyticsLog_eWithNSString_withNSString_(ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_, @"Stopping an already-stopped populator");
    return;
  }
  for (ComRemulasceLametroappJava_coreBasic_typesServiceRequest * __strong r in nil_chk(serviceRequests_)) {
    [((ComRemulasceLametroappJava_coreBasic_typesServiceRequest *) nil_chk(r)) pauseRequest];
  }
  running_ = NO;
}

- (void)rawSetServiceRequestsWithJavaUtilCollection:(id<JavaUtilCollection>)requests {
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_, @"Setting service requests");
  for (ComRemulasceLametroappJava_coreBasic_typesServiceRequest * __strong r in nil_chk(requests)) {
    if (![((id<JavaUtilList>) nil_chk(serviceRequests_)) containsWithId:r]) {
      [((ComRemulasceLametroappJava_coreBasic_typesServiceRequest *) nil_chk(r)) startRequest];
    }
  }
  [((id<JavaUtilList>) nil_chk(serviceRequests_)) clear];
  [serviceRequests_ addAllWithJavaUtilCollection:requests];
}

- (id<JavaUtilCollection>)getRequests {
  return serviceRequests_;
}

- (void)SetServiceRequestsWithJavaUtilCollection:(id<JavaUtilCollection>)requests {
  [self rawSetServiceRequestsWithJavaUtilCollection:requests];
}

- (instancetype)init {
  if (self = [super init]) {
    running_ = NO;
    serviceRequests_ = [[JavaUtilConcurrentCopyOnWriteArrayList alloc] init];
    tripPriorityComparator_ = [[ComRemulasceLametroappJava_coreServiceRequestHandler_$1 alloc] init];
  }
  return self;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "sortTripsWithJavaUtilCollection:", "sortTrips", "Ljava.util.List;", 0x0, NULL },
    { "GetSortedTripList", NULL, "Ljava.util.List;", 0x1, NULL },
    { "isRunning", NULL, "Z", 0x1, NULL },
    { "numRequests", NULL, "I", 0x1, NULL },
    { "StartPopulating", NULL, "V", 0x1, NULL },
    { "StopPopulating", NULL, "V", 0x1, NULL },
    { "rawSetServiceRequestsWithJavaUtilCollection:", "rawSetServiceRequests", "V", 0x0, NULL },
    { "getRequests", NULL, "Ljava.util.Collection;", 0x1, NULL },
    { "SetServiceRequestsWithJavaUtilCollection:", "SetServiceRequests", "V", 0x1, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "TAG_", NULL, 0x1a, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_,  },
    { "running_", NULL, 0x2, "Z", NULL,  },
    { "serviceRequests_", NULL, 0x12, "Ljava.util.List;", NULL,  },
    { "tripPriorityComparator_", NULL, 0x10, "Ljava.util.Comparator;", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreServiceRequestHandler = { 2, "ServiceRequestHandler", "com.remulasce.lametroapp.java_core", NULL, 0x1, 10, methods, 4, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreServiceRequestHandler;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreServiceRequestHandler)

@implementation ComRemulasceLametroappJava_coreServiceRequestHandler_$1

- (jint)compareWithId:(ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *)lhs
               withId:(ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *)rhs {
  return ([((ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *) nil_chk(lhs)) getPriority] < [((ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *) nil_chk(rhs)) getPriority]) ? 1 : -1;
}

- (instancetype)init {
  return [super init];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "compareWithId:withId:", "compare", "I", 0x1, NULL },
    { "init", NULL, NULL, 0x0, NULL },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreServiceRequestHandler_$1 = { 2, "", "com.remulasce.lametroapp.java_core", "ServiceRequestHandler", 0x8008, 2, methods, 0, NULL, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreServiceRequestHandler_$1;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreServiceRequestHandler_$1)
