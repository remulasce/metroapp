//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/dynamic_data/HTTPGetter.java
//

#include "HTTPGetter.h"
#include "J2ObjC_source.h"
#include "NetworkStatusReporter.h"

@interface ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter () {
}
@end

@implementation ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter

ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter * ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getter_;

+ (NSString *)getHTTPResponseWithNSString:(NSString *)message
withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:(id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter>)reporter {
  return ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getHTTPResponseWithNSString_withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter_(message, reporter);
}

- (NSString *)doGetHTTPResponseWithNSString:(NSString *)message
withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:(id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter>)reporter {
  return @"Not Implemented";
}

+ (void)setHTTPGetterWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter:(ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *)set {
  ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_setHTTPGetterWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_(set);
}

+ (ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *)getHTTPGetter {
  return ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getHTTPGetter();
}

- (instancetype)init {
  return [super init];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "getHTTPResponseWithNSString:withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:", "getHTTPResponse", "Ljava.lang.String;", 0x9, NULL },
    { "doGetHTTPResponseWithNSString:withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:", "doGetHTTPResponse", "Ljava.lang.String;", 0x1, NULL },
    { "setHTTPGetterWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter:", "setHTTPGetter", "V", 0x9, NULL },
    { "getHTTPGetter", NULL, "Lcom.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;", 0x9, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "getter_", NULL, 0xa, "Lcom.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;", &ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getter_,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter = { 2, "HTTPGetter", "com.remulasce.lametroapp.java_core.dynamic_data", NULL, 0x1, 5, methods, 1, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter;
}

@end

NSString *ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getHTTPResponseWithNSString_withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter_(NSString *message, id<ComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter> reporter) {
  ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_init();
  return [((ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *) nil_chk(ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getHTTPGetter())) doGetHTTPResponseWithNSString:message withComRemulasceLametroappJava_coreNetwork_statusNetworkStatusReporter:reporter];
}

void ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_setHTTPGetterWithComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_(ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *set) {
  ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_init();
  ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getter_ = set;
}

ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter *ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getHTTPGetter() {
  ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_init();
  if (ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getter_ == nil) {
    ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getter_ = [[ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter alloc] init];
  }
  return ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter_getter_;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreDynamic_dataHTTPGetter)
