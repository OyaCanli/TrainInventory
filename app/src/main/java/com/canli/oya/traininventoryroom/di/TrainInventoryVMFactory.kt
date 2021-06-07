package com.canli.oya.traininventoryroom.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canli.oya.traininventoryroom.ui.brands.BrandViewModel
import com.canli.oya.traininventoryroom.ui.categories.CategoryViewModel
import com.canli.oya.traininventoryroom.ui.filter.FilterTrainViewModel
import com.canli.oya.traininventoryroom.ui.trains.TrainViewModel
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import javax.inject.Inject

class TrainInventoryVMFactory @Inject constructor(private val trainInteractors: TrainInteractors,
                                                  private val brandInteractor: BrandCategoryInteractors<Brand>,
                                                  private val categoryInteractor: BrandCategoryInteractors<Category>
)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TrainViewModel::class.java) -> TrainViewModel(trainInteractors) as T
            modelClass.isAssignableFrom(BrandViewModel::class.java) -> BrandViewModel(brandInteractor) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(categoryInteractor) as T
            modelClass.isAssignableFrom(FilterTrainViewModel::class.java) -> FilterTrainViewModel(trainInteractors, brandInteractor, categoryInteractor) as T
            else -> throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}