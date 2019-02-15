package com.canli.oya.traininventoryroom.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry

class ChosenTrainViewModel internal constructor(database: TrainDatabase, trainId: Int) : ViewModel() {
    val chosenTrain: LiveData<TrainEntry>

    init {
        chosenTrain = database.trainDao().getChosenTrain(trainId)
    }

}
