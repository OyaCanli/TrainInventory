package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.CategoryEntry

interface ICategoryDataSource {

    fun getAllCategories() : LiveData<PagedList<CategoryEntry>>

    suspend fun insertCategory(category: CategoryEntry)

    suspend fun deleteCategory(category: CategoryEntry)

    fun isThisCategoryUsed(category: String): Boolean

    suspend fun updateCategory(category: CategoryEntry)
}