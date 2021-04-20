package com.canli.oya.traininventoryroom.datasource

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeCategoryDataSource(private var categories : MutableList<CategoryEntry> = sampleCategoryList,
                                                 private val trains: MutableList<TrainEntry> = mutableListOf()
) : IBrandCategoryDataSource<CategoryEntry> {

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

    override suspend fun isThisItemUsed(item: CategoryEntry): Int? {
        val index = trains.indexOfFirst { it.categoryName == item.categoryName }
        return if(index == -1) null else 1
    }

    override fun getAllItems(): Flow<List<CategoryEntry>> = flow {
        emit(categories)
    }

    override suspend fun getItemNames(): List<String> {
        return categories.map { it.categoryName }
    }

    override suspend fun isThisItemUsedInTrash(item: CategoryEntry): Int? {
        val trainsInTrash = trains.filter {
            it.dateOfDeletion != null
        }
        //Search brand in trash folder
        val index = trainsInTrash.indexOfFirst { it.categoryName == item.categoryName }
        return if (index == -1) null else return 1
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: CategoryEntry) {
        trains.removeAll {
            it.dateOfDeletion != null && it.categoryName == item.categoryName
        }
    }
}