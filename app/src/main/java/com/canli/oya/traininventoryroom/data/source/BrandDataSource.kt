package com.canli.oya.traininventoryroom.data.source

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.toBrandEntity
import com.canli.oya.traininventoryroom.data.entities.toBrandList
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.models.Brand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class BrandDataSource (private val database: TrainDatabase) :
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