package com.remulasce.lametroapp.components.omni_bar;

import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.remulasce.lametroapp.java_core.analytics.Log;

/**
 * Created by Remulasce on 2/15/2015.
 *
 * Extension to autocompletetextview enables a progressbar spinnie thing when working
 * That mitigates having long search times.
 *
 * Also, show suggestion drop-down on 0 text input (as soon as it's focused)
 */
public class ProgressAutoCompleteTextView extends AutoCompleteTextView implements FilterTaskCompleteListener {

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
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused && getWindowVisibility() != View.GONE && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
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
            Log.w(TAG, "Expected adapter to bea OmniAutoCompleteAdapter, but it didn't. Progress spinner won't work.");
        }
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        // the AutoCompleteTextview is about to start the filtering so show
        // the ProgressPager
        if (showLoadingIndicator) {
            Log.w(TAG,"PerformFiltering, show progress spinner");
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        super.performFiltering(text, keyCode);
    }


    @Override
    public void filterCompletionDetails(String constraint) {
        String text = this.getText().toString();
        if (constraint.equals(text)) {
            Log.w(TAG, "Filter current text complete, hide progress spinner");
            mLoadingIndicator.setVisibility(INVISIBLE);
        } else {
            Log.w(TAG, "Filter returned results, but not for current text, so leave up progress spinner");
        }
    }
}