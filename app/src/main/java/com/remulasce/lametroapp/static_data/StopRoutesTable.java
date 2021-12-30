package com.remulasce.lametroapp.static_data;

import java.util.Collection;

/**
 * Created by Remulasce on 6/11/2015.
 *
 * <p>This is a little half-baked because we didn't go and clean up the rest of the statics This
 * interface narrowly converts stopids->routes within an SQL table Like, string to string.
 *
 * <p>We hve a separate interface that actually takes a Stop and returns proper Routes. Something
 * something something abstraction.
 */
public interface StopRoutesTable {
  public Collection<String> getRoutesToStop(String stopID);
}
