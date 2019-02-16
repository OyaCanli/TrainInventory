package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.canli.oya.traininventoryroom.data.BrandEntry

@Dao
interface BrandDao {

    @get:Query("SELECT * FROM brands")
    val allBrands: LiveData<List<BrandEntry>>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun getChosenBrand(id: Int): LiveData<BrandEntry>

    @Insert
    suspend fun insertBrand(brand: BrandEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBrandInfo(brand: BrandEntry)

    @Delete
    suspend fun deleteBrand(brand: BrandEntry)
}
