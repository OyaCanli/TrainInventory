package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry

class FakeCategoryDataSource(private var categories : MutableList<CategoryEntry> = mutableListOf(),
                                                 private val trains: List<TrainEntry> = listOf()) : ICategoryDataSource {

    private val categoriesLiveData : MutableLiveData<PagedList<CategoryEntry>> = MutableLiveData()

    init {
        updateCategoriesLiveData()
    }

    override suspend fun insertCategory(category: CategoryEntry) {
        categories.add(category)
        updateCategoriesLiveData()
    }

    override suspend fun deleteCategory(category: CategoryEntry) {
        categories.remove(category)
        updateCategoriesLiveData()
    }

    override fun isThisCategoryUsed(category: String): Boolean {
        val index = trains.indexOfFirst { it.categoryName == category }
        return (index != -1)
    }

    override suspend fun updateCategory(category: CategoryEntry) {
        val index = categories.indexOfFirst { it.categoryId == category.categoryId }
        categories[index] = category
        updateCategoriesLiveData()
    }

    override fun getAllCategories(): LiveData<PagedList<CategoryEntry>> = categoriesLiveData

    private fun updateCategoriesLiveData(){
        categoriesLiveData.value = categories.asPagedList()
    }

    fun setData(newCategoryList : MutableList<CategoryEntry>){
        categories = newCategoryList
        updateCategoriesLiveData()
    }
}