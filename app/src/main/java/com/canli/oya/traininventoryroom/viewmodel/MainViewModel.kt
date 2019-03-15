package com.canli.oya.traininventoryroom.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.BrandRepository
import com.canli.oya.traininventoryroom.data.repositories.CategoryRepository
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import com.canli.oya.traininventoryroom.utils.UIState
import com.canli.oya.traininventoryroom.utils.provideBrandRepo
import com.canli.oya.traininventoryroom.utils.provideCategoryRepo
import com.canli.oya.traininventoryroom.utils.provideTrainRepo
import kotlinx.coroutines.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mTrainRepo: TrainRepository
    private val mBrandRepo: BrandRepository
    private val mCategoryRepo: CategoryRepository

    val context: Context = application.applicationContext

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
        mTrainRepo = provideTrainRepo(context)
        mBrandRepo = provideBrandRepo(context)
        mCategoryRepo = provideCategoryRepo(context)
    }

    /////////// TRAIN LIST /////////////
    var trainListUiState : UIState = UIState(context.resources.getString(R.string.no_trains_found))

    var trainList: LiveData<List<TrainEntry>>? = null
        get() {
            return field ?: mTrainRepo.trainList.also { field = it }
        }
        private set

    fun getChosenTrain(trainId : Int): LiveData<TrainEntry> {
        return mTrainRepo.getChosenTrainLiveData(trainId)
    }

    fun deleteTrain(train: TrainEntry) {
        viewModelScope.launch { mTrainRepo.deleteTrain(train) }
    }

    ////////////// BRAND LIST //////////////////
    var brandListUiState : UIState = UIState(context.resources.getString(R.string.no_brands_found))

    var brandList: LiveData<List<BrandEntry>>?  = null
        get() {
            return field ?: mBrandRepo.getAllBrands().also { field = it }
        }
        private set

    private val mChosenBrand = MutableLiveData<BrandEntry>()

    val chosenBrand: LiveData<BrandEntry>
        get() = mChosenBrand

    fun setChosenBrand(chosenBrand: BrandEntry) {
        mChosenBrand.value = chosenBrand
    }

    fun insertBrand(brand: BrandEntry) {
        viewModelScope.launch { mBrandRepo.insertBrand(brand) }
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        mBrandRepo.deleteBrand(brand)
    }

    fun updateBrand(brand: BrandEntry) {
        viewModelScope.launch { mBrandRepo.updateBrand(brand) }
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return mBrandRepo.isThisBrandUsed(brandName)
    }

    //////////////// CATEGORY LIST //////////////////
    var categoryListUiState = UIState(context.resources.getString(R.string.no_categories_found))

    var categoryList: LiveData<List<String>>?  = null
        get() {
            return field ?: mCategoryRepo.getAllCategories().also { field = it }
        }
        private set

    fun deleteCategory(category: CategoryEntry) {
        viewModelScope.launch { mCategoryRepo.deleteCategory(category) }
    }

    fun insertCategory(category: CategoryEntry) {
        viewModelScope.launch { mCategoryRepo.insertCategory(category) }
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return mCategoryRepo.isThisCategoryUsed(category)
    }

    ///////////// SEARCH //////////////////////////
    fun getTrainsFromThisBrand(brandName: String): LiveData<List<TrainEntry>> {
        return mTrainRepo.getTrainsFromThisBrand(brandName)
    }

    fun getTrainsFromThisCategory(category: String): LiveData<List<TrainEntry>> {
        return mTrainRepo.getTrainsFromThisCategory(category)
    }

    suspend fun searchInTrains(query: String): List<TrainEntry> {
        val searchResults = viewModelScope.async { mTrainRepo.searchInTrains(query) }
        return searchResults.await()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

