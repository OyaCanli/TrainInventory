package com.canli.oya.traininventoryroom.data.source


import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.toCategoryEntity
import com.canli.oya.traininventoryroom.data.entities.toCategoryList
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CategoryDataSource(private val database: TrainDatabase) :
    IBrandCategoryDataSource<Category> {

    override fun getAllItems(): Flow<List<Category>> {
        return database.categoryDao().observeAllCategories().map { it.toCategoryList() }
    }

    override suspend fun insertItem(item: Category) {
        database.categoryDao().insert(item.toCategoryEntity())
    }

    override suspend fun deleteItem(item: Category) {
        database.categoryDao().delete(item.toCategoryEntity())
    }

    override suspend fun isThisItemUsed(item: Category): Boolean {
        return database.trainDao().isThisCategoryUsed(item.categoryName) != null
    }

    override suspend fun updateItem(item: Category) {
        database.categoryDao().update(item.toCategoryEntity())
    }

    override suspend fun getItemNames(): List<String> {
        return database.categoryDao().getCategoryNames()
    }

    override suspend fun isThisItemUsedInTrash(item: Category): Boolean {
        return database.trainDao().isThisCategoryUsedInTrash(item.categoryName) != null
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: Category) {
        database.trainDao().deleteTrainsInTrashWithThisCategory(item.categoryName)
    }
}