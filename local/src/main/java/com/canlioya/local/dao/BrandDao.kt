package com.canlioya.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.canlioya.local.entities.BrandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandDao : BaseDao<BrandEntity> {

    @Query("SELECT * FROM brands ORDER BY brandName")
    fun observeAllBrands() : Flow<List<BrandEntity>>

    @Query("SELECT * FROM brands ORDER BY brandName")
    suspend fun getBrandList() : List<BrandEntity>

    @Query("SELECT brandName FROM brands ORDER BY brandName")
    suspend fun getBrandNames() : List<String>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun observeChosenBrand(id: Int): Flow<BrandEntity>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    suspend fun getChosenBrand(id: Int): BrandEntity
}
