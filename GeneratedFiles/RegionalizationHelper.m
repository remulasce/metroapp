//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/RegionalizationHelper.java
//

#include "J2ObjC_source.h"
#include "RegionalizationHelper.h"

@interface ComRemulasceLametroappJava_coreRegionalizationHelper ()
- (instancetype)init;
@end

BOOL ComRemulasceLametroappJava_coreRegionalizationHelper_initialized = NO;

@implementation ComRemulasceLametroappJava_coreRegionalizationHelper

ComRemulasceLametroappJava_coreRegionalizationHelper * ComRemulasceLametroappJava_coreRegionalizationHelper_instance_;

- (instancetype)init {
  return [super init];
}

+ (ComRemulasceLametroappJava_coreRegionalizationHelper *)getInstance {
  return ComRemulasceLametroappJava_coreRegionalizationHelper_getInstance();
}

+ (void)initialize {
  if (self == [ComRemulasceLametroappJava_coreRegionalizationHelper class]) {
    ComRemulasceLametroappJava_coreRegionalizationHelper_instance_ = [[ComRemulasceLametroappJava_coreRegionalizationHelper alloc] init];
    J2OBJC_SET_INITIALIZED(ComRemulasceLametroappJava_coreRegionalizationHelper)
  }
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "init", "RegionalizationHelper", NULL, 0x2, NULL },
    { "getInstance", NULL, "Lcom.remulasce.lametroapp.java_core.RegionalizationHelper;", 0x9, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "instance_", NULL, 0x19, "Lcom.remulasce.lametroapp.java_core.RegionalizationHelper;", &ComRemulasceLametroappJava_coreRegionalizationHelper_instance_,  },
    { "agencyName_", NULL, 0x1, "Ljava.lang.String;", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreRegionalizationHelper = { 2, "RegionalizationHelper", "com.remulasce.lametroapp.java_core", NULL, 0x1, 2, methods, 2, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreRegionalizationHelper;
}

@end

ComRemulasceLametroappJava_coreRegionalizationHelper *ComRemulasceLametroappJava_coreRegionalizationHelper_getInstance() {
  ComRemulasceLametroappJava_coreRegionalizationHelper_init();
  return ComRemulasceLametroappJava_coreRegionalizationHelper_instance_;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreRegionalizationHelper)
