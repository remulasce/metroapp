//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/location/GlobalLocationProvider.java
//

#ifndef _ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_H_
#define _ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_H_

@protocol ComRemulasceLametroappJava_coreLocationLocationRetriever;

#include "J2ObjC_header.h"

@interface ComRemulasceLametroappJava_coreLocationGlobalLocationProvider : NSObject {
}

#pragma mark Public

- (instancetype)init;

+ (id<ComRemulasceLametroappJava_coreLocationLocationRetriever>)getRetriever;

+ (void)setRetrieverWithComRemulasceLametroappJava_coreLocationLocationRetriever:(id<ComRemulasceLametroappJava_coreLocationLocationRetriever>)r;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreLocationGlobalLocationProvider)

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_setRetrieverWithComRemulasceLametroappJava_coreLocationLocationRetriever_(id<ComRemulasceLametroappJava_coreLocationLocationRetriever> r);

FOUNDATION_EXPORT id<ComRemulasceLametroappJava_coreLocationLocationRetriever> ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_getRetriever();

FOUNDATION_EXPORT id<ComRemulasceLametroappJava_coreLocationLocationRetriever> ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_retriever_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreLocationGlobalLocationProvider, retriever_, id<ComRemulasceLametroappJava_coreLocationLocationRetriever>)
J2OBJC_STATIC_FIELD_SETTER(ComRemulasceLametroappJava_coreLocationGlobalLocationProvider, retriever_, id<ComRemulasceLametroappJava_coreLocationLocationRetriever>)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreLocationGlobalLocationProvider)

#endif // _ComRemulasceLametroappJava_coreLocationGlobalLocationProvider_H_
