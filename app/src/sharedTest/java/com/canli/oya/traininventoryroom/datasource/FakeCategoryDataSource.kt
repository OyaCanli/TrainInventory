package com.canli.oya.traininventoryroom.datasource

import com.canli.oya.traininventoryroom.data.CategoryEntity
import com.canli.oya.traininventoryroom.data.TrainEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeCategoryDataSource(private var categories : MutableList<CategoryEntity> = sampleCategoryList,
                             private val trains: MutableList<TrainEntity> = mutableListOf()
) : IBrandCategoryDataSource<CategoryEntity> {


    override suspend fun insertItem(item: CategoryEntity) {
        categories.add(item)
    }

    override suspend fun deleteItem(item: CategoryEntity) {
        categories.remove(item)
    }

    override suspend fun updateItem(item: CategoryEntity) {
        val index = categories.indexOfFirst { it.categoryId == item.categoryId }
        categories[index] = item
    }

    fun setData(newCategoryList : MutableList<CategoryEntity>){
        if(categories == newCategoryList) return
        categories = newCategoryList
    }

    override suspend fun isThisItemUsed(item: CategoryEntity): Int? {
        val index = trains.indexOfFirst { it.categoryName == item.categoryName }
        return if(index == -1) null else 1
    }

    override fun getAllItems(): Flow<List<CategoryEntity>> = flow {
        emit(categories)
    }

    override suspend fun getItemNames(): List<String> {
        return categories.map { it.categoryName }
    }

    override suspend fun isThisItemUsedInTrash(item: CategoryEntity): Int? {
        val trainsInTrash = trains.filter {
            it.dateOfDeletion != null
        }
        //Search brand in trash folder
        val index = trainsInTrash.indexOfFirst { it.categoryName == item.categoryName }
        return if (index == -1) null else return 1
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: CategoryEntity) {
        trains.removeAll {
            it.dateOfDeletion != null && it.categoryName == item.categoryName
        }
    }
}