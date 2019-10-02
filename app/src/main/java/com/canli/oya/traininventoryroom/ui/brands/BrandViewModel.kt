package com.canli.oya.traininventoryroom.ui.brands

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandDataSource
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.utils.UIState
import com.canli.oya.traininventoryroom.utils.provideBrandDataSource
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrandViewModel(application: Application) : AndroidViewModel(application) {

    val context: Context = application.applicationContext

    private val dataSource : BrandDataSource = provideBrandDataSource(context)

    var brandListUiState : UIState = UIState(context.resources.getString(R.string.no_brands_found))

    var brandList: Flowable<List<BrandEntry>> = dataSource.getAllBrands()

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