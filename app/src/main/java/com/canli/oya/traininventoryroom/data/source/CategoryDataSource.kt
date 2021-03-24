package com.canli.oya.traininventoryroom.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val CATEGORIES_PAGE_SIZE = 15

class CategoryDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<CategoryEntry> {

    override fun getAllPagedItems(): Flow<PagingData<CategoryEntry>> {
        val pager = Pager(config = PagingConfig(CATEGORIES_PAGE_SIZE, enablePlaceholders = true)){
            database.categoryDao().observeAllPagedCategories()
        }
        return pager.flow
    }

    override fun getAllItems(): Flow<List<CategoryEntry>> {
        return database.categoryDao().observeAllCategories()
    }

    override suspend fun insertItem(item: CategoryEntry) {
        database.categoryDao().insert(item)
    }

    override suspend fun deleteItem(item: CategoryEntry) {
        database.categoryDao().delete(item)
    }

    override fun isThisItemUsed(item: CategoryEntry): Boolean {
        return database.trainDao().isThisCategoryUsed(item.categoryName)
    }

    override suspend fun updateItem(item: CategoryEntry) {
        database.categoryDao().update(item)
    }
}