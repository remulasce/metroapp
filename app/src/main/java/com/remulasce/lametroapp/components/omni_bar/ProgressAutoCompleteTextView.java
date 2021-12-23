package com.remulasce.lametroapp.components.omni_bar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
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
public class ProgressAutoCompleteTextView extends AutoCompleteTextView
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

    if (focused
        && getWindowVisibility() != View.GONE
        && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      performFiltering(getText(), 0);
      showDropDown();
    } else {
      Log.w(TAG, "Couldn't show dropdown because we don't have focus / window visibility");
    }
  }

  @Override
  public boolean enoughToFilter() {
    return true;
  }

  @Override
  public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
    super.setAdapter(adapter);

    // Ugh.
    if (adapter instanceof OmniAutoCompleteAdapter) {
      ((OmniAutoCompleteAdapter) adapter).setCompleteListener(this);
      showLoadingIndicator = true;
    } else {
      showLoadingIndicator = false;
      mLoadingIndicator.setVisibility(GONE);
      Log.w(
          TAG,
          "Expected adapter to bea OmniAutoCompleteAdapter, but it didn't. Progress spinner won't work.");
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
    // Hide soft keyboard- https://stackoverflow.com/questions/1109022
    Context context = view.getContext();
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
