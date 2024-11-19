package com.example.cq_mobile.ui.myJob;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class myJobViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public myJobViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my Jobs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}