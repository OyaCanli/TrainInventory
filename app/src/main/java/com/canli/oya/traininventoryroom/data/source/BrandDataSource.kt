package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import javax.inject.Inject

const val BRANDS_PAGE_SIZE = 15

class BrandDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<BrandEntry> {

    override fun getAllItems() : LiveData<PagedList<BrandEntry>> {
        val factory = database.brandDao().allBrands
        return LivePagedListBuilder(factory, BRANDS_PAGE_SIZE).build()
    }

    override suspend fun insertItem(brand: BrandEntry) {
        database.brandDao().insert(brand)
    }

    override suspend fun updateItem(brand: BrandEntry) {
        database.brandDao().update(brand)
    }

    override suspend fun deleteItem(brand: BrandEntry) {
        database.brandDao().delete(brand)
    }

    override fun isThisItemUsed(brandName: String): Boolean {
        return database.trainDao().isThisBrandUsed(brandName)
    }
}