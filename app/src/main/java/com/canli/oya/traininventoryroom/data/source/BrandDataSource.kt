package com.canli.oya.traininventoryroom.data.source

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class BrandDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<BrandEntry> {

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

    override suspend fun isThisItemUsed(item: BrandEntry): Int? {
        return database.trainDao().isThisBrandUsed(item.brandName)
    }

    override suspend fun getItemNames(): List<String> {
        return database.brandDao().getBrandNames()
    }

    override suspend fun isThisItemUsedInTrash(item: BrandEntry): Int? {
        return database.trainDao().isThisBrandUsedInTrash(item.brandName)
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: BrandEntry) {
        database.trainDao().deleteTrainsInTrashWithThisBrand(item.brandName)
    }
}