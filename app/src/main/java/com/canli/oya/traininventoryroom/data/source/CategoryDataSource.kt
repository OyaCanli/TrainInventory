package com.canli.oya.traininventoryroom.data.source


import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CategoryDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<CategoryEntity> {

    override fun getAllItems(): Flow<List<CategoryEntity>> {
        return database.categoryDao().observeAllCategories()
    }

    override suspend fun insertItem(item: CategoryEntity) {
        database.categoryDao().insert(item)
    }

    override suspend fun deleteItem(item: CategoryEntity) {
        database.categoryDao().delete(item)
    }

    override suspend fun isThisItemUsed(item: CategoryEntity): Int? {
        return database.trainDao().isThisCategoryUsed(item.categoryName)
    }

    override suspend fun updateItem(item: CategoryEntity) {
        database.categoryDao().update(item)
    }

    override suspend fun getItemNames(): List<String> {
        return database.categoryDao().getCategoryNames()
    }

    override suspend fun isThisItemUsedInTrash(item: CategoryEntity): Int? {
        return database.trainDao().isThisCategoryUsedInTrash(item.categoryName)
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: CategoryEntity) {
        database.trainDao().deleteTrainsInTrashWithThisCategory(item.categoryName)
    }
}