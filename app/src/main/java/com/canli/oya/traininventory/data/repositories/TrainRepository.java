package com.canli.oya.traininventory.data.repositories;

import android.arch.lifecycle.LiveData;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;

import java.util.List;

public class TrainRepository {

    private static TrainRepository sInstance;
    private final TrainDatabase mDatabase;
    private final LiveData<List<TrainEntry>> trainList;

    private TrainRepository(TrainDatabase database) {
        mDatabase = database;
        trainList = loadTrains();
    }

    public static TrainRepository getInstance(TrainDatabase database) {
        if (sInstance == null) {
            synchronized (TrainRepository.class) {
                sInstance = new TrainRepository(database);
            }
        }
        return sInstance;
    }

    private LiveData<List<TrainEntry>> loadTrains(){
        return mDatabase.trainDao().getAllTrains();
    }

    public LiveData<List<TrainEntry>> getTrainList() {
        return trainList;
    }
}
