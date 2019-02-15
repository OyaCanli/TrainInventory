package com.canli.oya.traininventoryroom.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.canli.oya.traininventoryroom.data.BrandEntry

@Dao
interface BrandDao {

    @get:Query("SELECT * FROM brands")
    val allBrands: LiveData<List<BrandEntry>>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun getChosenBrand(id: Int): LiveData<BrandEntry>

    @Insert
    fun insertBrand(brand: BrandEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateBrandInfo(brand: BrandEntry)

    @Delete
    fun deleteBrand(brand: BrandEntry)
}
