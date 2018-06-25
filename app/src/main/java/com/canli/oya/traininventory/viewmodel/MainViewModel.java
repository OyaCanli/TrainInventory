package com.canli.oya.traininventory.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.data.entities.TrainEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final LiveData<List<TrainEntry>> trainList;
    private final LiveData<List<CategoryEntry>> categoryList;
    private final LiveData<List<String>> categoryNames;
    private final LiveData<List<BrandEntry>> brandList;
    private final MutableLiveData<TrainEntry> mChosenTrain = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        TrainDatabase mDb = TrainDatabase.getInstance(this.getApplication());
        trainList = mDb.trainDao().getAllTrains();
        categoryList = mDb.categoryDao().getAllCategories();
        brandList = mDb.brandDao().getAllBrands();
        categoryNames = mDb.categoryDao().getAllCategoryNames();
        Log.d("ChosenTrainViewModel", "constructor of viewmodel");
    }

    public LiveData<List<TrainEntry>> getTrains() {
        return trainList;
    }

    public LiveData<List<CategoryEntry>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<BrandEntry>> getBrandList() {
        return brandList;
    }

    public LiveData<List<String>> getCategoryNames() {
        return categoryNames;
    }

    public LiveData<TrainEntry> getChosenTrain() {
        return mChosenTrain;
    }

    public void setChosenTrain(TrainEntry chosenTrain) {
        mChosenTrain.setValue(chosenTrain);
    }
}
