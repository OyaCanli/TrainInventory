package com.canli.oya.traininventoryroom.ui.brands

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.source.IBrandDataSource
import com.canli.oya.traininventoryroom.ui.base.BaseListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrandViewModel(private val dataSource : IBrandDataSource,
                     private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : BaseListViewModel<BrandEntry>() {

    override var allItems: LiveData<PagedList<BrandEntry>> = dataSource.getAllBrands()

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