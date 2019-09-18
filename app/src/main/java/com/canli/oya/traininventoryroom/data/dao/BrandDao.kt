package com.canli.oya.traininventoryroom.data.dao

import androidx.room.*
import com.canli.oya.traininventoryroom.data.BrandEntry
import io.reactivex.Flowable

@Dao
interface BrandDao {

    @get:Query("SELECT * FROM brands")
    val allBrands: Flowable<List<BrandEntry>>

    @Query("SELECT * FROM brands")
    suspend fun getBrandList() : List<BrandEntry>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun getChosenBrand(id: Int): Flowable<BrandEntry>

    @Insert
    suspend fun insertBrand(brand: BrandEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBrandInfo(brand: BrandEntry)

    @Delete
    suspend fun deleteBrand(brand: BrandEntry)
}
