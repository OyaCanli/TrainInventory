package com.canlioya.local.source

import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.models.Brand
import com.canlioya.local.TrainDatabase
import com.canlioya.local.entities.toBrandEntity
import com.canlioya.local.entities.toBrandList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BrandDataSource(private val database: TrainDatabase) :
    IBrandCategoryDataSource<Brand> {

    override fun getAllItems(): Flow<List<Brand>> {
        return database.brandDao().observeAllBrands().map { it.toBrandList() }
    }

    override suspend fun insertItem(item: Brand) {
        database.brandDao().insert(item.toBrandEntity())
    }

    override suspend fun updateItem(item: Brand) {
        database.brandDao().update(item.toBrandEntity())
    }

    override suspend fun deleteItem(item: Brand) {
        database.brandDao().delete(item.toBrandEntity())
    }

    override suspend fun isThisItemUsed(item: Brand): Boolean {
        return database.trainDao().isThisBrandUsed(item.brandName) != null
    }

    override suspend fun getItemNames(): List<String> {
        return database.brandDao().getBrandNames()
    }

    override suspend fun isThisItemUsedInTrash(item: Brand): Boolean {
        return database.trainDao().isThisBrandUsedInTrash(item.brandName) != null
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: Brand) {
        database.trainDao().deleteTrainsInTrashWithThisBrand(item.brandName)
    }
}
