package com.canli.oya.traininventoryroom.di

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.data.datasource.BrandDataSource
import com.canli.oya.traininventoryroom.data.datasource.CategoryDataSource
import com.canli.oya.traininventoryroom.data.datasource.TrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.BrandViewModel
import com.canli.oya.traininventoryroom.ui.categories.CategoryViewModel
import com.canli.oya.traininventoryroom.ui.trains.TrainViewModel
import javax.inject.Inject

class TrainInventoryVMFactory @Inject constructor(private val trainDataSource: TrainDataSource,
                                                  private val brandDataSource: BrandDataSource,
                                                  private val categoryDataSource: CategoryDataSource,
                                                  private val resources: Resources)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TrainViewModel::class.java) -> TrainViewModel(trainDataSource, resources) as T
            modelClass.isAssignableFrom(BrandViewModel::class.java) -> BrandViewModel(brandDataSource, resources) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(categoryDataSource, resources) as T
            else -> throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}