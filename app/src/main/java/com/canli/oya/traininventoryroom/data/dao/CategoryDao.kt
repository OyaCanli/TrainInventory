package com.canli.oya.traininventoryroom.data.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.CategoryEntry

@Dao
interface CategoryDao : BaseDao<CategoryEntry> {

    @Query("SELECT * FROM categories")
    fun observeAllCategories(): DataSource.Factory<Int, CategoryEntry>

    @Query("SELECT * FROM categories")
    suspend fun getCategoryList() : List<CategoryEntry>

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getChosenCategory(id: Int): CategoryEntry
}
