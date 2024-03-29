package com.remulasce.lametroapp.components.omni_bar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.remulasce.lametroapp.java_core.analytics.Log;

/**
 * Created by Remulasce on 2/15/2015.
 *
 * <p>Extension to autocompletetextview enables a progressbar spinnie thing when working That
 * mitigates having long search times.
 *
 * <p>Also, show suggestion drop-down on 0 text input (as soon as it's focused)
 */
public class ProgressAutoCompleteTextView
    extends android.support.v7.widget.AppCompatAutoCompleteTextView
    implements FilterTaskCompleteListener {

  public static final String TAG = "Autocompleteview";

  public ProgressAutoCompleteTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  private ProgressBar mLoadingIndicator;
  private boolean showLoadingIndicator = false;

  public void setLoadingIndicator(ProgressBar view) {
    mLoadingIndicator = view;
  }

  @Override
  protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
    super.onFocusChanged(focused, direction, previouslyFocusedRect);

    if (focused && getWindowVisibility() != View.GONE) {
      Log.i(TAG, "Showing dropdown again on focus change");
      // Load-bearing for the unfocus-refocus case, without rotation or leaving the app.
      performFiltering(getText(), 0);
      showDropDown(); // Must be invoked separately.
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasWindowFocus) {
    // Load bearing for initial entry.
    performFiltering(getText(), 0);

    // The standard behavior is faulty and omitted completely.
    // It hides the popup window, which is not necessary, and forces the rest of the system to
    // recover after returning to the window from the app switcher.
  }

  @Override
  public boolean enoughToFilter() {
    return true;
  }

  @Override
  public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
    super.setAdapter(adapter);

    // Recheck filtering using the new adapter.
    // This is load-bearing for the initial inflation case.
    Log.i(TAG, "Refilter on adapter set");
    performFiltering(getText(), 0);
    // showDropDown(); Can't. Window isn't attached yet.

    if (adapter instanceof OmniAutoCompleteAdapter) {
      ((OmniAutoCompleteAdapter) adapter).setCompleteListener(this);
      showLoadingIndicator = true;
    } else {
      showLoadingIndicator = false;
      mLoadingIndicator.setVisibility(GONE);
      Log.w(
          TAG,
          "Expected adapter to be a OmniAutoCompleteAdapter, but it didn't. Progress spinner won't work.");
    }
  }

  @Override
  protected void performFiltering(CharSequence text, int keyCode) {
    // the AutoCompleteTextview is about to start the filtering so show
    // the ProgressPager
    if (showLoadingIndicator) {
      Log.d(TAG, "PerformFiltering, show progress spinner");
      mLoadingIndicator.setVisibility(View.VISIBLE);
    }
    super.performFiltering(text, keyCode);
  }

  @Override
  public void filterCompletionDetails(String constraint) {
    String text = this.getText().toString();
    // Just give up.
    if (constraint == null) {
      Log.w(TAG, "Filter of null returned? Just give up.");
      mLoadingIndicator.setVisibility(INVISIBLE);
    } else if (constraint.equals(text)) {
      Log.d(TAG, "Filter current text complete, hide progress spinner");
      mLoadingIndicator.setVisibility(INVISIBLE);
    } else {
      Log.d(TAG, "Filter returned results, but not for current text, so leave up progress spinner");
    }
  }

  // Defocus ourselves (hide text pointer) on back button
  @Override
  public boolean onKeyPreIme(int key_code, KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
      this.clearFocus();
      hideSoftKeyboard(this);
    }

    return super.onKeyPreIme(key_code, event);
  }

  private void hideSoftKeyboard(View view) {
    Log.i(TAG, "hideSoftKeyboard in ProgressAutoCompleteTextView");
    // Hide soft keyboard- https://stackoverflow.com/questions/1109022
    Context context = view.getContext();
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
