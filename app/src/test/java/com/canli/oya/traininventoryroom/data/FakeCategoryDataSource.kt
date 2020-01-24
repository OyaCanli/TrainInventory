package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource

class FakeCategoryDataSource(private val categories : MutableList<CategoryEntry> = mutableListOf(),
                             private val trains: List<TrainEntry> = listOf()) : IBrandCategoryDataSource<CategoryEntry> {

    private val categoriesLiveData : MutableLiveData<PagedList<CategoryEntry>> = MutableLiveData()

    init {
        updateCategoriesLiveData()
    }

    override suspend fun insertItem(item: CategoryEntry) {
        categories.add(item)
        updateCategoriesLiveData()
    }

    override suspend fun deleteItem(item: CategoryEntry) {
        categories.remove(item)
        updateCategoriesLiveData()
    }

    override suspend fun updateItem(item: CategoryEntry) {
        val index = categories.indexOfFirst { it.categoryId == item.categoryId }
        categories[index] = item
        updateCategoriesLiveData()
    }

    override fun getAllItems(): LiveData<PagedList<CategoryEntry>> = categoriesLiveData

    private fun updateCategoriesLiveData(){
        categoriesLiveData.value = categories.asPagedList()
    }

    override fun isThisItemUsed(item: CategoryEntry): Boolean {
        val index = trains.indexOfFirst { it.categoryName == item.categoryName }
        return (index != -1)
    }
}