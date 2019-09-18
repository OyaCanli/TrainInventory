package com.canli.oya.traininventoryroom.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.CategoryEntry
import io.reactivex.Flowable

@Dao
interface CategoryDao : BaseDao<CategoryEntry> {

    @get:Query("SELECT * FROM categories")
    val allCategories: Flowable<List<String>>

    @Query("SELECT * FROM categories")
    suspend fun getCategoryList() : List<String>

}
