package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.repositories.TrainRepository;

public class TrainsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TrainRepository mRepo;

    public TrainsViewModelFactory(TrainRepository repo){
        mRepo = repo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new TrainsViewModel(mRepo);
    }
}
