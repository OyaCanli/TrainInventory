package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.data.entities.TrainEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import javax.inject.Inject


class AddTrainFactory @Inject constructor(private val trainDataSource: ITrainDataSource,
                                          private val brandDataSource: IBrandCategoryDataSource<BrandEntity>,
                                          private val categoryDataSource: IBrandCategoryDataSource<CategoryEntity>,
                                          private val chosenTrain: TrainEntity?)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddTrainViewModel::class.java)) {
            AddTrainViewModel(trainDataSource, brandDataSource, categoryDataSource, chosenTrain) as T
        } else {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}