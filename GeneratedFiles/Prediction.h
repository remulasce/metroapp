//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/types/Prediction.java
//

#ifndef _ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_H_
#define _ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_H_

#include "J2ObjC_header.h"
#include "java/io/Serializable.h"

@interface ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction : NSObject < JavaIoSerializable > {
}

#pragma mark Public

- (instancetype)init;

- (void)cancelTrips;

- (jint)getRequestedUpdateInterval;

- (NSString *)getRequestString;

- (jlong)getTimeSinceLastUpdate;

- (void)handleResponseWithNSString:(NSString *)response;

- (jboolean)hasAnyPredictions;

- (jboolean)isInScope;

- (void)restoreTrips;

- (void)setGettingUpdate;

- (void)setUpdated;

- (void)startPredicting;

- (void)stopPredicting;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction)

#endif // _ComRemulasceLametroappJava_coreDynamic_dataTypesPrediction_H_