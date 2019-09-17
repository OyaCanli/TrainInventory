package com.canli.oya.traininventoryroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository

class AddTrainFactory(private val trainRepo: TrainRepository, private val chosenTrain: TrainEntry?) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddTrainViewModel(trainRepo, chosenTrain) as T
    }
}