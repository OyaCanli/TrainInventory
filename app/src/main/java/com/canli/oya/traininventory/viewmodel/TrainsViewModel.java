package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.data.repositories.TrainRepository;

import java.util.List;

public class TrainsViewModel extends ViewModel {

    private final LiveData<List<TrainEntry>> trainList;

    TrainsViewModel(TrainRepository trainRepo) {
        trainList = trainRepo.getTrainList();
    }

    public LiveData<List<TrainEntry>> getTrainList() {
        return trainList;
    }
}
