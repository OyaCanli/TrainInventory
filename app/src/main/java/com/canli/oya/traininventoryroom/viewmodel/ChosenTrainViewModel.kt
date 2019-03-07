package com.canli.oya.traininventoryroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry

class ChosenTrainViewModel internal constructor(database: TrainDatabase, trainId: Int) : ViewModel() {
    val chosenTrain: LiveData<TrainEntry> = database.trainDao().getChosenTrain(trainId)
}
