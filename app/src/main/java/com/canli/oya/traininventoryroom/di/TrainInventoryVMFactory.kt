package com.canli.oya.traininventoryroom.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.BrandViewModel
import com.canli.oya.traininventoryroom.ui.categories.CategoryViewModel
import com.canli.oya.traininventoryroom.ui.trains.TrainViewModel
import javax.inject.Inject

class TrainInventoryVMFactory @Inject constructor(private val trainDataSource: ITrainDataSource,
                                                  private val brandDataSource: IBrandCategoryDataSource<BrandEntry>,
                                                  private val categoryDataSource: IBrandCategoryDataSource<CategoryEntry> )
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TrainViewModel::class.java) -> TrainViewModel(trainDataSource) as T
            modelClass.isAssignableFrom(BrandViewModel::class.java) -> BrandViewModel(brandDataSource) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(categoryDataSource) as T
            else -> throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}