package com.canli.oya.traininventoryroom.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventoryroom.data.TrainDatabase;
import com.canli.oya.traininventoryroom.data.entities.TrainEntry;

public class ChosenTrainViewModel extends ViewModel {
    private final LiveData<TrainEntry> chosenTrain;

    ChosenTrainViewModel(TrainDatabase database, int trainId) {
        chosenTrain = database.trainDao().getChosenTrain(trainId);
    }

    public LiveData<TrainEntry> getChosenTrain() {
        return chosenTrain;
    }

}
