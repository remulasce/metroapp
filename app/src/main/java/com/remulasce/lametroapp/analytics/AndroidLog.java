package com.remulasce.lametroapp.analytics;

/**
 * Created by Remulasce on 3/4/2015.
 *
 * Does default Android logging.
 */
public class AndroidLog extends Log {

    @Override
    public void print_d(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }

    @Override
    public void print_e(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }

    @Override
    public void print_v(String tag, String msg) {
        android.util.Log.v(tag, msg);
    }

    @Override
    public void print_i(String tag, String msg) {
        android.util.Log.i(tag, msg);
    }

    @Override
    public void print_w(String tag, String msg) {
        android.util.Log.w(tag, msg);
    }
}
