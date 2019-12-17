package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import timber.log.Timber
import javax.inject.Inject

const val CATEGORIES_PAGE_SIZE = 15

class CategoryDataSource @Inject constructor(private val database: TrainDatabase) : IBrandCategoryDataSource<CategoryEntry> {

    override fun getAllItems(): LiveData<PagedList<CategoryEntry>> {
        Timber.d("getAllCategories is called")
        val factory = database.categoryDao().allCategories
        return LivePagedListBuilder(factory, CATEGORIES_PAGE_SIZE).build()
    }

    override suspend fun insertItem(category: CategoryEntry) {
        database.categoryDao().insert(category)
    }

    override suspend fun deleteItem(category: CategoryEntry) {
        database.categoryDao().delete(category)
    }

    override fun isThisItemUsed(category: String): Boolean {
        return database.trainDao().isThisCategoryUsed(category)
    }

    override suspend fun updateItem(category: CategoryEntry) {
        database.categoryDao().update(category)
    }
}