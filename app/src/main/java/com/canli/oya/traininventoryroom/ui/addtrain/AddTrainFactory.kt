package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.BrandDataSource
import com.canli.oya.traininventoryroom.data.source.CategoryDataSource
import com.canli.oya.traininventoryroom.data.source.TrainDataSource
import javax.inject.Inject


class AddTrainFactory @Inject constructor(private val trainDataSource: TrainDataSource,
                                          private val brandDataSource: BrandDataSource,
                                          private val categoryDataSource: CategoryDataSource,
                                          private val chosenTrain: TrainEntry?)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddTrainViewModel::class.java)) {
            AddTrainViewModel(trainDataSource, brandDataSource, categoryDataSource, chosenTrain) as T
        } else {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}