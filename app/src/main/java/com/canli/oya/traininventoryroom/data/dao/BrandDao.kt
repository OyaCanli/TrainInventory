package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.BrandEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandDao : BaseDao<BrandEntry> {

    @Query("SELECT * FROM brands ORDER BY brandName")
    fun observeAllPagedBrands(): PagingSource<Int, BrandEntry>

    @Query("SELECT * FROM brands ORDER BY brandName")
    fun observeAllBrands() : Flow<List<BrandEntry>>

    @Query("SELECT * FROM brands ORDER BY brandName")
    suspend fun getBrandList() : List<BrandEntry>

    @Query("SELECT brandName FROM brands ORDER BY brandName")
    suspend fun getBrandNames() : List<String>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun observeChosenBrand(id: Int): Flow<BrandEntry>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    suspend fun getChosenBrand(id: Int): BrandEntry
}
