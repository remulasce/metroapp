//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/PredictionManager.java
//

#ifndef _ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_H_
#define _ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_H_

@class ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter;
@class ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager;
@class ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction;
@protocol ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter;
@protocol JavaUtilList;

#include "J2ObjC_header.h"
#include "java/lang/Runnable.h"

@interface ComRemulasceLametroappJava_coreDynamic_dataPredictionManager : NSObject {
}

#pragma mark Public

- (instancetype)init;

+ (ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)getInstance;

- (jboolean)isRunning;

- (jint)numPredictions;

- (void)pauseTracking;

- (void)rawSetNetworkWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter:(ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *)network;

- (void)resumeTracking;

+ (void)setPredictionManagerWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)manager;

+ (void)setStatusReporterWithComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:(id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter>)reporter;

- (void)setThrottleWithBoolean:(jboolean)throttlePredictions;

- (void)startTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p;

- (void)stopTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager)

FOUNDATION_EXPORT ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_getInstance();

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_setPredictionManagerWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *manager);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_setStatusReporterWithComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter_(id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter> reporter);

FOUNDATION_EXPORT NSString *ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, TAG_, NSString *)

FOUNDATION_EXPORT jint ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PREDICTION_UPDATE_MAX_INTERVAL_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, PREDICTION_UPDATE_MAX_INTERVAL_, jint)
J2OBJC_STATIC_FIELD_REF_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, PREDICTION_UPDATE_MAX_INTERVAL_, jint)

FOUNDATION_EXPORT jint ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UPDATE_CHECK_INTERVAL_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, UPDATE_CHECK_INTERVAL_, jint)
J2OBJC_STATIC_FIELD_REF_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, UPDATE_CHECK_INTERVAL_, jint)

FOUNDATION_EXPORT ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, manager_, ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)
J2OBJC_STATIC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, manager_, ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)

FOUNDATION_EXPORT id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter> ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_statusReporter_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, statusReporter_, id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter>)
J2OBJC_STATIC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, statusReporter_, id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter>)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager)

@interface ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager : NSObject < JavaLangRunnable > {
 @public
  jboolean run__;
  id updateObject_;
}

#pragma mark Public

- (void)run;

#pragma mark Package-Private

- (instancetype)initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)outer$;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager)

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager, updateObject_, id)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager)

@interface ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher : NSObject < JavaLangRunnable > {
 @public
  ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *prediction_;
}

#pragma mark Public

- (instancetype)initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)outer$
                      withComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p;

- (void)run;

- (NSString *)sendRequestWithNSString:(NSString *)request;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher)

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher, prediction_, ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher)

#endif // _ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_H_
