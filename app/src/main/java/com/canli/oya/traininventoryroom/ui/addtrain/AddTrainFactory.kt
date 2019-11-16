package com.canli.oya.traininventoryroom.ui.addtrain

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.TrainEntry


class AddTrainFactory(private val context : Application,
                      private val chosenTrain: TrainEntry?)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddTrainViewModel(context, chosenTrain) as T
    }
}