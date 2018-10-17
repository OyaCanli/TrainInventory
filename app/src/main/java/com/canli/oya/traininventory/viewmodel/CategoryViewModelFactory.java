package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.repositories.CategoryRepository;

public class CategoryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final CategoryRepository mRepo;

    public CategoryViewModelFactory(CategoryRepository repo){
        mRepo = repo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new CategoryViewModel(mRepo);
    }
}
