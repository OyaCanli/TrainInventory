package com.canli.oya.traininventoryroom.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val BRANDS_PAGE_SIZE = 15

class BrandDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<BrandEntry> {

    override fun getAllPagedItems(): Flow<PagingData<BrandEntry>> {
        val pager = Pager(config = PagingConfig(BRANDS_PAGE_SIZE, enablePlaceholders = true)) {
            database.brandDao().observeAllPagedBrands()
        }
        return pager.flow
    }

    override fun getAllItems(): Flow<List<BrandEntry>> {
        return database.brandDao().observeAllBrands()
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

    override suspend fun isThisItemUsed(item: BrandEntry): Boolean {
        return database.trainDao().isThisBrandUsed(item.brandName)
    }

    override suspend fun getItemNames(): List<String> {
        return database.brandDao().getBrandNames()
    }
}