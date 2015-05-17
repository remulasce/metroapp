//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/ServiceRequestHandler.java
//

#ifndef _ComRemulasceLametroappJava_coreServiceRequestHandler_H_
#define _ComRemulasceLametroappJava_coreServiceRequestHandler_H_

@class ComRemulasceLametroappJava_coreDynamic_dataTypesTrip;
@protocol JavaUtilCollection;
@protocol JavaUtilList;

#include "J2ObjC_header.h"
#include "java/util/Comparator.h"

@interface ComRemulasceLametroappJava_coreServiceRequestHandler : NSObject {
 @public
  id<JavaUtilComparator> tripPriorityComparator_;
}

#pragma mark Public

- (instancetype)init;

- (id<JavaUtilCollection>)getRequests;

- (id<JavaUtilList>)GetSortedTripList;

- (jboolean)isRunning;

- (jint)numRequests;

- (void)SetServiceRequestsWithJavaUtilCollection:(id<JavaUtilCollection>)requests;

- (void)StartPopulating;

- (void)StopPopulating;

#pragma mark Package-Private

- (void)rawSetServiceRequestsWithJavaUtilCollection:(id<JavaUtilCollection>)requests;

- (id<JavaUtilList>)sortTripsWithJavaUtilCollection:(id<JavaUtilCollection>)trips;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreServiceRequestHandler)

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreServiceRequestHandler, tripPriorityComparator_, id<JavaUtilComparator>)

FOUNDATION_EXPORT NSString *ComRemulasceLametroappJava_coreServiceRequestHandler_TAG_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreServiceRequestHandler, TAG_, NSString *)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreServiceRequestHandler)

@interface ComRemulasceLametroappJava_coreServiceRequestHandler_$1 : NSObject < JavaUtilComparator > {
}

#pragma mark Public

- (jint)compareWithId:(ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *)lhs
               withId:(ComRemulasceLametroappJava_coreDynamic_dataTypesTrip *)rhs;

#pragma mark Package-Private

- (instancetype)init;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreServiceRequestHandler_$1)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreServiceRequestHandler_$1)

#endif // _ComRemulasceLametroappJava_coreServiceRequestHandler_H_
