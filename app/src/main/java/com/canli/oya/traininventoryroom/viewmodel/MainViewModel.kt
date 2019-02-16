package com.canli.oya.traininventoryroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.BrandRepository
import com.canli.oya.traininventoryroom.data.repositories.CategoryRepository
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {

    var trainList: LiveData<List<TrainEntry>>? = null
        private set
    var brandList: LiveData<List<BrandEntry>>? = null
        private set
    var categoryList: LiveData<List<String>>? = null
        private set

    private lateinit var mTrainRepo: TrainRepository
    private lateinit var mBrandRepo: BrandRepository
    private lateinit var mCategoryRepo: CategoryRepository

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val mChosenBrand = MutableLiveData<BrandEntry>()

    val chosenBrand: LiveData<BrandEntry>
        get() = mChosenBrand

    /////////// TRAIN LIST /////////////
    fun loadTrainList(trainRepo: TrainRepository) {
        if (trainList == null) {
            trainList = trainRepo.trainList
        }
        mTrainRepo = trainRepo
    }

    fun insertTrain(train: TrainEntry) {
        viewModelScope.launch {mTrainRepo.insertTrain(train)}
    }

    fun updateTrain(train: TrainEntry) {
        viewModelScope.launch {mTrainRepo.updateTrain(train)}
    }

    fun deleteTrain(train: TrainEntry) {
        viewModelScope.launch {mTrainRepo.deleteTrain(train)}
    }

    ////////////// BRAND LIST //////////////////

    fun loadBrandList(brandRepo: BrandRepository) {
        if (brandList == null) {
            brandList = brandRepo.brandList
        }
        mBrandRepo = brandRepo
    }

    fun setChosenBrand(chosenBrand: BrandEntry) {
        mChosenBrand.value = chosenBrand
    }

    fun insertBrand(brand: BrandEntry) {
        viewModelScope.launch {mBrandRepo.insertBrand(brand)}
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        mBrandRepo.deleteBrand(brand)
    }

    fun updateBrand(brand: BrandEntry) {
        viewModelScope.launch {mBrandRepo.updateBrand(brand)}
    }

    suspend fun isThisBrandUsed(brandName: String): Boolean {
        return mBrandRepo.isThisBrandUsed(brandName)
    }

    //////////////// CATEGORY LIST //////////////////

    fun loadCategoryList(categoryRepo: CategoryRepository) {
        if (categoryList == null) {
            categoryList = categoryRepo.categoryList
        }
        mCategoryRepo = categoryRepo
    }

    fun deleteCategory(category: CategoryEntry) {
        viewModelScope.launch {mCategoryRepo.deleteCategory(category)}
    }

    fun insertCategory(category: CategoryEntry) {
        viewModelScope.launch { mCategoryRepo.insertCategory(category)}
    }

    suspend fun isThisCategoryUsed(category: String): Boolean {
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
        val searchResults = viewModelScope.async {mTrainRepo.searchInTrains(query)  }
        return searchResults.await()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
