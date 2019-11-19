package com.canli.oya.traininventoryroom.data.datasource

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import javax.inject.Inject

const val CATEGORIES_PAGE_SIZE = 15

class CategoryDataSource @Inject constructor(private val database: TrainDatabase) : ICategoryDataSource {

    override fun getAllCategories(): LiveData<PagedList<CategoryEntry>> {
        val factory = database.categoryDao().allCategories
        return LivePagedListBuilder(factory, CATEGORIES_PAGE_SIZE).build()
    }

    override suspend fun insertCategory(category: CategoryEntry) {
        database.categoryDao().insert(category)
    }

    override suspend fun deleteCategory(category: CategoryEntry) {
        database.categoryDao().delete(category)
    }

    override fun isThisCategoryUsed(category: String): Boolean {
        return database.trainDao().isThisCategoryUsed(category)
    }

    override suspend fun updateCategory(category: CategoryEntry) {
        database.categoryDao().update(category)
    }
}