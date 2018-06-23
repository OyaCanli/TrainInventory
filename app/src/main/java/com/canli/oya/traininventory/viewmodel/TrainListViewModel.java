package com.canli.oya.traininventory.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;

import java.util.List;

public class TrainListViewModel extends AndroidViewModel {

    private LiveData<List<TrainEntry>> trainList;

    public TrainListViewModel(@NonNull Application application) {
        super(application);
        TrainDatabase mDb = TrainDatabase.getInstance(this.getApplication());
        trainList = mDb.trainDao().getAllTrains();
    }

    public LiveData<List<TrainEntry>> getTrains() {
        return trainList;
    }
}
