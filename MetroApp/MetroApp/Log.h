//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/analytics/Log.java
//

#ifndef _ComRemulasceLametroappJava_coreAnalyticsLog_H_
#define _ComRemulasceLametroappJava_coreAnalyticsLog_H_

#include "J2ObjC_header.h"

@interface ComRemulasceLametroappJava_coreAnalyticsLog : NSObject {
}

#pragma mark Public

- (instancetype)init;

+ (void)dWithNSString:(NSString *)tag
         withNSString:(NSString *)msg;

+ (void)eWithNSString:(NSString *)tag
         withNSString:(NSString *)msg;

+ (void)iWithNSString:(NSString *)tag
         withNSString:(NSString *)msg;

- (void)print_dWithNSString:(NSString *)tag
               withNSString:(NSString *)msg;

- (void)print_eWithNSString:(NSString *)tag
               withNSString:(NSString *)msg;

- (void)print_iWithNSString:(NSString *)tag
               withNSString:(NSString *)msg;

- (void)print_vWithNSString:(NSString *)tag
               withNSString:(NSString *)msg;

- (void)print_wWithNSString:(NSString *)tag
               withNSString:(NSString *)msg;

+ (void)SetLoggerWithComRemulasceLametroappJava_coreAnalyticsLog:(ComRemulasceLametroappJava_coreAnalyticsLog *)realLog;

+ (void)vWithNSString:(NSString *)tag
         withNSString:(NSString *)msg;

+ (void)wWithNSString:(NSString *)tag
         withNSString:(NSString *)msg;

@end

J2OBJC_EMPTY_STATIC_INIT(ComRemulasceLametroappJava_coreAnalyticsLog)

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(NSString *tag, NSString *msg);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsLog_eWithNSString_withNSString_(NSString *tag, NSString *msg);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsLog_iWithNSString_withNSString_(NSString *tag, NSString *msg);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(NSString *tag, NSString *msg);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsLog_wWithNSString_withNSString_(NSString *tag, NSString *msg);

FOUNDATION_EXPORT void ComRemulasceLametroappJava_coreAnalyticsLog_SetLoggerWithComRemulasceLametroappJava_coreAnalyticsLog_(ComRemulasceLametroappJava_coreAnalyticsLog *realLog);

FOUNDATION_EXPORT ComRemulasceLametroappJava_coreAnalyticsLog *ComRemulasceLametroappJava_coreAnalyticsLog_actualLogger_;
J2OBJC_STATIC_FIELD_GETTER(ComRemulasceLametroappJava_coreAnalyticsLog, actualLogger_, ComRemulasceLametroappJava_coreAnalyticsLog *)
J2OBJC_STATIC_FIELD_SETTER(ComRemulasceLametroappJava_coreAnalyticsLog, actualLogger_, ComRemulasceLametroappJava_coreAnalyticsLog *)

J2OBJC_TYPE_LITERAL_HEADER(ComRemulasceLametroappJava_coreAnalyticsLog)

#endif // _ComRemulasceLametroappJava_coreAnalyticsLog_H_