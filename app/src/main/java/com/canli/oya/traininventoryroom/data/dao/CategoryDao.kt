package com.canli.oya.traininventoryroom.data.dao

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.CategoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<CategoryEntry> {

    @Query("SELECT * FROM categories")
    fun observeAllPagedCategories(): PagingSource<Int, CategoryEntry>

    @Query("SELECT * FROM categories")
    fun observeAllCategories(): Flow<List<CategoryEntry>>

    @Query("SELECT * FROM categories")
    suspend fun getCategoryList() : List<CategoryEntry>

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getChosenCategory(id: Int): CategoryEntry
}
