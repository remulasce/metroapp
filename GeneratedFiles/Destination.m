//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/Destination.java
//

#include "Destination.h"
#include "IOSClass.h"
#include "J2ObjC_source.h"

@interface ComRemulasceLametroappJava_coreBasic_typesDestination () {
 @public
  NSString *raw_;
}
@end

J2OBJC_FIELD_SETTER(ComRemulasceLametroappJava_coreBasic_typesDestination, raw_, NSString *)

@implementation ComRemulasceLametroappJava_coreBasic_typesDestination

- (instancetype)init {
  if (self = [super init]) {
    raw_ = @"";
  }
  return self;
}

- (instancetype)initWithNSString:(NSString *)dest {
  if (self = [super init]) {
    raw_ = @"";
    raw_ = dest;
  }
  return self;
}

- (NSString *)getString {
  return raw_;
}

- (jboolean)isValid {
  return raw_ != nil && ![raw_ isEmpty];
}

- (NSUInteger)hash {
  return ((jint) [((NSString *) nil_chk(raw_)) hash]);
}

- (jboolean)isEqual:(id)o {
  if ([nil_chk(o) getClass] != [self getClass]) {
    return NO;
  }
  return ((jint) [o hash]) == ((jint) [self hash]);
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "init", "Destination", NULL, 0x1, NULL },
    { "initWithNSString:", "Destination", NULL, 0x1, NULL },
    { "getString", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "isValid", NULL, "Z", 0x1, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
    { "isEqual:", "equals", "Z", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "serialVersionUID_", NULL, 0x1a, "J", NULL, .constantValue.asLong = ComRemulasceLametroappJava_coreBasic_typesDestination_serialVersionUID },
    { "raw_", NULL, 0x2, "Ljava.lang.String;", NULL,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreBasic_typesDestination = { 2, "Destination", "com.remulasce.lametroapp.java_core.basic_types", NULL, 0x1, 6, methods, 2, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreBasic_typesDestination;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreBasic_typesDestination)
