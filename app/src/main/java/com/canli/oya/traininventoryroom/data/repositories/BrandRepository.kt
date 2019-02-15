package com.canli.oya.traininventoryroom.data.repositories

import android.arch.lifecycle.LiveData
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.utils.AppExecutors

class BrandRepository private constructor(private val mDatabase: TrainDatabase, private val mExecutors: AppExecutors) {
    val brandList: LiveData<List<BrandEntry>>

    init {
        brandList = loadBrands()
    }

    private fun loadBrands(): LiveData<List<BrandEntry>> {
        return mDatabase.brandDao().allBrands
    }

    fun insertBrand(brand: BrandEntry) {
        mExecutors.diskIO().execute { mDatabase.brandDao().insertBrand(brand) }
    }

    fun updateBrand(brand: BrandEntry) {
        mExecutors.diskIO().execute { mDatabase.brandDao().updateBrandInfo(brand) }
    }

    fun deleteBrand(brand: BrandEntry) {
        mExecutors.diskIO().execute { mDatabase.brandDao().deleteBrand(brand) }
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return mDatabase.trainDao().isThisBrandUsed(brandName)
    }

    companion object {

        private var sInstance: BrandRepository? = null

        fun getInstance(database: TrainDatabase, executors: AppExecutors): BrandRepository {
            return sInstance ?: synchronized(BrandRepository::class.java) {
                    BrandRepository(database, executors)
                            .also { sInstance = it }
            }

        }
    }
}
