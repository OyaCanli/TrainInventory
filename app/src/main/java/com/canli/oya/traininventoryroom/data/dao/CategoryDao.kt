package com.canli.oya.traininventoryroom.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<CategoryEntity> {

    @Query("SELECT * FROM categories ORDER BY categoryName")
    fun observeAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY categoryName")
    suspend fun getCategoryList() : List<CategoryEntity>

    @Query("SELECT categoryName FROM categories ORDER BY categoryName")
    suspend fun getCategoryNames() : List<String>

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getChosenCategory(id: Int): CategoryEntity
}
