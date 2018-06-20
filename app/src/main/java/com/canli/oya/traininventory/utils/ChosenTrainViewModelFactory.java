package com.canli.oya.traininventory.utils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.TrainDatabase;

public class ChosenTrainViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private final TrainDatabase mDb;
    private final int mTrainId;

    public ChosenTrainViewModelFactory(TrainDatabase database, int trainId) {
        mDb = database;
        mTrainId = trainId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChosenTrainViewModel(mDb, mTrainId);
    }
}
