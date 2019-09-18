package com.canli.oya.traininventoryroom.data.dao

import androidx.room.*
import com.canli.oya.traininventoryroom.data.CategoryEntry
import io.reactivex.Flowable

@Dao
interface CategoryDao {

    @get:Query("SELECT * FROM categories")
    val allCategories: Flowable<List<String>>

    @Query("SELECT * FROM categories")
    suspend fun getCategoryList() : List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntry)

    @Delete
    suspend fun deleteCategory(category: CategoryEntry)

}
