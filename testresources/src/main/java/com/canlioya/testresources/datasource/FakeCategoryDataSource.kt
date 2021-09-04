package com.canlioya.testresources.datasource


import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeCategoryDataSource(private var categories : MutableList<Category> = sampleCategoryList,
                             private val trains: MutableList<Train> = mutableListOf()
) : IBrandCategoryDataSource<Category> {


    override suspend fun insertItem(item: Category) {
        categories.add(item)
    }

    override suspend fun deleteItem(item: Category) {
        categories.remove(item)
    }

    override suspend fun updateItem(item: Category) {
        val index = categories.indexOfFirst { it.categoryId == item.categoryId }
        categories[index] = item
    }

    fun setData(newCategoryList : MutableList<Category>){
        if(categories == newCategoryList) return
        categories = newCategoryList
    }

    override suspend fun isThisItemUsed(item: Category): Boolean {
        val index = trains.indexOfFirst { it.categoryName == item.categoryName }
        return index != -1
    }

    override fun getAllItems(): Flow<List<Category>> = flow {
        emit(categories)
    }

    override suspend fun getItemNames(): List<String> {
        return categories.map { it.categoryName }
    }

    override suspend fun isThisItemUsedInTrash(item: Category): Boolean {
        val trainsInTrash = trains.filter {
            it.dateOfDeletion != null
        }
        //Search brand in trash folder
        val index = trainsInTrash.indexOfFirst { it.categoryName == item.categoryName }
        return index != -1
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: Category) {
        trains.removeAll {
            it.dateOfDeletion != null && it.categoryName == item.categoryName
        }
    }
}