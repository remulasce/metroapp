//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/PredictionManager.java
//

#include "HTTPGetter.h"
#include "J2ObjC_source.h"
#include "Log.h"
#include "NetworkStatusReporter.h"
#include "Prediction.h"
#include "PredictionManager.h"
#include "Tracking.h"
#include "java/lang/IndexOutOfBoundsException.h"
#include "java/lang/InterruptedException.h"
#include "java/lang/Math.h"
#include "java/lang/Thread.h"
#include "java/util/List.h"
#include "java/util/concurrent/CopyOnWriteArrayList.h"

__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_forceUpdateNow(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_resumeUpdating(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_startUpdatingIfNotStarted(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_pauseUpdating(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_waitForNextRun(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *self);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_updateOldPredictions(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *self);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *self, ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *p);

@interface ComRemulasceLametroappJava_coreDynamic_dataPredictionManager () {
 @public
  ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *network_;
  id<JavaUtilList> trackingList_;
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *updater_;
}

- (void)forceUpdateNow;

- (void)resumeUpdating;

- (void)startUpdatingIfNotStarted;

- (void)pauseUpdating;
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, network_, ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, trackingList_, id<JavaUtilList>)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager, updater_, ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *)

@interface ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager () {
 @public
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *this$0_;
}

- (void)waitForNextRun;

- (void)updateOldPredictions;

- (void)GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p;
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager, this$0_, ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)

@interface ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher () {
 @public
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *this$0_;
}
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher, this$0_, ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)

@implementation ComRemulasceLametroappJava_coreDynamic_dataPredictionManager

NSString * ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_ = @"PredictionManager";
jint ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PREDICTION_UPDATE_MAX_INTERVAL_ = 5000;
jint ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UPDATE_CHECK_INTERVAL_ = 500;
ComRemulasceLametroappJava_coreDynamic_dataPredictionManager * ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_;
id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter> ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_statusReporter_;

+ (ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)getInstance {
  return ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_getInstance();
}

+ (void)setPredictionManagerWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)manager {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_setPredictionManagerWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager_(manager);
}

+ (void)setStatusReporterWithComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:(id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter>)reporter {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_setStatusReporterWithComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter_(reporter);
}

- (instancetype)init {
  if (self = [super init]) {
    trackingList_ = [[JavaUtilConcurrentCopyOnWriteArrayList alloc] init];
    self->network_ = ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getHTTPGetter();
  }
  return self;
}

- (void)rawSetNetworkWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter:(ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *)network {
  self->network_ = network;
}

- (void)setThrottleWithBoolean:(jboolean)throttlePredictions {
  if (throttlePredictions) {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PREDICTION_UPDATE_MAX_INTERVAL_ = 5000;
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UPDATE_CHECK_INTERVAL_ = 1;
  }
  else {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PREDICTION_UPDATE_MAX_INTERVAL_ = 0;
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UPDATE_CHECK_INTERVAL_ = 500;
  }
}

- (void)startTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p {
  if (![((id<JavaUtilList>) nil_chk(trackingList_)) containsWithId:p]) {
    [trackingList_ addWithId:p];
  }
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_startUpdatingIfNotStarted(self);
}

- (void)stopTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p {
  [((id<JavaUtilList>) nil_chk(trackingList_)) removeWithId:p];
}

- (void)pauseTracking {
  @synchronized(self) {
    ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_, @"Pausing all prediction tracking");
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_pauseUpdating(self);
  }
}

- (void)resumeTracking {
  @synchronized(self) {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_resumeUpdating(self);
  }
  @synchronized(((ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *) nil_chk(updater_))->updateObject_) {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_forceUpdateNow(self);
  }
}

- (void)forceUpdateNow {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_forceUpdateNow(self);
}

- (void)resumeUpdating {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_resumeUpdating(self);
}

- (void)startUpdatingIfNotStarted {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_startUpdatingIfNotStarted(self);
}

- (void)pauseUpdating {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_pauseUpdating(self);
}

- (jboolean)isRunning {
  if (updater_ == nil) {
    return NO;
  }
  return ((ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *) nil_chk(updater_))->run__;
}

- (jint)numPredictions {
  return [((id<JavaUtilList>) nil_chk(trackingList_)) size];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "getInstance", NULL, "Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;", 0x9, NULL },
    { "setPredictionManagerWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:", "setPredictionManager", "V", 0x9, NULL },
    { "setStatusReporterWithComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:", "setStatusReporter", "V", 0x9, NULL },
    { "init", "PredictionManager", NULL, 0x1, NULL },
    { "rawSetNetworkWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter:", "rawSetNetwork", "V", 0x1, NULL },
    { "setThrottleWithBoolean:", "setThrottle", "V", 0x1, NULL },
    { "startTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:", "startTracking", "V", 0x1, NULL },
    { "stopTrackingWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:", "stopTracking", "V", 0x1, NULL },
    { "pauseTracking", NULL, "V", 0x1, NULL },
    { "resumeTracking", NULL, "V", 0x1, NULL },
    { "forceUpdateNow", NULL, "V", 0x2, NULL },
    { "resumeUpdating", NULL, "V", 0x2, NULL },
    { "startUpdatingIfNotStarted", NULL, "V", 0x2, NULL },
    { "pauseUpdating", NULL, "V", 0x2, NULL },
    { "isRunning", NULL, "Z", 0x1, NULL },
    { "numPredictions", NULL, "I", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "TAG_", NULL, 0x1a, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_,  },
    { "PREDICTION_UPDATE_MAX_INTERVAL_", NULL, 0xa, "I", &ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PREDICTION_UPDATE_MAX_INTERVAL_,  },
    { "UPDATE_CHECK_INTERVAL_", NULL, 0x9, "I", &ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UPDATE_CHECK_INTERVAL_,  },
    { "manager_", NULL, 0xa, "Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;", &ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_,  },
    { "statusReporter_", NULL, 0xa, "Lcom.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;", &ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_statusReporter_,  },
    { "network_", NULL, 0x2, "Lcom.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;", NULL,  },
    { "trackingList_", NULL, 0x12, "Ljava.util.List;", NULL,  },
    { "updater_", NULL, 0x2, "Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager$UpdateStager;", NULL,  },
  };
  static const char *inner_classes[] = {"Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager$UpdateStager;", "Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager$PredictionFetcher;"};
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataPredictionManager = { 2, "PredictionManager", "com.remulasce.lametroapp.java_core.dynamic_data", NULL, 0x1, 16, methods, 8, fields, 0, NULL, 2, inner_classes};
  return &_ComRemulasceLametroappJava_coreDynamic_dataPredictionManager;
}

@end

ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_getInstance() {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_init();
  if (ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_ == nil) {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_ = [[ComRemulasceLametroappJava_coreDynamic_dataPredictionManager alloc] init];
  }
  return ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_;
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_setPredictionManagerWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *manager) {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_init();
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_manager_ = manager;
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_setStatusReporterWithComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter_(id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter> reporter) {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_init();
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_statusReporter_ = reporter;
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_forceUpdateNow(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self) {
  [nil_chk(((ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *) nil_chk(self->updater_))->updateObject_) notify];
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_resumeUpdating(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self) {
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_, @"Resuming all prediction tracking");
  if (self->updater_ == nil) {
    self->updater_ = [[ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager alloc] initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:self];
    [((JavaLangThread *) [[JavaLangThread alloc] initWithJavaLangRunnable:self->updater_ withNSString:@"Prediction Update Checker"]) start];
  }
  else {
    ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_, @"Resuming an existing prediction updater");
  }
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_startUpdatingIfNotStarted(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self) {
  @synchronized(self) {
    if (self->updater_ == nil) {
      self->updater_ = [[ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager alloc] initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:self];
      [((JavaLangThread *) [[JavaLangThread alloc] initWithJavaLangRunnable:self->updater_ withNSString:@"Prediction Update Checker"]) start];
    }
  }
  @synchronized(((ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *) nil_chk(self->updater_))->updateObject_) {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_forceUpdateNow(self);
  }
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_pauseUpdating(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *self) {
  if (self->updater_ != nil) {
    self->updater_->run__ = NO;
    self->updater_ = nil;
  }
  else {
    ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_TAG_, @"Pausing a missing prediction updater");
  }
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager)

@implementation ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager

- (void)run {
  while (run__) {
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_updateOldPredictions(self);
    ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_waitForNextRun(self);
  }
}

- (void)waitForNextRun {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_waitForNextRun(self);
}

- (void)updateOldPredictions {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_updateOldPredictions(self);
}

- (void)GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_(self, p);
}

- (instancetype)initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)outer$ {
  this$0_ = outer$;
  if (self = [super init]) {
    run__ = YES;
    updateObject_ = [[NSObject alloc] init];
  }
  return self;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "run", NULL, "V", 0x1, NULL },
    { "waitForNextRun", NULL, "V", 0x2, NULL },
    { "updateOldPredictions", NULL, "V", 0x2, NULL },
    { "GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:", "GetUpdate", "V", 0x2, NULL },
    { "initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:", "init", NULL, 0x0, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;", NULL,  },
    { "run__", "run", 0x1, "Z", NULL,  },
    { "updateObject_", NULL, 0x11, "Ljava.lang.Object;", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager = { 2, "UpdateStager", "com.remulasce.lametroapp.java_core.dynamic_data", "PredictionManager", 0x0, 5, methods, 3, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager;
}

@end

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_waitForNextRun(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *self) {
  @try {
    @synchronized(self->updateObject_) {
      [nil_chk(self->updateObject_) waitWithLong:ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_UPDATE_CHECK_INTERVAL_()];
    }
  }
  @catch (JavaLangInterruptedException *e) {
    [((JavaLangInterruptedException *) nil_chk(e)) printStackTrace];
  }
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_updateOldPredictions(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *self) {
  for (jint i = [((id<JavaUtilList>) nil_chk(self->this$0_->trackingList_)) size] - 1; i >= 0; i--) {
    @try {
      ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *p = [self->this$0_->trackingList_ getWithInt:i];
      jint requestedInterval = [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) getRequestedUpdateInterval];
      jlong timeSinceUpdate = [p getTimeSinceLastUpdate];
      if (timeSinceUpdate >= JavaLangMath_maxWithInt_withInt_(requestedInterval, ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_PREDICTION_UPDATE_MAX_INTERVAL_())) {
        ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_TAG_(), JreStrcat("$I", @"Getting update after ", requestedInterval));
        [p setGettingUpdate];
        ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_(self, p);
      }
    }
    @catch (JavaLangIndexOutOfBoundsException *e) {
      ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_TAG_(), @"Prediction removed out from under PredictionManager");
    }
  }
}

void ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager_GetUpdateWithComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager *self, ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *p) {
  ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher *r = [[ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher alloc] initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:self->this$0_ withComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:p];
  [((JavaLangThread *) [[JavaLangThread alloc] initWithJavaLangRunnable:r withNSString:JreStrcat("$$", @"Prediction update ", [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(p)) getRequestString])]) start];
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_UpdateStager)

@implementation ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher

- (instancetype)initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager *)outer$
                      withComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *)p {
  this$0_ = outer$;
  if (self = [super init]) {
    self->prediction_ = p;
  }
  return self;
}

- (void)run {
  jlong t = ComRemulasceLametroappJava_coreAnalyticsTracking_startTime();
  NSString *request = [((ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction *) nil_chk(prediction_)) getRequestString];
  ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_TAG_(), JreStrcat("$$", @"Handling request ", request));
  if (ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_statusReporter_() != nil) {
  }
  NSString *response = [self sendRequestWithNSString:request];
  ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_TAG_(), JreStrcat("$$", @"Response received: ", response));
  [prediction_ handleResponseWithNSString:response];
  [prediction_ setUpdated];
  ComRemulasceLametroappJava_coreAnalyticsTracking_sendTimeWithNSString_withNSString_withNSString_withLong_(@"PredictionManager", @"UpdateRunner", @"Total Run", t);
}

- (NSString *)sendRequestWithNSString:(NSString *)request {
  ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_TAG_(), JreStrcat("$$", @"Trying request: ", request));
  return [((ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *) nil_chk(this$0_->network_)) doGetHTTPResponseWithNSString:request withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_get_statusReporter_()];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithComRemulasceLametroappJava_coreDynamic_dataPredictionManager:withComRemulasceLametroappJava_coreDynamic_dataTypesPrediction:", "PredictionFetcher", NULL, 0x1, NULL },
    { "run", NULL, "V", 0x1, NULL },
    { "sendRequestWithNSString:", "sendRequest", "Ljava.lang.String;", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "Lcom.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;", NULL,  },
    { "prediction_", NULL, 0x10, "Lcom.remulasce.lametroapp.java_core.dynamic_data.types.Prediction;", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher = { 2, "PredictionFetcher", "com.remulasce.lametroapp.java_core.dynamic_data", "PredictionManager", 0x0, 3, methods, 2, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataPredictionManager_PredictionFetcher)
