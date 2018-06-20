package com.canli.oya.traininventory.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.TrainEntry;


public class ChosenTrainViewModel extends ViewModel{

    private LiveData<TrainEntry> chosenTrain;

    ChosenTrainViewModel(TrainDatabase database, int trainId){
        chosenTrain = database.trainDao().getChosenTrain(trainId);
    }

    public LiveData<TrainEntry> getChosenTrain() {
        return chosenTrain;
    }
}
