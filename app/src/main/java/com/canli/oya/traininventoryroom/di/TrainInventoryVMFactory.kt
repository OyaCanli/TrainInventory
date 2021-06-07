package com.canli.oya.traininventoryroom.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.BrandViewModel
import com.canli.oya.traininventoryroom.ui.categories.CategoryViewModel
import com.canli.oya.traininventoryroom.ui.filter.FilterTrainViewModel
import com.canli.oya.traininventoryroom.ui.trains.TrainViewModel
import javax.inject.Inject

class TrainInventoryVMFactory @Inject constructor(private val trainDataSource: ITrainDataSource,
                                                  private val brandDataSource: IBrandCategoryDataSource<BrandEntity>,
                                                  private val categoryDataSource: IBrandCategoryDataSource<CategoryEntity> )
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TrainViewModel::class.java) -> TrainViewModel(trainDataSource) as T
            modelClass.isAssignableFrom(BrandViewModel::class.java) -> BrandViewModel(brandDataSource) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(categoryDataSource) as T
            modelClass.isAssignableFrom(FilterTrainViewModel::class.java) -> FilterTrainViewModel(trainDataSource, brandDataSource, categoryDataSource) as T
            else -> throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}