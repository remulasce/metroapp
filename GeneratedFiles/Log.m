//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/app/src/main/java/com/remulasce/lametroapp/java_core/analytics/Log.java
//

#include "J2ObjC_source.h"
#include "Log.h"

@interface ComRemulasceLametroappJava_coreAnalyticsLog () {
}
@end

@implementation ComRemulasceLametroappJava_coreAnalyticsLog

ComRemulasceLametroappJava_coreAnalyticsLog * ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_;

+ (void)dWithNSString:(NSString *)tag
         withNSString:(NSString *)msg {
  ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(tag, msg);
}

+ (void)eWithNSString:(NSString *)tag
         withNSString:(NSString *)msg {
  ComRemulasceLametroappJava_coreAnalyticsLog_eWithNSString_withNSString_(tag, msg);
}

+ (void)iWithNSString:(NSString *)tag
         withNSString:(NSString *)msg {
  ComRemulasceLametroappJava_coreAnalyticsLog_iWithNSString_withNSString_(tag, msg);
}

+ (void)vWithNSString:(NSString *)tag
         withNSString:(NSString *)msg {
  ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(tag, msg);
}

+ (void)wWithNSString:(NSString *)tag
         withNSString:(NSString *)msg {
  ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(tag, msg);
}

+ (void)SetLoggerWithComRemulasceLametroappJava_coreAnalyticsLog:(ComRemulasceLametroappJava_coreAnalyticsLog *)realLog {
  ComRemulasceLametroappJava_coreAnalyticsLog_SetLoggerWithComRemulasceLametroappJava_coreAnalyticsLog_(realLog);
}

- (void)print_dWithNSString:(NSString *)tag
               withNSString:(NSString *)msg {
}

- (void)print_eWithNSString:(NSString *)tag
               withNSString:(NSString *)msg {
}

- (void)print_vWithNSString:(NSString *)tag
               withNSString:(NSString *)msg {
}

- (void)print_iWithNSString:(NSString *)tag
               withNSString:(NSString *)msg {
}

- (void)print_wWithNSString:(NSString *)tag
               withNSString:(NSString *)msg {
}

- (instancetype)init {
  return [super init];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "dWithNSString:withNSString:", "d", "V", 0x9, NULL },
    { "eWithNSString:withNSString:", "e", "V", 0x9, NULL },
    { "iWithNSString:withNSString:", "i", "V", 0x9, NULL },
    { "vWithNSString:withNSString:", "v", "V", 0x9, NULL },
    { "wWithNSString:withNSString:", "w", "V", 0x9, NULL },
    { "SetLoggerWithComRemulasceLametroappJava_coreAnalyticsLog:", "SetLogger", "V", 0x9, NULL },
    { "print_dWithNSString:withNSString:", "print_d", "V", 0x1, NULL },
    { "print_eWithNSString:withNSString:", "print_e", "V", 0x1, NULL },
    { "print_vWithNSString:withNSString:", "print_v", "V", 0x1, NULL },
    { "print_iWithNSString:withNSString:", "print_i", "V", 0x1, NULL },
    { "print_wWithNSString:withNSString:", "print_w", "V", 0x1, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "actualLogger_", NULL, 0xa, "Lcom.remulasce.lametroapp.java_core.analytics.Log;", &ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreAnalyticsLog = { 2, "Log", "com.remulasce.lametroapp.java_core.analytics", NULL, 0x1, 12, methods, 1, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreAnalyticsLog;
}

@end

void ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(NSString *tag, NSString *msg) {
  ComRemulasceLametroappJava_coreAnalyticsLog_init();
  if (ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ print_dWithNSString:tag withNSString:msg];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsLog_eWithNSString_withNSString_(NSString *tag, NSString *msg) {
  ComRemulasceLametroappJava_coreAnalyticsLog_init();
  if (ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ print_eWithNSString:tag withNSString:msg];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsLog_iWithNSString_withNSString_(NSString *tag, NSString *msg) {
  ComRemulasceLametroappJava_coreAnalyticsLog_init();
  if (ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ print_iWithNSString:tag withNSString:msg];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(NSString *tag, NSString *msg) {
  ComRemulasceLametroappJava_coreAnalyticsLog_init();
  if (ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ print_vWithNSString:tag withNSString:msg];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(NSString *tag, NSString *msg) {
  ComRemulasceLametroappJava_coreAnalyticsLog_init();
  if (ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ != nil) {
    [ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ print_wWithNSString:tag withNSString:msg];
  }
}

void ComRemulasceLametroappJava_coreAnalyticsLog_SetLoggerWithComRemulasceLametroappJava_coreAnalyticsLog_(ComRemulasceLametroappJava_coreAnalyticsLog *realLog) {
  ComRemulasceLametroappJava_coreAnalyticsLog_init();
  ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_ = realLog;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreAnalyticsLog)
