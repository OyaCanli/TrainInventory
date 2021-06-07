package com.canli.oya.traininventoryroom.data.source


import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canlioya.core.data.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CategoryDataSource @Inject constructor(private val database: TrainDatabase) :
    IBrandCategoryDataSource<CategoryEntity> {

    override fun getAllItems(): Flow<List<CategoryEntity>> {
        return database.categoryDao().observeAllCategories()
    }

    override suspend fun insertItem(item: CategoryEntity) {
        database.categoryDao().insert(item)
    }

    override suspend fun deleteItem(item: CategoryEntity) {
        database.categoryDao().delete(item)
    }

    override suspend fun isThisItemUsed(item: CategoryEntity): Boolean {
        return database.trainDao().isThisCategoryUsed(item.categoryName) != null
    }

    override suspend fun updateItem(item: CategoryEntity) {
        database.categoryDao().update(item)
    }

    override suspend fun getItemNames(): List<String> {
        return database.categoryDao().getCategoryNames()
    }

    override suspend fun isThisItemUsedInTrash(item: CategoryEntity): Boolean {
        return database.trainDao().isThisCategoryUsedInTrash(item.categoryName) != null
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: CategoryEntity) {
        database.trainDao().deleteTrainsInTrashWithThisCategory(item.categoryName)
    }
}