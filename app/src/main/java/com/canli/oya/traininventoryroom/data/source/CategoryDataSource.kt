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
        val factory = database.categoryDao().observeAllCategories()
        return LivePagedListBuilder(factory, CATEGORIES_PAGE_SIZE).build()
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