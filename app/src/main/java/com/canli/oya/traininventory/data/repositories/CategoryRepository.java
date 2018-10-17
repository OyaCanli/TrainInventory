package com.canli.oya.traininventory.data.repositories;

import android.arch.lifecycle.LiveData;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.utils.AppExecutors;

import java.util.List;

public class CategoryRepository {

    private static CategoryRepository sInstance;
    private final TrainDatabase mDatabase;
    private final LiveData<List<String>> categoryList;

    private CategoryRepository(TrainDatabase database) {
        mDatabase = database;
        categoryList = loadCategories();
    }

    public static CategoryRepository getInstance(TrainDatabase database){
        if (sInstance == null) {
            synchronized (CategoryRepository.class) {
                sInstance = new CategoryRepository(database);
            }
        }
        return sInstance;
    }

    private LiveData<List<String>> loadCategories(){
        return mDatabase.categoryDao().getAllCategories();
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public void insertCategory(final CategoryEntry category){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.categoryDao().insertCategory(category);
            }
        });
    }

    public void deleteCategory(final CategoryEntry category){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.categoryDao().deleteCategory(category);
            }
        });
    }

    public boolean isThisCategoryUsed(String category){
        return mDatabase.trainDao().isThisCategoryUsed(category);
    }
}
