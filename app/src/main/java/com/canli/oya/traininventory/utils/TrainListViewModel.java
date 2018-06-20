package com.canli.oya.traininventory.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.TrainMinimal;

import java.util.List;

public class TrainListViewModel extends AndroidViewModel {

    private LiveData<List<TrainMinimal>> trainList;

    public TrainListViewModel(@NonNull Application application) {
        super(application);
        TrainDatabase mDb = TrainDatabase.getInstance(this.getApplication());
        trainList = mDb.trainDao().getAllTrains();
    }

    public LiveData<List<TrainMinimal>> getTrains() {
        return trainList;
    }
}
