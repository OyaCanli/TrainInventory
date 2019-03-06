package com.canli.oya.traininventoryroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository

class AddTrainFactory(private val mRepo: TrainRepository, private val mTrainId: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddTrainViewModel(mRepo, mTrainId) as T
    }
}