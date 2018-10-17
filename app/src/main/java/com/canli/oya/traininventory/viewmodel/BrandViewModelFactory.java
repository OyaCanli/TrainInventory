package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.repositories.BrandRepository;

public class BrandViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final BrandRepository mRepo;

    public BrandViewModelFactory(BrandRepository repo){
        mRepo = repo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new BrandsViewModel(mRepo);
    }
}