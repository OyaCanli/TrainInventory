package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.data.repositories.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final LiveData<List<String>> categoryList;
    private final CategoryRepository mRepo;

    CategoryViewModel(CategoryRepository categoryRepo) {
        categoryList = categoryRepo.getCategoryList();
        mRepo = categoryRepo;
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public void deleteCategory(CategoryEntry category){
        mRepo.deleteCategory(category);
    }

    public void insertCategory(CategoryEntry category){
        mRepo.insertCategory(category);
    }

    public boolean isThisCategoryUsed(String category){
        return mRepo.isThisCategoryUsed(category);
    }
}
