package com.canli.oya.traininventoryroom.ui.filter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.TrainMinimal
import kotlinx.coroutines.CoroutineDispatcher


import kotlinx.coroutines.Dispatchers


class FilterTrainViewModel @ViewModelInject constructor(val trainInteractors: TrainInteractors,
                            val brandInteractors: BrandCategoryInteractors<Brand>,
                            val categoryInteractors : BrandCategoryInteractors<Category>,
                            private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var selectedBrand : MutableLiveData<String?> = MutableLiveData(null)

    var selectedCategory : MutableLiveData<String?> = MutableLiveData(null)

    var keyword : String? = null

    suspend fun getBrandNames() = brandInteractors.getItemNames()

    suspend fun getCategoryNames() = categoryInteractors.getItemNames()

    suspend fun filterTrains() : ArrayList<TrainMinimal> {
        val filteredList = ArrayList<TrainMinimal>()
        filteredList.addAll(trainInteractors.searchInTrains(keyword, selectedCategory.value, selectedBrand.value))
        return filteredList
    }
}