//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/Route.java
//

#ifndef _ComRemulasceLametroappJava_coreBasic_typesRoute_H_
#define _ComRemulasceLametroappJava_coreBasic_typesRoute_H_

@class ComRemulasceLametroappJava_coreStatic_dataTypesRouteColor;

#include "J2ObjC_header.h"
#include "java/io/Serializable.h"

#define ComRemulasceLametroappJava_coreBasic_typesRoute_serialVersionUID -1330979643298664422LL

@interface ComRemulasceLametroappJava_coreBasic_typesRoute : NSObject < JavaIoSerializable > {
}

#pragma mark Public

- (instancetype)init;

- (instancetype)initWithNSString:(NSString *)route;

- (instancetype)initWithNSString:(NSString *)route
withComRemulasceLametroappJava_coreStatic_dataTypesRouteColor:(ComRemulasceLametroappJava_coreStatic_dataTypesRouteColor *)color;

- (jboolean)isEqual:(id)o;

- (ComRemulasceLametroappJava_coreStatic_dataTypesRouteColor *)getColor;

- (NSString *)getString;

- (NSUInteger)hash;

- (jboolean)isValid;

- (void)setColorWithComRemulasceLametroappJava_coreStatic_dataTypesRouteColor:(ComRemulasceLametroappJava_coreStatic_dataTypesRouteColor *)color;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreBasic_typesRoute)

J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreBasic_typesRoute, serialVersionUID, jlong)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreBasic_typesRoute)

#endif // _ComRemulasceLametroappJava_coreBasic_typesRoute_H_
