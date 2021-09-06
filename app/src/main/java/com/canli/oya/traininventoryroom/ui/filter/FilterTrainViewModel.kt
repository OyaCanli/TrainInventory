package com.canli.oya.traininventoryroom.ui.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.TrainMinimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterTrainViewModel @Inject constructor(
    private val trainInteractors: TrainInteractors,
    private val brandInteractors: BrandCategoryInteractors<Brand>,
    private val categoryInteractors: BrandCategoryInteractors<Category>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _brandNames: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val brandNames: StateFlow<List<String>>
        get() = _brandNames

    private val _categoryNames: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val categoryNames: StateFlow<List<String>>
        get() = _categoryNames

    var selectedBrand: MutableLiveData<String?> = MutableLiveData(null)

    var selectedCategory: MutableLiveData<String?> = MutableLiveData(null)

    var keyword: String? = null

    init {
        viewModelScope.launch(ioDispatcher) {
            _brandNames.value = brandInteractors.getItemNames()
            _categoryNames.value = categoryInteractors.getItemNames()
        }
    }

    suspend fun filterTrains(): ArrayList<TrainMinimal> {
        val filteredList = ArrayList<TrainMinimal>()
        filteredList.addAll(trainInteractors.searchInTrains(keyword, selectedCategory.value, selectedBrand.value))
        return filteredList
    }
}
