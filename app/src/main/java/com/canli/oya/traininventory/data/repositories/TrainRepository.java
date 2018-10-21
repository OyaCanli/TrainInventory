package com.canli.oya.traininventory.data.repositories;

import android.arch.lifecycle.LiveData;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.utils.AppExecutors;

import java.util.List;

public class TrainRepository {

    private static TrainRepository sInstance;
    private final TrainDatabase mDatabase;
    private final LiveData<List<TrainEntry>> trainList;
    private final AppExecutors mExecutors;

    private TrainRepository(TrainDatabase database, AppExecutors executors) {
        mDatabase = database;
        mExecutors = executors;
        trainList = loadTrains();
    }

    public static TrainRepository getInstance(TrainDatabase database, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (TrainRepository.class) {
                sInstance = new TrainRepository(database, executors);
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

    public void insertTrain(final TrainEntry train){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.trainDao().insertTrain(train);
            }
        });
    }

    public void updateTrain(final TrainEntry train){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.trainDao().updateTrainInfo(train);
            }
        });
    }

    public void deleteTrain(final TrainEntry train){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.trainDao().deleteTrain(train);
            }
        });
    }
}
