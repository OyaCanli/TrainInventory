package com.canli.oya.traininventoryroom.data.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.CategoryEntry

@Dao
interface CategoryDao : BaseDao<CategoryEntry> {

    @get:Query("SELECT * FROM categories")
    val allCategories: DataSource.Factory<Int, CategoryEntry>

}
