package com.canli.oya.traininventoryroom.ui.brands

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.source.IBrandDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrandViewModel(private val dataSource : IBrandDataSource,
                     private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var brandListUiState : UIState = UIState(message = R.string.no_brands_found)

    var brandList = dataSource.getAllBrands()

    var isChildFragVisible : Boolean = false

    private val _chosenBrand = MutableLiveData<BrandEntry>()

    val chosenBrand: LiveData<BrandEntry>
        get() = _chosenBrand

    fun setChosenBrand(chosenBrand: BrandEntry) {
        _chosenBrand.value = chosenBrand
    }

    fun insertBrand(brand: BrandEntry) {
        viewModelScope.launch(ioDispatcher) { dataSource.insertBrand(brand) }
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        dataSource.deleteBrand(brand)
    }

    fun updateBrand(brand: BrandEntry) {
        viewModelScope.launch(ioDispatcher) { dataSource.updateBrand(brand) }
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return dataSource.isThisBrandUsed(brandName)
    }
}