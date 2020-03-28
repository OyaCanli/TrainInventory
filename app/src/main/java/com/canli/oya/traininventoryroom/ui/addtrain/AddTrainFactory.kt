package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import javax.inject.Inject


class AddTrainFactory @Inject constructor(private val trainDataSource: ITrainDataSource,
                                          private val brandDataSource: IBrandCategoryDataSource<BrandEntry>,
                                          private val categoryDataSource: IBrandCategoryDataSource<CategoryEntry>,
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