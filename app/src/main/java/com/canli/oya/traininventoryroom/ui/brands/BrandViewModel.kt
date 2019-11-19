package com.canli.oya.traininventoryroom.ui.brands

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState
import com.canli.oya.traininventoryroom.data.BrandDataSource
import com.canli.oya.traininventoryroom.data.BrandEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class BrandViewModel(private val dataSource : BrandDataSource, resources: Resources) : ViewModel() {

    var brandListUiState : UIState = UIState(resources.getString(R.string.no_brands_found))

    var brandList = dataSource.getAllBrands()

    private var _isChildFragVisible = MutableLiveData<Boolean>()

    init {
        _isChildFragVisible.value = false
    }

    var isChildFragVisible : LiveData<Boolean> = _isChildFragVisible

    fun setIsChildFragVisible(isVisible : Boolean) {
        _isChildFragVisible.value = isVisible
        Timber.d("isChildFragVisible is set to $isVisible")
    }

    private val _chosenBrand = MutableLiveData<BrandEntry>()

    val chosenBrand: LiveData<BrandEntry>
        get() = _chosenBrand

    fun setChosenBrand(chosenBrand: BrandEntry) {
        _chosenBrand.value = chosenBrand
    }

    fun insertBrand(brand: BrandEntry) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.insertBrand(brand) }
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        dataSource.deleteBrand(brand)
    }

    fun updateBrand(brand: BrandEntry) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.updateBrand(brand) }
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return dataSource.isThisBrandUsed(brandName)
    }
}