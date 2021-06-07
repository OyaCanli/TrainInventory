package com.canli.oya.traininventoryroom.data.source

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class BrandDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<BrandEntity> {

    override fun getAllItems(): Flow<List<BrandEntity>> {
        return database.brandDao().observeAllBrands()
    }

    override suspend fun insertItem(item: BrandEntity) {
        database.brandDao().insert(item)
    }

    override suspend fun updateItem(item: BrandEntity) {
        database.brandDao().update(item)
    }

    override suspend fun deleteItem(item: BrandEntity) {
        database.brandDao().delete(item)
    }

    override suspend fun isThisItemUsed(item: BrandEntity): Int? {
        return database.trainDao().isThisBrandUsed(item.brandName)
    }

    override suspend fun getItemNames(): List<String> {
        return database.brandDao().getBrandNames()
    }

    override suspend fun isThisItemUsedInTrash(item: BrandEntity): Int? {
        return database.trainDao().isThisBrandUsedInTrash(item.brandName)
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: BrandEntity) {
        database.trainDao().deleteTrainsInTrashWithThisBrand(item.brandName)
    }
}