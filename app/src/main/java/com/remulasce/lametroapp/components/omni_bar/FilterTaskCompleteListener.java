package com.remulasce.lametroapp.components.omni_bar;

/**
 * Created by Remulasce on 5/20/2015.
 *
 * This allows types implementing this interface to know which Filter operations they have requested
 * have actually returned.
 *
 * The standard Filter listener system is all super asynchronous. You send off a new filter request
 *   for every letter the user types, and then eventually each may return some values.
 *
 * The problem is you don't know if the values you receive are based on the full entered text, or
 *   if they're from a request sent off before the user finished typing.
 *
 * That means you don't know if there's still operations in the background occuring which will
 *   return more better results. You need to know that in order to display a 'loading' indicator.
 *
 * So, you implement this interface, and then your Filter will be able to tell you if there's
 *   any pending operations.
 */
public interface FilterTaskCompleteListener {
    public void filterCompletionDetails(String constraint);
}
