package com.canli.oya.traininventoryroom.viewmodel

import android.app.Application
import android.content.Context
import androidx.databinding.adapters.AdapterViewBindingAdapter
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
import com.canli.oya.traininventoryroom.utils.provideBrandRepo
import com.canli.oya.traininventoryroom.utils.provideCategoryRepo
import com.canli.oya.traininventoryroom.utils.provideTrainRepo
import kotlinx.coroutines.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mTrainRepo: TrainRepository
    private val mBrandRepo: BrandRepository
    private val mCategoryRepo: CategoryRepository

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val newTrainWithNulls: TrainEntry by lazy {
        TrainEntry(trainName = null, modelReference = null,
                brandName = brandList?.value?.get(0)?.brandName, categoryName = categoryList?.value?.get(0),
                imageUri = null, description = null, location = null, scale = null)
    }

    init {
        val context: Context = application.applicationContext
        mTrainRepo = provideTrainRepo(context)
        mBrandRepo = provideBrandRepo(context)
        mCategoryRepo = provideCategoryRepo(context)
    }

    /////////// TRAIN LIST /////////////

    var trainList: LiveData<List<TrainEntry>>? = null
        private set

    fun loadTrainList(trainRepo: TrainRepository) {
        if (trainList == null) {
            trainList = trainRepo.trainList
        }
    }

    lateinit var chosenTrain: TrainEntry
    lateinit var trainBeingModified: TrainEntry

    var isEdit: Boolean = false
        set(value) {
            field = value
            trainBeingModified = if (value) chosenTrain else newTrainWithNulls
        }

    fun insertTrain(train: TrainEntry) {
        viewModelScope.launch { mTrainRepo.insertTrain(train) }
    }

    fun updateTrain(train: TrainEntry) {
        viewModelScope.launch { mTrainRepo.updateTrain(train) }
    }

    fun deleteTrain(train: TrainEntry) {
        viewModelScope.launch { mTrainRepo.deleteTrain(train) }
    }

    var spinnerListener = AdapterViewBindingAdapter.OnItemSelected { spinner, _, position, _ ->
        //the listener is attached to both spinners.
        //when statement differentiate which spinners is selected
        when (spinner.id) {
            R.id.brandSpinner -> trainBeingModified.brandName = brandList?.value?.get(position)?.brandName
            R.id.categorySpinner -> trainBeingModified.categoryName = categoryList?.value?.get(position)
        }
    }

    ////////////// BRAND LIST //////////////////

    var brandList: LiveData<List<BrandEntry>>?  = null
        get() {
            return field ?: mBrandRepo.getBrandList().also { field = it }
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

    var categoryList: LiveData<List<String>>?  = null
        get() {
            return field ?: mCategoryRepo.getCategoryList().also { field = it }
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

