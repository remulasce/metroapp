//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/BasicLocation.java
//

#include "BasicLocation.h"
#include "J2ObjC_source.h"

@implementation ComRemulasceLametroappJava_coreBasic_typesBasicLocation

- (instancetype)initWithDouble:(jdouble)latitude
                    withDouble:(jdouble)longitude {
  if (self = [super init]) {
    self->latitude_ = latitude;
    self->longitude_ = longitude;
  }
  return self;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithDouble:withDouble:", "BasicLocation", NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "latitude_", NULL, 0x11, "D", NULL,  },
    { "longitude_", NULL, 0x11, "D", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreBasic_typesBasicLocation = { 2, "BasicLocation", "com.remulasce.lametroapp.java_core.basic_types", NULL, 0x1, 1, methods, 2, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreBasic_typesBasicLocation;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreBasic_typesBasicLocation)