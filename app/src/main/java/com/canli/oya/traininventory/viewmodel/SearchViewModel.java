package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private final TrainDatabase mDb;

    SearchViewModel(final TrainDatabase database) {
        mDb = database;
    }

    public LiveData<List<TrainEntry>> getTrainsFromThisBrand(String brandName){
        return mDb.trainDao().getTrainsFromThisBrand(brandName);
    }

    public LiveData<List<TrainEntry>> getTrainsFromThisCategory(String category){
        return mDb.trainDao().getTrainsFromThisCategory(category);
    }
}
