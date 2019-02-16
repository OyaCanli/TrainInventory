package com.canli.oya.traininventoryroom.data.repositories

import androidx.lifecycle.LiveData
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase

class BrandRepository private constructor(private val mDatabase: TrainDatabase) {
    val brandList: LiveData<List<BrandEntry>>

    init {
        brandList = loadBrands()
    }

    private fun loadBrands(): LiveData<List<BrandEntry>> {
        return mDatabase.brandDao().allBrands
    }

    suspend fun insertBrand(brand: BrandEntry) {
        mDatabase.brandDao().insertBrand(brand)
    }

    suspend fun updateBrand(brand: BrandEntry) {
        mDatabase.brandDao().updateBrandInfo(brand)
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        mDatabase.brandDao().deleteBrand(brand)
    }

    suspend fun isThisBrandUsed(brandName: String): Boolean {
        return mDatabase.trainDao().isThisBrandUsed(brandName)
    }

    companion object {

        private var sInstance: BrandRepository? = null

        fun getInstance(database: TrainDatabase): BrandRepository {
            return sInstance ?: synchronized(BrandRepository::class.java) {
                    sInstance ?: BrandRepository(database)
                            .also { sInstance = it }
            }

        }
    }
}
