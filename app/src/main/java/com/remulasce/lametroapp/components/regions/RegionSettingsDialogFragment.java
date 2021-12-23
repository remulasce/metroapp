package com.remulasce.lametroapp.components.regions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.remulasce.lametroapp.R;
import com.remulasce.lametroapp.java_core.RegionalizationHelper;
import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 7/30/2015.
 *
 * <p>This is what's shown as the region-select dialog.
 *
 * <p>Checkboxes per region, plus an auto-detect box. Currently all hardcoded. Later to be dynamic.
 */
public class RegionSettingsDialogFragment extends DialogFragment {

  private ViewGroup checkBoxList;
  private CheckBox autodetectCheckBox;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    View view = View.inflate(getActivity(), R.layout.region_settings_pane, null);

    checkBoxList = (ViewGroup) view.findViewById(R.id.region_settings_pane_agencies_layout);
    autodetectCheckBox =
        (CheckBox) view.findViewById(R.id.region_settings_pane_autodetect_checkbox);

    addAgencyItems();
    autodetectCheckBox.setChecked(RegionalizationHelper.getInstance().getAutoDetect());
    autodetectCheckBox.setOnCheckedChangeListener(autodetectCheckedChangeListener);

    return new AlertDialog.Builder(getActivity())
        .setTitle(getString(R.string.settings_dialog_title))
        .setView(view)
        .create();
  }

  CompoundButton.OnCheckedChangeListener autodetectCheckedChangeListener =
      new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          RegionalizationHelper.getInstance().setAutoDetect(b);

          // We don't want to deal with changing all the checkboxes when autodetect runs
          // So just close the window and let the user open again if s/he wants.
          if (b) {
            dismiss();
          }
        }
      };

  private void addAgencyItems() {
    checkBoxList.removeAllViews();

    for (Agency a : RegionalizationHelper.getInstance().getInstalledAgencies()) {
      CheckBox agencyItem = new CheckBox(getActivity());
      agencyItem.setPadding(0, 2, 0, 2);
      agencyItem.setText(a.displayName);
      agencyItem.setTag(a);

      if (RegionalizationHelper.getInstance().getActiveAgencies().contains(a)) {
        agencyItem.setChecked(true);
      }

      agencyItem.setOnCheckedChangeListener(onAgencyCheckedChangeListener);

      checkBoxList.addView(agencyItem);
    }
  }

  private CompoundButton.OnCheckedChangeListener onAgencyCheckedChangeListener =
      new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          setCheckedAgenciesActive();
          autodetectCheckBox.setChecked(false);
        }
      };

  private void setCheckedAgenciesActive() {
    Collection<Agency> activeAgencies = new ArrayList<Agency>();

    for (int i = 0; i < checkBoxList.getChildCount(); i++) {
      CheckBox child = (CheckBox) checkBoxList.getChildAt(i);

      if (child.isChecked()) {
        activeAgencies.add((Agency) child.getTag());
      }
    }

    RegionalizationHelper.getInstance().setActiveAgencies(activeAgencies);
  }
}
