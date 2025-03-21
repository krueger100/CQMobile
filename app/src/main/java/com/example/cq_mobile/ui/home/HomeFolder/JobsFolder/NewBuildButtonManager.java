package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder;

import android.view.View;
import android.widget.LinearLayout;

public class NewBuildButtonManager {
    private LinearLayout notes, folder;

    public NewBuildButtonManager(LinearLayout notes, LinearLayout folder) {
        this.notes = notes;
        this.folder = folder;

    }

    // Example method to show/hide buttons
    public void setButtonsVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        notes.setVisibility(visibility);
        folder.setVisibility(visibility);

    }

    // Example method to add click listeners
    public void setButtonClickListener(View.OnClickListener listener) {
        notes.setOnClickListener(listener);
        folder.setOnClickListener(listener);

    }

    // Example method to update button states
    public void updateButtonStates(boolean isEnabled) {
        notes.setEnabled(isEnabled);
        folder.setEnabled(isEnabled);

    }
}
