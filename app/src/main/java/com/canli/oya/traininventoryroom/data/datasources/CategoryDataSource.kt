package com.canli.oya.traininventoryroom.data.datasources

import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase

class CategoryDataSource(val database: TrainDatabase) {

    fun getAllCategories() = database.categoryDao().allCategories

    suspend fun getCategoryList() = database.categoryDao().getCategoryList()

    suspend fun insertCategory(category: CategoryEntry) {
        database.categoryDao().insert(category)
    }

    suspend fun deleteCategory(category: CategoryEntry) {
        database.categoryDao().delete(category)
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return database.trainDao().isThisCategoryUsed(category)
    }
}