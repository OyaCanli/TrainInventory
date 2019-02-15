package com.canli.oya.traininventoryroom.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import com.canli.oya.traininventoryroom.data.TrainDatabase

class ChosenTrainFactory(private val mDb: TrainDatabase, private val mTrainId: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChosenTrainViewModel(mDb, mTrainId) as T
    }
}