package com.remulasce.lametroapp.display;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.remulasce.lametroapp.java_core.dynamic_data.types.Trip;

/**
 * Created by Remulasce on 3/5/2015.
 *
 * This is the first Android-specific layer on the type stack
 * It takes a trip and makes a View out of it.
 */
public interface AndroidDisplay {

    public View getView(ViewGroup parent, Context context, View recycleView);
    public Trip getTrip();
    public void executeAction(Context c);

}
