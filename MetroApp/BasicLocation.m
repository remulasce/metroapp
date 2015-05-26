//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/BasicLocation.java
//

#include "BasicLocation.h"
#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "java/lang/Double.h"

@implementation ComRemulasceLametroappJava_coreBasic_typesBasicLocation

- (instancetype)initWithDouble:(jdouble)latitude
                    withDouble:(jdouble)longitude {
  if (self = [super init]) {
    self->latitude_ = latitude;
    self->longitude_ = longitude;
  }
  return self;
}

- (jboolean)isEqual:(id)o {
  if (self == o) return YES;
  if (o == nil || [self getClass] != [o getClass]) return NO;
  ComRemulasceLametroappJava_coreBasic_typesBasicLocation *that = (ComRemulasceLametroappJava_coreBasic_typesBasicLocation *) check_class_cast(o, [ComRemulasceLametroappJava_coreBasic_typesBasicLocation class]);
  if (JavaLangDouble_compareWithDouble_withDouble_(((ComRemulasceLametroappJava_coreBasic_typesBasicLocation *) nil_chk(that))->latitude_, latitude_) != 0) return NO;
  return JavaLangDouble_compareWithDouble_withDouble_(that->longitude_, longitude_) == 0;
}

- (NSUInteger)hash {
  jint result;
  jlong temp;
  temp = JavaLangDouble_doubleToLongBitsWithDouble_(latitude_);
  result = (jint) (temp ^ (URShift64(temp, 32)));
  temp = JavaLangDouble_doubleToLongBitsWithDouble_(longitude_);
  result = 31 * result + (jint) (temp ^ (URShift64(temp, 32)));
  return result;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithDouble:withDouble:", "BasicLocation", NULL, 0x1, NULL },
    { "isEqual:", "equals", "Z", 0x1, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "latitude_", NULL, 0x11, "D", NULL,  },
    { "longitude_", NULL, 0x11, "D", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreBasic_typesBasicLocation = { 2, "BasicLocation", "com.remulasce.lametroapp.java_core.basic_types", NULL, 0x1, 3, methods, 2, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreBasic_typesBasicLocation;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreBasic_typesBasicLocation)