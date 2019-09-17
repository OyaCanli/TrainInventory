package com.canli.oya.traininventoryroom.data.repositories

import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.datasources.CategoryDataSource

class CategoryRepository(private val categoryDataSource: CategoryDataSource) {

    fun getAllCategories() = categoryDataSource.getAllCategories()

    suspend fun getCategoryList() = categoryDataSource.getCategoryList()

    suspend fun insertCategory(category: CategoryEntry) {
        categoryDataSource.insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntry) {
        categoryDataSource.deleteCategory(category)
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return categoryDataSource.isThisCategoryUsed(category)
    }
}
