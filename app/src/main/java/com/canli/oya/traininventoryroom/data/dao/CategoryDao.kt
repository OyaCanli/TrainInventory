package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.canli.oya.traininventoryroom.data.CategoryEntry

@Dao
interface CategoryDao {

    @get:Query("SELECT * FROM categories")
    val allCategories: LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntry)

    @Delete
    suspend fun deleteCategory(category: CategoryEntry)

}
