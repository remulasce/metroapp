package com.remulasce.lametroapp.display;

import com.remulasce.lametroapp.dynamic_data.types.PredictionUpdateCallback;
import com.remulasce.lametroapp.dynamic_data.types.TripUpdateCallback;

/**
 * Created by Remulasce on 3/5/2015.
 */
public interface PredictionUI extends PredictionUpdateCallback {
    public void setTripUpdateCallback(TripUpdateCallback t);
    public void stopPredicting();
}
