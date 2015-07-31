package com.remulasce.lametroapp.components.regions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.remulasce.lametroapp.MainActivity;
import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.RegionalizationHelper;
import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 7/30/2015.
 *
 * This is what's shown as the region-select dialog.
 *
 * Checkboxes per region, plus an auto-detect box.
 * Currently all hardcoded. Later to be dynamic.
 */
public class RegionSettingsDialogFragment extends DialogFragment {

    private ViewGroup checkBoxList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.region_settings_pane, null);

        checkBoxList = (ViewGroup) view.findViewById(R.id.region_settings_pane_agencies_layout);

        addAgencyItems();

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.settings_dialog_title))
                .setView(view).create();
    }

    private void addAgencyItems() {
        checkBoxList.removeAllViews();

        for(Agency a : RegionalizationHelper.getInstance().getInstalledAgencies()) {
            CheckBox agencyItem = new CheckBox(getActivity());
            agencyItem.setText(a.raw);
            agencyItem.setTag(a);

            if (RegionalizationHelper.getInstance().getActiveAgencies().contains(a)) {
                agencyItem.setChecked(true);
            }

            agencyItem.setOnCheckedChangeListener(onCheckedChangeListener);

            checkBoxList.addView(agencyItem);
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            setCheckedAgenciesActive();
        }
    };

    private void setCheckedAgenciesActive() {
        Collection<Agency> activeAgencies = new ArrayList<Agency>();

        for (int i = 0; i < checkBoxList.getChildCount(); i++) {
            CheckBox child = (CheckBox) checkBoxList.getChildAt(i);

            if (child.isChecked()) {
                activeAgencies.add((Agency)child.getTag());
            }
        }

        RegionalizationHelper.getInstance().setActiveAgencies(activeAgencies);
    }
}
