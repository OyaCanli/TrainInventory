package com.canli.oya.traininventory.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.TrainEntry;

import java.util.List;


public class ChosenTrainViewModel extends ViewModel{

    private LiveData<TrainEntry> chosenTrain;
    private LiveData<List<String>> categoryList;
    private LiveData<List<String>> brandList;

    ChosenTrainViewModel(TrainDatabase database, int trainId){
        categoryList = database.categoryDao().getAllCategoryNames();
        brandList = database.brandDao().getAllBrandNames();
        if(trainId != 0){
            chosenTrain = database.trainDao().getChosenTrain(trainId);
        }
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<String>> getBrandList() {
        return brandList;
    }

    public LiveData<TrainEntry> getChosenTrain() {
        return chosenTrain;
    }
}
