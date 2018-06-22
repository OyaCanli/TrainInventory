package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.TrainEntry;

import java.util.List;


public class ChosenTrainViewModel extends ViewModel{

    private LiveData<TrainEntry> chosenTrain;
    private LiveData<List<String>> categoryList;
    private LiveData<List<BrandEntry>> brandList;

    ChosenTrainViewModel(TrainDatabase database, int trainId){
        if(categoryList == null){
            categoryList = database.categoryDao().getAllCategoryNames();
            Log.d("ChosenTrainViewModel", "categoryList is retrieved");
        }
        if(brandList == null){
            brandList = database.brandDao().getAllBrands();
            Log.d("ChosenTrainViewModel", "brandList is retrieved");
        }
        if(trainId != 0){
            chosenTrain = database.trainDao().getChosenTrain(trainId);
        }
        Log.d("ChosenTrainViewModel", "new viewmodel created");
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<BrandEntry>> getBrandList() {
        return brandList;
    }

    public LiveData<TrainEntry> getChosenTrain() {
        return chosenTrain;
    }
}
