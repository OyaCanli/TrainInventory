package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

const val BRANDS_PAGE_SIZE = 15

class BrandDataSource(private val database: TrainDatabase) {

    fun getAllBrands() : LiveData<PagedList<BrandEntry>> {
        val factory = database.brandDao().allBrands
        return LivePagedListBuilder(factory, BRANDS_PAGE_SIZE).build()
    }

    suspend fun insertBrand(brand: BrandEntry) {
        database.brandDao().insert(brand)
    }

    suspend fun updateBrand(brand: BrandEntry) {
        database.brandDao().update(brand)
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        database.brandDao().delete(brand)
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return database.trainDao().isThisBrandUsed(brandName)
    }
}