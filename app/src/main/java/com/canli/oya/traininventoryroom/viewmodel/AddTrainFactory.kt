package com.canli.oya.traininventoryroom.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddTrainFactory(private val mApplication: Application, private val mTrainId: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddTrainViewModel(mApplication, mTrainId) as T
    }
}