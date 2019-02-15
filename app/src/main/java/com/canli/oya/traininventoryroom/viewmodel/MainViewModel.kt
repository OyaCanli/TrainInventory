package com.canli.oya.traininventoryroom.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.BrandRepository
import com.canli.oya.traininventoryroom.data.repositories.CategoryRepository
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository

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
        mTrainRepo.insertTrain(train)
    }

    fun updateTrain(train: TrainEntry) {
        mTrainRepo.updateTrain(train)
    }

    fun deleteTrain(train: TrainEntry) {
        mTrainRepo.deleteTrain(train)
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
        mBrandRepo.insertBrand(brand)
    }

    fun deleteBrand(brand: BrandEntry) {
        mBrandRepo.deleteBrand(brand)
    }

    fun updateBrand(brand: BrandEntry) {
        mBrandRepo.updateBrand(brand)
    }

    fun isThisBrandUsed(brandName: String): Boolean {
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
        mCategoryRepo.deleteCategory(category)
    }

    fun insertCategory(category: CategoryEntry) {
        mCategoryRepo.insertCategory(category)
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

    fun searchInTrains(query: String): List<TrainEntry> {
        return mTrainRepo.searchInTrains(query)
    }
}
