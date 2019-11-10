package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.BrandEntry

@Dao
interface BrandDao : BaseDao<BrandEntry> {

    @get:Query("SELECT * FROM brands")
    val allBrands: DataSource.Factory<Int, BrandEntry>

    @Query("SELECT * FROM brands")
    suspend fun getBrandList() : List<BrandEntry>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun getChosenBrand(id: Int): LiveData<BrandEntry>
}
