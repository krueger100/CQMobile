package com.example.cq_mobile.ui.MoreInFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MoreModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MoreModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my Jobs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}