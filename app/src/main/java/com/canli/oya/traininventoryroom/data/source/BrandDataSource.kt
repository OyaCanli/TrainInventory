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
        val factory = database.brandDao().observeAllBrands()
        return LivePagedListBuilder(factory, BRANDS_PAGE_SIZE).build()
    }

    override suspend fun insertItem(item: BrandEntry) {
        database.brandDao().insert(item)
    }

    override suspend fun updateItem(item: BrandEntry) {
        database.brandDao().update(item)
    }

    override suspend fun deleteItem(item: BrandEntry) {
        database.brandDao().delete(item)
    }

    override fun isThisItemUsed(item: BrandEntry): Boolean {
        return database.trainDao().isThisBrandUsed(item.brandName)
    }
}