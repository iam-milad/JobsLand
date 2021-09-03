package com.example.jobland.ui.JobRequests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class JobRequestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public JobRequestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is job requests fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}