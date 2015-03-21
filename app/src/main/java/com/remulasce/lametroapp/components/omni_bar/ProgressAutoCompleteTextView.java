package com.remulasce.lametroapp.components.omni_bar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by Remulasce on 2/15/2015.
 *
 * Extension to autocompletetextview enables a progressbar spinnie thing when working
 * That mitigates having long search times.
 *
 * Also, show suggestion drop-down on 0 text input (as soon as it's focused)
 */
public class ProgressAutoCompleteTextView extends AutoCompleteTextView {
    public ProgressAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    private ProgressBar mLoadingIndicator;

    public void setLoadingIndicator(ProgressBar view) {
        mLoadingIndicator = view;
    }


    @Override
    public boolean enoughToFilter() {
        return true;
    }


    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        // the AutoCompleteTextview is about to start the filtering so show
        // the ProgressPager
        mLoadingIndicator.setVisibility(View.VISIBLE);
        super.performFiltering(text, keyCode);
    }

    @Override
    public void onFilterComplete(int count) {
        // the AutoCompleteTextView has done its job and it's about to show
        // the drop down so close/hide the ProgreeBar
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        super.onFilterComplete(count);
    }
}