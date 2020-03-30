package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.BrandEntry

@Dao
interface BrandDao : BaseDao<BrandEntry> {

    @Query("SELECT * FROM brands")
    fun observeAllBrands(): DataSource.Factory<Int, BrandEntry>

    @Query("SELECT * FROM brands")
    suspend fun getBrandList() : List<BrandEntry>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun observeChosenBrand(id: Int): LiveData<BrandEntry>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    suspend fun getChosenBrand(id: Int): BrandEntry
}
