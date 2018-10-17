package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.TrainDatabase;

public class SearchViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TrainDatabase mDb;

    public SearchViewModelFactory(TrainDatabase database){
        mDb = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new SearchViewModel(mDb);
    }
}