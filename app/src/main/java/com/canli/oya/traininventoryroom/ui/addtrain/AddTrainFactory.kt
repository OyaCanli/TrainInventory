package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train

import javax.inject.Inject


class AddTrainFactory @Inject constructor(private val trainInteractors: TrainInteractors,
                                          private val brandInteractors: BrandCategoryInteractors<Brand>,
                                          private val categoryInteractors: BrandCategoryInteractors<Category>,
                                          private val chosenTrain: Train?)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddTrainViewModel::class.java)) {
            AddTrainViewModel(trainInteractors, brandInteractors, categoryInteractors, chosenTrain) as T
        } else {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}