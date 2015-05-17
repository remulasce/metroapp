//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/types/Arrival.java
//

#ifndef _ComRemulasceLametroappJava_coreDynamic_dataTypesArrival_H_
#define _ComRemulasceLametroappJava_coreDynamic_dataTypesArrival_H_

@class ComRemulasceLametroappJava_coreBasic_typesDestination;
@class ComRemulasceLametroappJava_coreBasic_typesRoute;
@class ComRemulasceLametroappJava_coreBasic_typesStop;
@class ComRemulasceLametroappJava_coreBasic_typesVehicle;

#include "J2ObjC_header.h"
#include "java/io/Serializable.h"

@interface ComRemulasceLametroappJava_coreDynamic_dataTypesArrival : NSObject < JavaIoSerializable > {
 @public
  ComRemulasceLametroappJava_coreBasic_typesRoute *route_;
  ComRemulasceLametroappJava_coreBasic_typesDestination *destination_;
  ComRemulasceLametroappJava_coreBasic_typesStop *stop_;
  ComRemulasceLametroappJava_coreBasic_typesVehicle *vehicle_;
}

#pragma mark Public

- (instancetype)init;

- (jboolean)isEqual:(id)o;

- (ComRemulasceLametroappJava_coreBasic_typesDestination *)getDirection;

- (jfloat)getEstimatedArrivalSeconds;

- (ComRemulasceLametroappJava_coreBasic_typesRoute *)getRoute;

- (ComRemulasceLametroappJava_coreBasic_typesStop *)getStop;

- (jlong)getTimeSinceLastEstimation;

- (ComRemulasceLametroappJava_coreBasic_typesVehicle *)getVehicleNum;

- (NSUInteger)hash;

- (jboolean)isInScope;

- (void)setDestinationWithComRemulasceLametroappJava_coreBasic_typesDestination:(ComRemulasceLametroappJava_coreBasic_typesDestination *)d;

- (void)setEstimatedArrivalSecondsWithFloat:(jfloat)secondsTillArrival;

- (void)setRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)route;

- (void)setScopeWithBoolean:(jboolean)inScope;

- (void)setStopWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)stop;

- (void)setVehicleWithComRemulasceLametroappJava_coreBasic_typesVehicle:(ComRemulasceLametroappJava_coreBasic_typesVehicle *)veh;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival)

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival, route_, ComRemulasceLametroappJava_coreBasic_typesRoute *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival, destination_, ComRemulasceLametroappJava_coreBasic_typesDestination *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival, stop_, ComRemulasceLametroappJava_coreBasic_typesStop *)
J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival, vehicle_, ComRemulasceLametroappJava_coreBasic_typesVehicle *)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreDynamic_dataTypesArrival)

#endif // _ComRemulasceLametroappJava_coreDynamic_dataTypesArrival_H_
