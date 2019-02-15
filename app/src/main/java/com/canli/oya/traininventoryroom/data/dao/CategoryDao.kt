package com.canli.oya.traininventoryroom.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.canli.oya.traininventoryroom.data.CategoryEntry

@Dao
interface CategoryDao {

    @get:Query("SELECT * FROM categories")
    val allCategories: LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: CategoryEntry)

    @Delete
    fun deleteCategory(category: CategoryEntry)

}
