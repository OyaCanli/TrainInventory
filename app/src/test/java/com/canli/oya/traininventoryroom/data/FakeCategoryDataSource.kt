package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.datasource.ICategoryDataSource

class FakeCategoryDataSource(private val categories : MutableList<CategoryEntry> = mutableListOf(),
                             private val trains: List<TrainEntry> = listOf()) : ICategoryDataSource {

    override suspend fun insertCategory(category: CategoryEntry) {
        categories.add(category)
    }

    override suspend fun deleteCategory(category: CategoryEntry) {
        categories.remove(category)
    }

    override fun isThisCategoryUsed(category: String): Boolean {
        val index = trains.indexOfFirst { it.categoryName == category }
        return (index != -1)
    }

    override suspend fun updateCategory(category: CategoryEntry) {
        val index = categories.indexOfFirst { it.categoryId == category.categoryId }
        categories[index] = category
    }

    override fun getAllCategories(): LiveData<PagedList<CategoryEntry>> {
        return MutableLiveData() //TODO: not a real implementation
    }
}