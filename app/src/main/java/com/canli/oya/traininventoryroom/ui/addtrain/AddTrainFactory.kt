package com.canli.oya.traininventoryroom.ui.addtrain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.BrandDataSource
import com.canli.oya.traininventoryroom.data.CategoryDataSource
import com.canli.oya.traininventoryroom.data.TrainDataSource
import com.canli.oya.traininventoryroom.data.TrainEntry


class AddTrainFactory(private val trainDataSource: TrainDataSource,
                      private val categoryDataSource : CategoryDataSource,
                      private val brandDataSource: BrandDataSource,
                      private val chosenTrain: TrainEntry?)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddTrainViewModel(trainDataSource, categoryDataSource, brandDataSource, chosenTrain) as T
    }
}