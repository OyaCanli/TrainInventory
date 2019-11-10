package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

const val CATEGORIES_PAGE_SIZE = 15

class CategoryDataSource(private val database: TrainDatabase) {

    fun getAllCategories(): LiveData<PagedList<CategoryEntry>> {
        val factory = database.categoryDao().allCategories
        return LivePagedListBuilder(factory, CATEGORIES_PAGE_SIZE).build()
    }

    suspend fun insertCategory(category: CategoryEntry) {
        database.categoryDao().insert(category)
    }

    suspend fun deleteCategory(category: CategoryEntry) {
        database.categoryDao().delete(category)
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return database.trainDao().isThisCategoryUsed(category)
    }

    suspend fun updateCategory(category: CategoryEntry) {
        database.categoryDao().update(category)
    }
}