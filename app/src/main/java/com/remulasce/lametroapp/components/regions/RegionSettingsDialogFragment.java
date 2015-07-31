package com.remulasce.lametroapp.components.regions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.remulasce.lametroapp.MainActivity;
import com.remulasce.lametroapp.R;

/**
 * Created by Remulasce on 7/30/2015.
 *
 * This is what's shown as the region-select dialog.
 *
 * Checkboxes per region, plus an auto-detect box.
 * Currently all hardcoded. Later to be dynamic.
 */
public class RegionSettingsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.settings_dialog_title))
                .setView(View.inflate(getActivity(), R.layout.region_settings_pane, null)).create();
    }
}
