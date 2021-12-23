package com.remulasce.lametroapp.java_core.dynamic_data.types;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.StopServiceRequest;

/**
 * Created by Remulasce on 4/21/2015.
 *
 * <p>This trip conveys special status about active ServiceRequests. It lets the user know when the
 * request is going to network to get predictions or when an error has occured.
 *
 * <p>It should only be shown when those conditions arise.
 *
 * <p>Conditions are: - Fetching update from server - Error occured from server.
 *
 * <p>It is specific to the StopServiceRequest for now.
 *
 * <p>Pass-through method to the platform-specific display: - getRequestStatus() returns either
 * NOTHING, SPINNER, or ERROR That denotes whether to display an error message, a progress spinner,
 * or that actually this trip shouldn't be displayed at all. - getTitleText() returns a string to
 * display up top. eg. "Redondo Beach Station"
 *
 * <p>This kind of trip should probably not be added at all in the NOTHING case. But that's up to
 * ServiceRequests to ensure.
 *
 * <p>Circular dependency note: It's weird currently to have StopServiceRequest make this kind of
 * trip, then only show it sometimes. In the future it would be better for this Trip to reference an
 * interface like 'NetworkStatusable' or something, so this trip could be used to display the status
 * of different kinds of servicerequests.
 *
 * <p>(Really this is just a thin pass-through on the underlying ServiceRequest state)
 */
public class RequestStatusTrip extends Trip {

  private StopServiceRequest parentRequest;
  private boolean hidden = false;

  public enum RequestDisplayType {
    NOTHING,
    SPINNER,
    ERROR,
    EMPTY
  }

  public RequestStatusTrip(StopServiceRequest parent) {
    this.parentRequest = parent;
  }

  public String getTitleText() {
    return parentRequest.getDisplayName();
  }

  public RequestDisplayType getRequestStatus() {
    switch (parentRequest.getNetworkStatus()) {
      case NOTHING:
        return RequestDisplayType.NOTHING;
      case SPINNER:
        return RequestDisplayType.SPINNER;
      case ERROR:
        return RequestDisplayType.ERROR;
      case EMPTY:
        return RequestDisplayType.EMPTY;
      default:
        Log.w("RequestStatusTrip", "Unknown parent request status");
        return RequestDisplayType.NOTHING;
    }
  }

  public String getAgencyName() {
    return parentRequest.getAgencyName();
  }

  @Override
  public void dismiss() {
    this.hidden = true;
  }

  @Override
  public boolean isValid() {
    return !this.hidden;
  }

  public void restore() {
    this.hidden = false;
  }
}
