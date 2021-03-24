package com.canli.oya.traininventoryroom.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.asPagedList

class FakeCategoryDataSource(private var categories : MutableList<CategoryEntry> = sampleCategoryList,
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

    override fun getAllPagedItems(): Flow<PagingData<T>> = categoriesLiveData

    private fun updateCategoriesLiveData(){
        categoriesLiveData.value = categories.asPagedList()
    }

    fun setData(newCategoryList : MutableList<CategoryEntry>){
        if(categories == newCategoryList) return
        categories = newCategoryList
        updateCategoriesLiveData()
    }

    override fun isThisItemUsed(item: CategoryEntry): Boolean {
        val index = trains.indexOfFirst { it.categoryName == item.categoryName }
        return (index != -1)
    }
}