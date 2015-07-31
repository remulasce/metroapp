package com.remulasce.lametroapp.components.regions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.remulasce.lametroapp.MainActivity;
import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.RegionalizationHelper;
import com.remulasce.lametroapp.java_core.basic_types.Agency;

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

        View view = View.inflate(getActivity(), R.layout.region_settings_pane, null);

        addAgencyItems(view);


        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.settings_dialog_title))
                .setView(view).create();
    }

    private void addAgencyItems(View view) {
        ViewGroup list = (ViewGroup) view.findViewById(R.id.region_settings_pane_agencies_layout);
        list.removeAllViews();

        for(Agency a : RegionalizationHelper.getInstance().getInstalledAgencies()) {
            CheckBox agencyItem = new CheckBox(getActivity());
            agencyItem.setText(a.raw);
            agencyItem.setTag(a);

            list.addView(agencyItem);
        }
    }
}
