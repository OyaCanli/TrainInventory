package com.canli.oya.traininventoryroom.data

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCategoryDataSource(private var categories : MutableList<CategoryEntry> = sampleCategoryList,
                             private val trains: List<TrainEntry> = listOf()) : IBrandCategoryDataSource<CategoryEntry> {

    private val categoryFlow : Flow<PagingData<CategoryEntry>> = flow {
        emit(PagingData.from(categories))
    }

    override suspend fun insertItem(item: CategoryEntry) {
        categories.add(item)
    }

    override suspend fun deleteItem(item: CategoryEntry) {
        categories.remove(item)
    }

    override suspend fun updateItem(item: CategoryEntry) {
        val index = categories.indexOfFirst { it.categoryId == item.categoryId }
        categories[index] = item
    }

    override fun getAllPagedItems(): Flow<PagingData<CategoryEntry>> = categoryFlow

    fun setData(newCategoryList : MutableList<CategoryEntry>){
        if(categories == newCategoryList) return
        categories = newCategoryList
    }

    override fun isThisItemUsed(item: CategoryEntry): Boolean {
        val index = trains.indexOfFirst { it.categoryName == item.categoryName }
        return (index != -1)
    }

    override fun getAllItems(): Flow<List<CategoryEntry>> = flow {
        emit(categories)
    }
}