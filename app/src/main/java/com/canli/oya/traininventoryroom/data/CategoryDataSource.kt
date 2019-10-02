package com.canli.oya.traininventoryroom.data

class CategoryDataSource(val database: TrainDatabase) {

    fun getAllCategories() = database.categoryDao().allCategories

    suspend fun insertCategory(category: CategoryEntry) {
        database.categoryDao().insert(category)
    }

    suspend fun deleteCategory(category: CategoryEntry) {
        database.categoryDao().delete(category)
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return database.trainDao().isThisCategoryUsed(category)
    }

    suspend fun updateCategory(category: CategoryEntry) {
        database.categoryDao().update(category)
    }
}