package com.canli.oya.traininventory.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.TrainDatabase;

import java.util.List;

public class CategoryBrandViewModel extends AndroidViewModel {

    private LiveData<List<String>> categoryList;
    private LiveData<List<String>> brandList;

    public CategoryBrandViewModel(@NonNull Application application) {
        super(application);
        TrainDatabase mDb = TrainDatabase.getInstance(this.getApplication());
        categoryList = mDb.categoryDao().getAllCategoryNames();
        brandList = mDb.brandDao().getAllBrandNames();
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<String>> getBrandList() {
        return brandList;
    }
}
