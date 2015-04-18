//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/basic_types/ServiceRequest.java
//

#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "ServiceRequest.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/util/Collection.h"

@interface ComRemulasceLametroappJava_coreBasic_typesServiceRequest () {
 @public
  jboolean inScope_;
}
@end

@implementation ComRemulasceLametroappJava_coreBasic_typesServiceRequest

- (instancetype)init {
  if (self = [super init]) {
    displayName_ = @"ServiceRequest";
    inScope_ = YES;
    lifecycleState_ = ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_get_STOPPED();
  }
  return self;
}

- (instancetype)initWithNSString:(NSString *)s {
  if (self = [super init]) {
    displayName_ = @"ServiceRequest";
    inScope_ = YES;
    lifecycleState_ = ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_get_STOPPED();
    self->displayName_ = s;
  }
  return self;
}

- (void)startRequest {
  lifecycleState_ = ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_get_RUNNING();
}

- (void)pauseRequest {
  lifecycleState_ = ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_get_PAUSED();
}

- (void)cancelRequest {
  lifecycleState_ = ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_get_STOPPED();
}

- (ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *)getLifecycleState {
  return lifecycleState_;
}

- (void)descope {
  inScope_ = NO;
}

- (jboolean)isInScope {
  return inScope_;
}

- (id<JavaUtilCollection>)getTrips {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)restoreTrips {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (jboolean)isValid {
  if (displayName_ == nil || [displayName_ isEmpty]) {
    return NO;
  }
  return YES;
}

- (void)setDisplayNameWithNSString:(NSString *)displayName {
  self->displayName_ = displayName;
}

- (NSString *)getDisplayName {
  return displayName_;
}

- (jboolean)updateAvailable {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)updateTaken {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (jboolean)hasTripsToDisplay {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (id<JavaUtilCollection>)getRaw {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (NSString *)description {
  return displayName_;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "init", "ServiceRequest", NULL, 0x1, NULL },
    { "initWithNSString:", "ServiceRequest", NULL, 0x1, NULL },
    { "startRequest", NULL, "V", 0x1, NULL },
    { "pauseRequest", NULL, "V", 0x1, NULL },
    { "cancelRequest", NULL, "V", 0x1, NULL },
    { "getLifecycleState", NULL, "Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;", 0x1, NULL },
    { "descope", NULL, "V", 0x1, NULL },
    { "isInScope", NULL, "Z", 0x1, NULL },
    { "getTrips", NULL, "Ljava.util.Collection;", 0x401, NULL },
    { "restoreTrips", NULL, "V", 0x401, NULL },
    { "isValid", NULL, "Z", 0x1, NULL },
    { "setDisplayNameWithNSString:", "setDisplayName", "V", 0x1, NULL },
    { "getDisplayName", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "updateAvailable", NULL, "Z", 0x401, NULL },
    { "updateTaken", NULL, "V", 0x401, NULL },
    { "hasTripsToDisplay", NULL, "Z", 0x401, NULL },
    { "getRaw", NULL, "Ljava.util.Collection;", 0x401, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "displayName_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
    { "inScope_", NULL, 0x2, "Z", NULL,  },
    { "lifecycleState_", NULL, 0x4, "Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;", NULL,  },
  };
  static const char *inner_classes[] = {"Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;"};
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreBasic_typesServiceRequest = { 2, "ServiceRequest", "com.remulasce.lametroapp.java_core.basic_types", NULL, 0x401, 18, methods, 3, fields, 0, NULL, 1, inner_classes};
  return &_ComRemulasceLametroappJava_coreBasic_typesServiceRequest;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreBasic_typesServiceRequest)

BOOL ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_initialized = NO;

ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_[3];

@implementation ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum

- (instancetype)initWithNSString:(NSString *)__name
                         withInt:(jint)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

IOSObjectArray *ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values() {
  ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_init();
  return [IOSObjectArray arrayWithObjects:ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_ count:3 type:ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_class_()];
}
+ (IOSObjectArray *)values {
  return ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values();
}

+ (ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *)valueOfWithNSString:(NSString *)name {
  return ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_valueOfWithNSString_(name);
}

ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_valueOfWithNSString_(NSString *name) {
  ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_init();
  for (int i = 0; i < 3; i++) {
    ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum *e = ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_values_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

+ (void)initialize {
  if (self == [ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum class]) {
    ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_STOPPED = [[ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum alloc] initWithNSString:@"STOPPED" withInt:0];
    ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_PAUSED = [[ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum alloc] initWithNSString:@"PAUSED" withInt:1];
    ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_RUNNING = [[ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum alloc] initWithNSString:@"RUNNING" withInt:2];
    J2OBJC_SET_INITIALIZED(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum)
  }
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithNSString:withInt:", "init", NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "STOPPED", "STOPPED", 0x4019, "Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;", &ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_STOPPED,  },
    { "PAUSED", "PAUSED", 0x4019, "Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;", &ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_PAUSED,  },
    { "RUNNING", "RUNNING", 0x4019, "Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;", &ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum_RUNNING,  },
  };
  static const char *superclass_type_args[] = {"Lcom.remulasce.lametroapp.java_core.basic_types.ServiceRequest$RequestLifecycleState;"};
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum = { 2, "RequestLifecycleState", "com.remulasce.lametroapp.java_core.basic_types", "ServiceRequest", 0x4019, 1, methods, 3, fields, 1, superclass_type_args, 0, NULL};
  return &_ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreBasic_typesServiceRequest_RequestLifecycleStateEnum)