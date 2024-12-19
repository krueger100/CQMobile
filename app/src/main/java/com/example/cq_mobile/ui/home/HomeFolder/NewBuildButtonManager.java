package com.example.cq_mobile.ui.home.HomeFolder;

import android.view.View;
import android.widget.LinearLayout;

public class NewBuildButtonManager {
    private LinearLayout notes, folder, docs, sheets;

    public NewBuildButtonManager(LinearLayout notes, LinearLayout folder, LinearLayout docs, LinearLayout sheets) {
        this.notes = notes;
        this.folder = folder;
        this.docs = docs;
        this.sheets = sheets;
    }

    // Example method to show/hide buttons
    public void setButtonsVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        notes.setVisibility(visibility);
        folder.setVisibility(visibility);
        docs.setVisibility(visibility);
        sheets.setVisibility(visibility);
    }

    // Example method to add click listeners
    public void setButtonClickListener(View.OnClickListener listener) {
        notes.setOnClickListener(listener);
        folder.setOnClickListener(listener);
        docs.setOnClickListener(listener);
        sheets.setOnClickListener(listener);
    }

    // Example method to update button states
    public void updateButtonStates(boolean isEnabled) {
        notes.setEnabled(isEnabled);
        folder.setEnabled(isEnabled);
        docs.setEnabled(isEnabled);
        sheets.setEnabled(isEnabled);
    }
}
