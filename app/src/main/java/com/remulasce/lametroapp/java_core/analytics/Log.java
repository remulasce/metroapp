package com.remulasce.lametroapp.java_core.analytics;

/**
 * Created by Remulasce on 3/4/2015.
 *
 * <p>Quick abstraction of the Android builtin logging. Default does nothing so you don't have to
 * worry about it.
 *
 * <p>Subclass Log and set the actual logger someplace to get real logs.
 */
public class Log {
  public static void d(String tag, String msg) {
    if (actualLogger != null) {
      actualLogger.print_d(tag, msg);
    }
  }

  public static void e(String tag, String msg) {
    if (actualLogger != null) {
      actualLogger.print_e(tag, msg);
    }
  }

  public static void i(String tag, String msg) {
    if (actualLogger != null) {
      actualLogger.print_i(tag, msg);
    }
  }

  public static void v(String tag, String msg) {
    if (actualLogger != null) {
      actualLogger.print_v(tag, msg);
    }
  }

  public static void w(String tag, String msg) {
    if (actualLogger != null) {
      actualLogger.print_w(tag, msg);
    }
  }

  private static Log actualLogger;

  public static void SetLogger(Log realLog) {
    actualLogger = realLog;
  }

  public void print_d(String tag, String msg) {}

  public void print_e(String tag, String msg) {}

  public void print_v(String tag, String msg) {}

  public void print_i(String tag, String msg) {}

  public void print_w(String tag, String msg) {}
}
