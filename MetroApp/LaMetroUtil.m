//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/nighelles/Desktop/MetroAppIOS/MetroApp/MetroApp/metroapp/app/src/main/java/com/remulasce/lametroapp/java_core/LaMetroUtil.java
//

#include "Arrival.h"
#include "BasicLocation.h"
#include "Destination.h"
#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "LaMetroUtil.h"
#include "Log.h"
#include "Route.h"
#include "RouteColor.h"
#include "RouteColorer.h"
#include "Stop.h"
#include "StopLocationTranslator.h"
#include "Vehicle.h"
#include "java/io/IOException.h"
#include "java/io/StringReader.h"
#include "java/lang/Exception.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/lang/Integer.h"
#include "java/util/ArrayList.h"
#include "java/util/List.h"
#include "javax/xml/parsers/DocumentBuilder.h"
#include "javax/xml/parsers/DocumentBuilderFactory.h"
#include "javax/xml/parsers/ParserConfigurationException.h"
#include "org/w3c/dom/Document.h"
#include "org/w3c/dom/Element.h"
#include "org/w3c/dom/Node.h"
#include "org/w3c/dom/NodeList.h"
#include "org/xml/sax/InputSource.h"
#include "org/xml/sax/SAXException.h"
#include "org/xmlpull/v1/XmlPullParser.h"
#include "org/xmlpull/v1/XmlPullParserException.h"
#include "org/xmlpull/v1/XmlPullParserFactory.h"

__attribute__((unused)) static void ComRemulasceLametroappJava_coreLaMetroUtil_parseWithJavaLibsWithNSString_withJavaUtilList_(NSString *response, id<JavaUtilList> ret);
__attribute__((unused)) static NSString *ComRemulasceLametroappJava_coreLaMetroUtil_cleanupStopIDWithNSString_(NSString *stopIDAttribute);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreLaMetroUtil_parseWithAndroidLibsWithNSString_withJavaUtilList_(NSString *response, id<JavaUtilList> ret);
__attribute__((unused)) static void ComRemulasceLametroappJava_coreLaMetroUtil_addNewArrivalWithJavaUtilList_withInt_withComRemulasceLametroappJava_coreBasic_typesDestination_withComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesVehicle_(id<JavaUtilList> ret, jint seconds, ComRemulasceLametroappJava_coreBasic_typesDestination *d, ComRemulasceLametroappJava_coreBasic_typesRoute *r, ComRemulasceLametroappJava_coreBasic_typesStop *s, ComRemulasceLametroappJava_coreBasic_typesVehicle *v);

@interface ComRemulasceLametroappJava_coreLaMetroUtil () {
}

+ (void)parseWithJavaLibsWithNSString:(NSString *)response
                     withJavaUtilList:(id<JavaUtilList>)ret;

+ (NSString *)cleanupStopIDWithNSString:(NSString *)stopIDAttribute;

+ (void)parseWithAndroidLibsWithNSString:(NSString *)response
                        withJavaUtilList:(id<JavaUtilList>)ret;

+ (void)addNewArrivalWithJavaUtilList:(id<JavaUtilList>)ret
                              withInt:(jint)seconds
withComRemulasceLametroappJava_coreBasic_typesDestination:(ComRemulasceLametroappJava_coreBasic_typesDestination *)d
withComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)r
withComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)s
withComRemulasceLametroappJava_coreBasic_typesVehicle:(ComRemulasceLametroappJava_coreBasic_typesVehicle *)v;
@end

@implementation ComRemulasceLametroappJava_coreLaMetroUtil

NSString * ComRemulasceLametroappJava_coreLaMetroUtil_NEXTBUS_FEED_URL_ = @"http://webservices.nextbus.com/service/publicXMLFeed";
NSString * ComRemulasceLametroappJava_coreLaMetroUtil_TAG_ = @"LaMetroUtil";
id<ComRemulasceLametroappJava_coreStatic_dataStopLocationTranslator> ComRemulasceLametroappJava_coreLaMetroUtil_locationTranslator_;
id<ComRemulasceLametroappJava_coreStatic_dataRouteColorer> ComRemulasceLametroappJava_coreLaMetroUtil_routeColorer_;

+ (jboolean)isValidStopWithNSString:(NSString *)stop {
  return ComRemulasceLametroappJava_coreLaMetroUtil_isValidStopWithNSString_(stop);
}

+ (jboolean)isValidRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)route {
  return ComRemulasceLametroappJava_coreLaMetroUtil_isValidRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_(route);
}

+ (NSString *)makePredictionsRequestWithComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)stop
                                   withComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)route {
  return ComRemulasceLametroappJava_coreLaMetroUtil_makePredictionsRequestWithComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesRoute_(stop, route);
}

+ (id<JavaUtilList>)parseAllArrivalsWithNSString:(NSString *)response {
  return ComRemulasceLametroappJava_coreLaMetroUtil_parseAllArrivalsWithNSString_(response);
}

+ (void)parseWithJavaLibsWithNSString:(NSString *)response
                     withJavaUtilList:(id<JavaUtilList>)ret {
  ComRemulasceLametroappJava_coreLaMetroUtil_parseWithJavaLibsWithNSString_withJavaUtilList_(response, ret);
}

+ (NSString *)cleanupStopIDWithNSString:(NSString *)stopIDAttribute {
  return ComRemulasceLametroappJava_coreLaMetroUtil_cleanupStopIDWithNSString_(stopIDAttribute);
}

+ (void)parseWithAndroidLibsWithNSString:(NSString *)response
                        withJavaUtilList:(id<JavaUtilList>)ret {
  ComRemulasceLametroappJava_coreLaMetroUtil_parseWithAndroidLibsWithNSString_withJavaUtilList_(response, ret);
}

+ (void)addNewArrivalWithJavaUtilList:(id<JavaUtilList>)ret
                              withInt:(jint)seconds
withComRemulasceLametroappJava_coreBasic_typesDestination:(ComRemulasceLametroappJava_coreBasic_typesDestination *)d
withComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)r
withComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)s
withComRemulasceLametroappJava_coreBasic_typesVehicle:(ComRemulasceLametroappJava_coreBasic_typesVehicle *)v {
  ComRemulasceLametroappJava_coreLaMetroUtil_addNewArrivalWithJavaUtilList_withInt_withComRemulasceLametroappJava_coreBasic_typesDestination_withComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesVehicle_(ret, seconds, d, r, s, v);
}

+ (NSString *)timeToDisplayWithInt:(jint)seconds {
  return ComRemulasceLametroappJava_coreLaMetroUtil_timeToDisplayWithInt_(seconds);
}

+ (NSString *)standaloneTimeToDisplayWithInt:(jint)seconds {
  return ComRemulasceLametroappJava_coreLaMetroUtil_standaloneTimeToDisplayWithInt_(seconds);
}

+ (NSString *)standaloneSecondsRemainderTimeWithInt:(jint)seconds {
  return ComRemulasceLametroappJava_coreLaMetroUtil_standaloneSecondsRemainderTimeWithInt_(seconds);
}

+ (NSString *)getAgencyFromRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:(ComRemulasceLametroappJava_coreBasic_typesRoute *)route
                                 withComRemulasceLametroappJava_coreBasic_typesStop:(ComRemulasceLametroappJava_coreBasic_typesStop *)stop {
  return ComRemulasceLametroappJava_coreLaMetroUtil_getAgencyFromRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_(route, stop);
}

- (instancetype)init {
  return [super init];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "isValidStopWithNSString:", "isValidStop", "Z", 0x9, NULL },
    { "isValidRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:", "isValidRoute", "Z", 0x9, NULL },
    { "makePredictionsRequestWithComRemulasceLametroappJava_coreBasic_typesStop:withComRemulasceLametroappJava_coreBasic_typesRoute:", "makePredictionsRequest", "Ljava.lang.String;", 0x9, NULL },
    { "parseAllArrivalsWithNSString:", "parseAllArrivals", "Ljava.util.List;", 0x9, NULL },
    { "parseWithJavaLibsWithNSString:withJavaUtilList:", "parseWithJavaLibs", "V", 0xa, NULL },
    { "cleanupStopIDWithNSString:", "cleanupStopID", "Ljava.lang.String;", 0xa, NULL },
    { "parseWithAndroidLibsWithNSString:withJavaUtilList:", "parseWithAndroidLibs", "V", 0xa, NULL },
    { "addNewArrivalWithJavaUtilList:withInt:withComRemulasceLametroappJava_coreBasic_typesDestination:withComRemulasceLametroappJava_coreBasic_typesRoute:withComRemulasceLametroappJava_coreBasic_typesStop:withComRemulasceLametroappJava_coreBasic_typesVehicle:", "addNewArrival", "V", 0xa, NULL },
    { "timeToDisplayWithInt:", "timeToDisplay", "Ljava.lang.String;", 0x9, NULL },
    { "standaloneTimeToDisplayWithInt:", "standaloneTimeToDisplay", "Ljava.lang.String;", 0x9, NULL },
    { "standaloneSecondsRemainderTimeWithInt:", "standaloneSecondsRemainderTime", "Ljava.lang.String;", 0x9, NULL },
    { "getAgencyFromRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:withComRemulasceLametroappJava_coreBasic_typesStop:", "getAgencyFromRoute", "Ljava.lang.String;", 0x9, "Ljava.lang.IllegalArgumentException;" },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "NEXTBUS_FEED_URL_", NULL, 0x1a, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreLaMetroUtil_NEXTBUS_FEED_URL_,  },
    { "TAG_", NULL, 0x19, "Ljava.lang.String;", &ComRemulasceLametroappJava_coreLaMetroUtil_TAG_,  },
    { "locationTranslator_", NULL, 0x9, "Lcom.remulasce.lametroapp.java_core.static_data.StopLocationTranslator;", &ComRemulasceLametroappJava_coreLaMetroUtil_locationTranslator_,  },
    { "routeColorer_", NULL, 0x9, "Lcom.remulasce.lametroapp.java_core.static_data.RouteColorer;", &ComRemulasceLametroappJava_coreLaMetroUtil_routeColorer_,  },
  };
  static const J2ObjcClassInfo _ComRemulasceLametroappJava_coreLaMetroUtil = { 2, "LaMetroUtil", "com.remulasce.lametroapp.java_core", NULL, 0x1, 13, methods, 4, fields, 0, NULL, 0, NULL};
  return &_ComRemulasceLametroappJava_coreLaMetroUtil;
}

@end

jboolean ComRemulasceLametroappJava_coreLaMetroUtil_isValidStopWithNSString_(NSString *stop) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  if (stop == nil) {
    return NO;
  }
  if ([((NSString *) nil_chk(stop)) isEmpty]) {
    return NO;
  }
  @try {
    jint stopNum = JavaLangInteger_parseIntWithNSString_(stop);
    if (stopNum <= 0) {
      return NO;
    }
  }
  @catch (JavaLangException *e) {
    return NO;
  }
  return YES;
}

jboolean ComRemulasceLametroappJava_coreLaMetroUtil_isValidRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_(ComRemulasceLametroappJava_coreBasic_typesRoute *route) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  if (route == nil || ![route isValid]) return NO;
  @try {
    jint routeNum = [JavaLangInteger_valueOfWithNSString_([((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk(route)) getString]) intValue];
    return routeNum > 0 && routeNum < 1000;
  }
  @catch (JavaLangException *e) {
    return NO;
  }
}

NSString *ComRemulasceLametroappJava_coreLaMetroUtil_makePredictionsRequestWithComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesRoute_(ComRemulasceLametroappJava_coreBasic_typesStop *stop, ComRemulasceLametroappJava_coreBasic_typesRoute *route) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  NSString *agency = ComRemulasceLametroappJava_coreLaMetroUtil_getAgencyFromRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_(route, stop);
  NSString *URI = JreStrcat("$$$$$", ComRemulasceLametroappJava_coreLaMetroUtil_NEXTBUS_FEED_URL_, @"?command=predictions&a=", agency, @"&stopId=", [((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk(stop)) getString]);
  if (ComRemulasceLametroappJava_coreLaMetroUtil_isValidRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_(route)) {
    URI = JreStrcat("$$", URI, JreStrcat("$$", @"&routeTag=", [((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk(route)) getString]));
  }
  return URI;
}

id<JavaUtilList> ComRemulasceLametroappJava_coreLaMetroUtil_parseAllArrivalsWithNSString_(NSString *response) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  id<JavaUtilList> ret = [[JavaUtilArrayList alloc] init];
  ComRemulasceLametroappJava_coreLaMetroUtil_parseWithJavaLibsWithNSString_withJavaUtilList_(response, ret);
  return ret;
}

void ComRemulasceLametroappJava_coreLaMetroUtil_parseWithJavaLibsWithNSString_withJavaUtilList_(NSString *response, id<JavaUtilList> ret) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  JavaxXmlParsersDocumentBuilderFactory *dbf = JavaxXmlParsersDocumentBuilderFactory_newInstance();
  @try {
    JavaxXmlParsersDocumentBuilder *db = [((JavaxXmlParsersDocumentBuilderFactory *) nil_chk(dbf)) newDocumentBuilder];
    id<OrgW3cDomDocument> dom = [((JavaxXmlParsersDocumentBuilder *) nil_chk(db)) parseWithOrgXmlSaxInputSource:[[OrgXmlSaxInputSource alloc] initWithJavaIoReader:[[JavaIoStringReader alloc] initWithNSString:response]]];
    id<OrgW3cDomElement> docEle = [((id<OrgW3cDomDocument>) nil_chk(dom)) getDocumentElement];
    id<OrgW3cDomNodeList> predictions = [((id<OrgW3cDomElement>) nil_chk(docEle)) getElementsByTagNameWithNSString:@"predictions"];
    if (predictions != nil && [predictions getLength] > 0) {
      for (jint i = 0; i < [predictions getLength]; i++) {
        id<OrgW3cDomElement> prediction = (id<OrgW3cDomElement>) check_protocol_cast([predictions itemWithInt:i], @protocol(OrgW3cDomElement));
        id<OrgW3cDomNodeList> directions = [((id<OrgW3cDomElement>) nil_chk(prediction)) getElementsByTagNameWithNSString:@"direction"];
        for (jint j = 0; j < [((id<OrgW3cDomNodeList>) nil_chk(directions)) getLength]; j++) {
          id<OrgW3cDomElement> direction = (id<OrgW3cDomElement>) check_protocol_cast([directions itemWithInt:j], @protocol(OrgW3cDomElement));
          id<OrgW3cDomNodeList> arrivals = [((id<OrgW3cDomElement>) nil_chk(direction)) getElementsByTagNameWithNSString:@"prediction"];
          for (jint k = 0; k < [((id<OrgW3cDomNodeList>) nil_chk(arrivals)) getLength]; k++) {
            id<OrgW3cDomElement> arrival = (id<OrgW3cDomElement>) check_protocol_cast([arrivals itemWithInt:k], @protocol(OrgW3cDomElement));
            jint seconds = JavaLangInteger_parseIntWithNSString_([((id<OrgW3cDomElement>) nil_chk(arrival)) getAttributeWithNSString:@"seconds"]);
            NSString *directionAttribute = [direction getAttributeWithNSString:@"title"];
            NSString *routeAttribute = [prediction getAttributeWithNSString:@"routeTag"];
            NSString *stopIDAttribute = [prediction getAttributeWithNSString:@"stopTag"];
            NSString *stopTitleAttribute = [prediction getAttributeWithNSString:@"stopTitle"];
            NSString *vehicleAttribute = [arrival getAttributeWithNSString:@"vehicle"];
            stopIDAttribute = ComRemulasceLametroappJava_coreLaMetroUtil_cleanupStopIDWithNSString_(stopIDAttribute);
            ComRemulasceLametroappJava_coreBasic_typesDestination *d = [[ComRemulasceLametroappJava_coreBasic_typesDestination alloc] initWithNSString:directionAttribute];
            ComRemulasceLametroappJava_coreBasic_typesRoute *r = [[ComRemulasceLametroappJava_coreBasic_typesRoute alloc] initWithNSString:routeAttribute];
            ComRemulasceLametroappJava_coreBasic_typesStop *s = [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithNSString:stopIDAttribute];
            ComRemulasceLametroappJava_coreBasic_typesVehicle *v = [[ComRemulasceLametroappJava_coreBasic_typesVehicle alloc] initWithNSString:vehicleAttribute];
            [s setStopNameWithNSString:stopTitleAttribute];
            ComRemulasceLametroappJava_coreLaMetroUtil_addNewArrivalWithJavaUtilList_withInt_withComRemulasceLametroappJava_coreBasic_typesDestination_withComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesVehicle_(ret, seconds, d, r, s, v);
            ComRemulasceLametroappJava_coreAnalyticsLog_dWithNSString_withNSString_(ComRemulasceLametroappJava_coreLaMetroUtil_TAG_, [arrival description]);
          }
        }
      }
    }
  }
  @catch (JavaxXmlParsersParserConfigurationException *pce) {
    [((JavaxXmlParsersParserConfigurationException *) nil_chk(pce)) printStackTrace];
  }
  @catch (OrgXmlSaxSAXException *se) {
    [((OrgXmlSaxSAXException *) nil_chk(se)) printStackTrace];
  }
  @catch (JavaIoIOException *ioe) {
    [((JavaIoIOException *) nil_chk(ioe)) printStackTrace];
  }
}

NSString *ComRemulasceLametroappJava_coreLaMetroUtil_cleanupStopIDWithNSString_(NSString *stopIDAttribute) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  jint indexOf_ = [((NSString *) nil_chk(stopIDAttribute)) indexOf:'_'];
  if (indexOf_ > 0) {
    stopIDAttribute = [stopIDAttribute substring:0 endIndex:[stopIDAttribute indexOf:'_']];
  }
  return stopIDAttribute;
}

void ComRemulasceLametroappJava_coreLaMetroUtil_parseWithAndroidLibsWithNSString_withJavaUtilList_(NSString *response, id<JavaUtilList> ret) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  OrgXmlpullV1XmlPullParserFactory *factory;
  @try {
    factory = OrgXmlpullV1XmlPullParserFactory_newInstance();
    [((OrgXmlpullV1XmlPullParserFactory *) nil_chk(factory)) setNamespaceAwareWithBoolean:YES];
    id<OrgXmlpullV1XmlPullParser> xpp = [factory newPullParser];
    [((id<OrgXmlpullV1XmlPullParser>) nil_chk(xpp)) setInputWithJavaIoReader:[[JavaIoStringReader alloc] initWithNSString:response]];
    jint eventType = [xpp getEventType];
    NSString *curStopName = @"";
    NSString *curDestination = @"";
    NSString *curRoute = @"";
    NSString *curStopTag = @"";
    while (eventType != OrgXmlpullV1XmlPullParser_END_DOCUMENT) {
      if (eventType == OrgXmlpullV1XmlPullParser_START_DOCUMENT) {
      }
      else if (eventType == OrgXmlpullV1XmlPullParser_START_TAG) {
        NSString *name = [xpp getName];
        if ([((NSString *) nil_chk(name)) isEqual:@"predictions"]) {
          curStopTag = [xpp getAttributeValueWithNSString:nil withNSString:@"stopTag"];
          curStopName = [xpp getAttributeValueWithNSString:nil withNSString:@"stopTitle"];
          curRoute = [xpp getAttributeValueWithNSString:nil withNSString:@"routeTag"];
        }
        if ([name isEqual:@"direction"]) {
          curDestination = [xpp getAttributeValueWithNSString:nil withNSString:@"title"];
        }
        if ([name isEqual:@"prediction"]) {
          NSString *vehicleNum;
          jint seconds = -1;
          NSString *timeString = [xpp getAttributeValueWithNSString:nil withNSString:@"seconds"];
          seconds = [JavaLangInteger_valueOfWithNSString_(timeString) intValue];
          vehicleNum = [xpp getAttributeValueWithNSString:nil withNSString:@"vehicle"];
          jboolean updated = NO;
          for (ComRemulasceLametroappJava_coreDynamic_dataTypesArrival * __strong aa in nil_chk(ret)) {
            if ([((ComRemulasceLametroappJava_coreBasic_typesDestination *) nil_chk([((ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *) nil_chk(aa)) getDirection])) isEqual:curDestination]) {
              updated = YES;
              if ([aa getEstimatedArrivalSeconds] > seconds) {
                [aa setEstimatedArrivalSecondsWithFloat:seconds];
              }
            }
          }
          if (!updated) {
            curStopTag = ComRemulasceLametroappJava_coreLaMetroUtil_cleanupStopIDWithNSString_(curStopTag);
            ComRemulasceLametroappJava_coreBasic_typesDestination *d = [[ComRemulasceLametroappJava_coreBasic_typesDestination alloc] initWithNSString:curDestination];
            ComRemulasceLametroappJava_coreBasic_typesRoute *r = [[ComRemulasceLametroappJava_coreBasic_typesRoute alloc] initWithNSString:curRoute];
            ComRemulasceLametroappJava_coreBasic_typesStop *s = [[ComRemulasceLametroappJava_coreBasic_typesStop alloc] initWithNSString:curStopTag];
            [s setStopNameWithNSString:curStopName];
            ComRemulasceLametroappJava_coreBasic_typesVehicle *v = [[ComRemulasceLametroappJava_coreBasic_typesVehicle alloc] initWithNSString:vehicleNum];
            ComRemulasceLametroappJava_coreLaMetroUtil_addNewArrivalWithJavaUtilList_withInt_withComRemulasceLametroappJava_coreBasic_typesDestination_withComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesVehicle_(ret, seconds, d, r, s, v);
          }
        }
      }
      else if (eventType == OrgXmlpullV1XmlPullParser_END_TAG) {
      }
      else if (eventType == OrgXmlpullV1XmlPullParser_TEXT) {
      }
      eventType = [xpp next];
    }
  }
  @catch (OrgXmlpullV1XmlPullParserException *e1) {
    [((OrgXmlpullV1XmlPullParserException *) nil_chk(e1)) printStackTrace];
  }
  @catch (JavaIoIOException *e) {
    [((JavaIoIOException *) nil_chk(e)) printStackTrace];
  }
}

void ComRemulasceLametroappJava_coreLaMetroUtil_addNewArrivalWithJavaUtilList_withInt_withComRemulasceLametroappJava_coreBasic_typesDestination_withComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_withComRemulasceLametroappJava_coreBasic_typesVehicle_(id<JavaUtilList> ret, jint seconds, ComRemulasceLametroappJava_coreBasic_typesDestination *d, ComRemulasceLametroappJava_coreBasic_typesRoute *r, ComRemulasceLametroappJava_coreBasic_typesStop *s, ComRemulasceLametroappJava_coreBasic_typesVehicle *v) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  ComRemulasceLametroappJava_coreAnalyticsLog_vWithNSString_withNSString_(ComRemulasceLametroappJava_coreLaMetroUtil_TAG_, JreStrcat("$IC@C@C@C@", @"Adding new arrival ", seconds, ' ', d, ' ', r, ' ', s, ' ', v));
  if (ComRemulasceLametroappJava_coreLaMetroUtil_locationTranslator_ != nil) {
    [((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk(s)) setLocationWithComRemulasceLametroappJava_coreBasic_typesBasicLocation:[ComRemulasceLametroappJava_coreLaMetroUtil_locationTranslator_ getStopLocationWithComRemulasceLametroappJava_coreBasic_typesStop:s]];
  }
  if (ComRemulasceLametroappJava_coreLaMetroUtil_routeColorer_ != nil) {
    [((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk(r)) setColorWithComRemulasceLametroappJava_coreStatic_dataTypesRouteColor:[ComRemulasceLametroappJava_coreLaMetroUtil_routeColorer_ getColorWithComRemulasceLametroappJava_coreBasic_typesRoute:r]];
  }
  ComRemulasceLametroappJava_coreDynamic_dataTypesArrival *a = [[ComRemulasceLametroappJava_coreDynamic_dataTypesArrival alloc] init];
  [a setDestinationWithComRemulasceLametroappJava_coreBasic_typesDestination:d];
  [a setRouteWithComRemulasceLametroappJava_coreBasic_typesRoute:r];
  [a setStopWithComRemulasceLametroappJava_coreBasic_typesStop:s];
  [a setEstimatedArrivalSecondsWithFloat:seconds];
  [a setVehicleWithComRemulasceLametroappJava_coreBasic_typesVehicle:v];
  [((id<JavaUtilList>) nil_chk(ret)) addWithId:a];
}

NSString *ComRemulasceLametroappJava_coreLaMetroUtil_timeToDisplayWithInt_(jint seconds) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  if (seconds > 60) {
    return JreStrcat("$$", @"in ", ComRemulasceLametroappJava_coreLaMetroUtil_standaloneTimeToDisplayWithInt_(seconds));
  }
  if (seconds > 1) {
    return JreStrcat("$$", @"in ", ComRemulasceLametroappJava_coreLaMetroUtil_standaloneTimeToDisplayWithInt_(seconds));
  }
  if (seconds == 0) {
    return JreStrcat("$$", @"in ", ComRemulasceLametroappJava_coreLaMetroUtil_standaloneTimeToDisplayWithInt_(seconds));
  }
  return ComRemulasceLametroappJava_coreLaMetroUtil_standaloneTimeToDisplayWithInt_(seconds);
}

NSString *ComRemulasceLametroappJava_coreLaMetroUtil_standaloneTimeToDisplayWithInt_(jint seconds) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  if (seconds > 60) {
    return JreStrcat("$$", NSString_valueOfInt_(seconds / 60), @" min");
  }
  if (seconds > 1) {
    return JreStrcat("$C", NSString_valueOfInt_(seconds), 's');
  }
  if (seconds == 0) {
    return @"1s";
  }
  return @"arrived";
}

NSString *ComRemulasceLametroappJava_coreLaMetroUtil_standaloneSecondsRemainderTimeWithInt_(jint seconds) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  if (seconds <= 60) {
    return @"";
  }
  return JreStrcat("IC", (seconds % 60), 's');
}

NSString *ComRemulasceLametroappJava_coreLaMetroUtil_getAgencyFromRouteWithComRemulasceLametroappJava_coreBasic_typesRoute_withComRemulasceLametroappJava_coreBasic_typesStop_(ComRemulasceLametroappJava_coreBasic_typesRoute *route, ComRemulasceLametroappJava_coreBasic_typesStop *stop) {
  ComRemulasceLametroappJava_coreLaMetroUtil_init();
  @try {
    if (route == nil || ![route isValid]) {
      if ([((ComRemulasceLametroappJava_coreBasic_typesStop *) nil_chk(stop)) getNum] > 80000 && [stop getNum] < 81000) {
        return @"lametro-rail";
      }
      return @"lametro";
    }
    jint routeN = [JavaLangInteger_valueOfWithNSString_([((ComRemulasceLametroappJava_coreBasic_typesRoute *) nil_chk(route)) getString]) intValue];
    if (routeN / 100 == 8) {
      return @"lametro-rail";
    }
    else if (routeN > 0 && routeN < 1000) {
      return @"lametro";
    }
    else {
      return @"lametro";
    }
  }
  @catch (JavaLangException *e) {
    @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:[((JavaLangException *) nil_chk(e)) getLocalizedMessage]];
  }
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ComRemulasceLametroappJava_coreLaMetroUtil)